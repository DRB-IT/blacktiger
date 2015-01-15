blacktiger
==========

Server implementation for blacktiger.

# Build project

## Preresequites
* Java
* Maven

In order to build and run blacktiger locally you need to follow these few steps.

## 1: Prepare asterisk-java
Unfortunately the asterisk-java project is not available in Maven Central. Therefore it is required to build this project locally. See [https://github.com/srt/asterisk-java](https://github.com/srt/asterisk-java) for more information.

## 2: Prepare Peers
Unfortunately the Peers project is not available in Maven Central. Therefore it is required to build this project locally.

```
git clone https://github.com/ymartineau/peers.git
cd peers
mvn install
```

## 3: Then clone and build blacktiger:
```
git clone https://github.com/DRB-IT/blacktiger.git
cd blacktiger
mvn package
```

## Integrations tests
In order to run integration tests the following needs to be installed locally;
* Virtualbox
* Vagrant 
 
Then you will be able to execute the following:
```
vagrant up
mvn verify
```

To stop the virtual machine again:
```
vagrant halt
```
