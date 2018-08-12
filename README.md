Status
====================

![Build Status](https://travis-ci.org/MaridProject/marid.svg?branch=master)

Summary
====================

Marid is a free software intended to build complex automated systems without writing code. How is it possible? The software written in Java and consists of three parts:

    1. Runtime
    2. IDE
    3. Repository

The runtime allows you to run a set of services linked and configured in the IDE as a project.
The IDE allows you to connect services one to each other according to the type information of inputs and outputs. Typically an input could be a parameter of a constructor or a method of the service class. Depending of types of passed arguments the target service will obtain a concrete type so that all its outputs will be resolved by this type too. An output could be a getter or field or a result of a method whose arguments are fully applied beforehand.

The interesting part of this project is a type-resolving engine.
For example, if you have a service S<A, B>, and S has a method add(A a) and you’ve configured this service so that the method called two times with arguments of Integer[] and String[] then A parameter will be resolved as Serializable[] since Serializable[] is type-compatible with Cloneable, Serializable, Object[] and Object.

Imagine the following situation:

1. You have two devices connected to a computer by different protocols and different connectors (for example USB and HDMI). These two devices are modelled in the IDE as two services with a generic type Device<P, C> where P is a protocol and C is a connector.
2. The connector USB is modelled via a class named USB implementing interfaces Detachabe and Configurable and HDMI connector is modelled via a class named HDMI implementing Detachable and Configurable too.
3. You see in the Repository a service named DeviceSet as a container of different devices of your project declared as DeviceSet<C, D extends Device<?, C>>
4. After connecting both of your devices to that DeviceSet the type of the service becomes DeviceSet<?, Detachable & Configurable> meaning that all the devices contained in that container are detachable and configurable simultaneously.
5. You see in the Repository a service named Detacher that can detach a set of detachable devices by a signal. It has an input ‘detachables’ of type DeviceSet<? extends Detachable, ?>. So, you can connect your service DeviceSet<?, Detachable & Configurable> to that service thanks to type compatibility and automatically inferred types

The type inferring engine is more complex than in Java language. It can infer union and intersection types like Ceylon-language compiler does. Since then Java runtime generics can (surprising) express that types, there was nothing to do but to write code to resolve these generics in runtime (needed by IDE to check service I/O compatibility and by runtime to resolve one of the overriden methods to call).

So the creating of a typical project is something like that:

(1) To find appropriate libraries from the Repository and to add them to the project
(2) To add services from these libraries, to configure them and to connect them one to each other
(3) To build the project resulting into a binary that can be deployed to a target server or controller
(4) To deploy the project to the target equipment

Usage
====================

| Artifact | Link  |
|----------|------:|
| marid-ide | [![Maven](http://img.shields.io/maven-central/v/org.marid/marid-ide.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.marid/marid-ide) |
| marid-runtime | [![Maven](http://img.shields.io/maven-central/v/org.marid/marid-runtime.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.marid/marid-runtime) |
| marid-fx | [![Maven](http://img.shields.io/maven-central/v/org.marid/marid-fx.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.marid/marid-fx) |
| marid-util | [![Maven](http://img.shields.io/maven-central/v/org.marid/marid-util.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.marid/marid-util) |
