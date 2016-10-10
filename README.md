# etcd-java
![alt tag](https://github.com/cdancy/etcd/blob/master/logos/etcd-horizontal-color.png)

Java implementation of Etcd v2. 

## Status
<a href='https://bintray.com/cdancy/java-libraries/etcd-java/view?source=watch' alt='Get automatic notifications about new "etcd-java" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>
[ ![Download](https://api.bintray.com/packages/cdancy/java-libraries/etcd-java/images/download.png) ](https://bintray.com/cdancy/java-libraries/etcd-java/_latestVersion)
[![Build Status](https://travis-ci.org/cdancy/etcd-java.svg?branch=master)](https://travis-ci.org/cdancy/etcd-java)

## Setup

Start server like so:

      EtcdJava etcd = new EtcdJava();
      etcd.start();

      // do some work...

      etcd.stop();
      
## Latest release

Can be sourced from jcenter like so:

	<dependency>
	  <groupId>com.cdancy</groupId>
	  <artifactId>etcd-java</artifactId>
	  <version>0.0.1</version>
	  <classifier>sources|javadoc|all</classifier> (Optional)
	</dependency>
	
## Documentation

javadocs can be found via [github pages here](http://cdancy.github.io/etcd-java/docs/javadoc/)

## Components

- [netty-http](https://github.com/caskdata/netty-http) \- used for client communication
- [atomix-copycat](https://github.com/atomix/copycat) \- used for peer communication
    
## Testing

Running mock tests can be done like so:

	./gradlew clean build mockTest
	
Running integration tests can be done like so (requires docker):

	./gradlew clean build integTest
	
Running integration tests without invoking docker can be done like so:

	./gradlew clean build integTest -PbootstrapDocker=false -PtestEtcdEndpoint=http://127.0.0.1:2379 

## Additional Resources

* [Etcd proto API](https://github.com/coreos/etcd/blob/master/etcdserver/etcdserverpb/rpc.proto)
* [Etcd grpc-to-json gateway](https://github.com/coreos/etcd/blob/master/Documentation/dev-guide/api_grpc_gateway.md)
* [Etcd grpc-to-json swagger](https://github.com/coreos/etcd/blob/master/Documentation/dev-guide/apispec/swagger/rpc.swagger.json)
* [Etcd REST API](https://github.com/coreos/etcd/blob/master/Documentation/api.md)
* [Etcd Auth API](https://github.com/coreos/etcd/blob/master/Documentation/auth_api.md)

