/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.etcdjava;

import com.cdancy.etcdjava.model.members.Member;
import com.cdancy.etcdjava.model.keys.Key;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.local.LocalServerRegistry;
import io.atomix.catalyst.transport.local.LocalTransport;
import io.atomix.collections.DistributedMap;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import io.atomix.group.DistributedGroup;
import io.atomix.group.LocalMember;
import io.atomix.group.messaging.Message;
import io.atomix.group.messaging.MessageConsumer;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java impl of Etcd peer server.
 * 
 * @author cdancy
 */
public final class PeerServer {
    
    private static final Logger logger = LoggerFactory.getLogger(PeerServer.class);

    public static final String DEFAULT_PEER_HOST = "127.0.0.1";
    public static final int DEFAULT_PEER_PORT = 2380;
    private static volatile Address address;
    private volatile LocalMember localMember = null;
    
    // local cache of Member(s) and their metadata
    private static final Map<String, Member> membersCache = new ConcurrentHashMap<>();
    
    private volatile AtomixReplica atomixReplica;
    private File logsDirectory = new File(System.getProperty("user.dir") + "/default.etcd");
    private static volatile DistributedMap<String, List<Key>> distributedMap = null;
    private static volatile DistributedGroup distributedGroup = null;
    private final String name = System.getProperty("name") + "-peer";
    
    public PeerServer() {
        address = new Address(DEFAULT_PEER_HOST, DEFAULT_PEER_PORT);
    }
    
    public PeerServer(int port) {
        address = new Address(DEFAULT_PEER_HOST, port);
    }
    
    public PeerServer(String host, int port) {
        address = new Address(host, port);
    }
    
    protected void start() {
        initServer();
        atomixReplica.bootstrap().join();
        distributedMap = atomixReplica.<String, List<Key>>getMap(name() + "-map").join();
        
        try {
            distributedGroup = atomixReplica.getGroup(name() + "-group").get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw Throwables.propagate(e);
        } 
        
        // Join this instance to the distributed group and register a consumer for incoming messages
        distributedGroup.join().thenAccept(member -> {
            localMember = member;
            MessageConsumer<Object> consumer = member.messaging().consumer("etcd-java-messages");
            consumer.onMessage((Message<Object> possibleMessage) -> {
                if (possibleMessage.message() instanceof Member) {
                    final Member receivedMember = (Member)possibleMessage.message();
                    membersCache.put(receivedMember.id, receivedMember);
                    logger.info("Member has joined cluster: id=" + receivedMember.id);
                    possibleMessage.ack();
                } else {
                    logger.error("Unrecognized message type: message=" + possibleMessage.message().toString());
                    possibleMessage.fail();
                }
            });
        });

        // Notify distributed group of newly added member. Group is already technically aware of an
        // added member but only of its ID. Here we are passing meta information about the member to be 
        // eventually served by our ClientServer.
        // 
        // TODO: There is probably a better, more "atomix", way to do this.
        distributedGroup.onJoin(member -> {
            Member memberToAdd = new Member();
            memberToAdd.id = member.id();
            memberToAdd.name = name();
            memberToAdd.clientURLs = Lists.newArrayList("http:/" + ClientServer.address().toString());
            memberToAdd.peerURLs = Lists.newArrayList("http:/" + PeerServer.address().toString());
            member.messaging().producer("etcd-java-messages").send(memberToAdd);
        });
        
        distributedGroup.onLeave(member -> {
            Member possibleMember = membersCache.remove(member.id());
            if (possibleMember != null) {
                logger.info("Member has left cluster: id=" + member.id());
            } else {
                logger.error("Failed removing member from cluster: id=" + member.id());               
            }
        });
    }
    
    protected void stop() {
        try {
            localMember.leave().get(1, TimeUnit.MINUTES);
            atomixReplica.shutdown().get(1, TimeUnit.MINUTES);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }
    
    private void initServer() {
        atomixReplica = AtomixReplica.builder(address)
                .withTransport(new LocalTransport(new LocalServerRegistry()))
                .withStorage(Storage.builder()
                    .withDirectory(logsDirectory)
                    .withStorageLevel(StorageLevel.DISK)
                    .withMinorCompactionInterval(Duration.ofSeconds(30))
                    .withMajorCompactionInterval(Duration.ofMinutes(1))
                    .withMaxSegmentSize(1024 * 1024 * 8)
                    .withMaxEntriesPerSegment(1024 * 8)
                    .build())
                .build();
        atomixReplica.serializer().disableWhitelist();
    }
        
    private String name() {
        return name;
    }
    
    public static DistributedMap<String, List<Key>> distributedMap() {
        return distributedMap;
    }
    
    public static Address address() {
        return address;
    }
    
    public static Map<String, Member> members() {
        return membersCache;
    }
}
