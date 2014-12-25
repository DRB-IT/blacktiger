blacktiger
==========

Server implementation for blacktiger.

## Build project

__Preresequites__
* Java
* Maven

Start by cloning the project:
```
git clone https://github.com/DRB-IT/blacktiger.git
```

> Somethin about astarisk-java

Then build it with maven:
```
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
