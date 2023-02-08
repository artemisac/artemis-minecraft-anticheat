<p align="center">
  <img width="75%" src="https://i.imgur.com/gpjLEmU.png" alt="Artemis">
  <br>
  <a href="https://github.com/artemisac/artemis-spigot-sdk/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/artemisac/artemis-spigot-sdk"></a>
  <a href="https://github.com/artemisac/artemis-spigot-sdk/network"><img alt="GitHub forks" src="https://img.shields.io/github/forks/artemisac/artemis-spigot-sdk"></a>
  <a href="https://github.com/artemisac/artemis-spigot-sdk/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/artemisac/artemis-spigot-sdk"></a>
</p>

## What is Artemis?

What is Artemis? Well... Imagine this: You run a Minecraft server. A cheater comes online. You're not online. 
So he proceeds to piss off your players and gain a competitive advantage. How to solve this? Very simple! 
You need an anticheat. However anticheats are tedious. This, by far to my belief, is the best solution to have been 
provided online.

## How do I use this API?

First and foremost you'll want to use Jitpack for maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

[...]


<dependency>
    <groupId>com.github.artemisac</groupId>
    <artifactId>artemis-spigot-sdk</artifactId>
    <version>1.0.2</version>
    <scope>compile</scope>
</dependency>
```



Integrating this must be a pain! Nope! It's simple! You'll encounter the following code in the NetworkManager.java class in NMS (1.8.8 example)
```java
  private void m() {
        if (this.channel != null && this.channel.isOpen()) {
            this.j.readLock().lock();

            try {
                while (!this.i.isEmpty()) {
                    NetworkManager.QueuedPacket networkmanager_queuedpacket = (NetworkManager.QueuedPacket) this.i.poll();

                    this.a(networkmanager_queuedpacket.a, networkmanager_queuedpacket.b);
                }
            } finally {
                this.j.readLock().unlock();
            }

        }
    }
    
```

Here's how to integrate the Flushing API:

```java
  private void m() {
      if (this.channel != null && this.channel.isOpen()) {
            final boolean flushApi = FlushAPI.getApi() != null 
                        && this.m instanceof PlayerConnection
                        && ((PlayerConnection) m).getPlayer() != null;
        
            if (flushApi) {
                  FlushAPI.getApi().callPre(((PlayerConnection) m).getPlayer().getUniqueId());
            }
            this.j.readLock().lock();

            try {
                while (!this.i.isEmpty()) {
                    NetworkManager.QueuedPacket networkmanager_queuedpacket = (NetworkManager.QueuedPacket) this.i.poll();

                    this.a(networkmanager_queuedpacket.a, networkmanager_queuedpacket.b);
                }
            } finally {
                this.j.readLock().unlock();
            }
            
            if (flushApi) {
                  FlushAPI.getApi().callPost(((PlayerConnection) m).getPlayer().getUniqueId());
            }

        }
    }
```
