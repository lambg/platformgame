## Program Description:
Our project is a simple 2D platformer. It supports multiple player clients by using one server client. The game has hostile entities and an infinite world. The world will be generate simple terrain and hostile entities as players move to the right. The hostile entities will follow nearby players. Players will take damage from enemies unless the player jumps and lands on an enemy - killing it.

We both enjoy JavaFX and wanted to gain more experience on the topic. We also explored client/server packet interaction as well as serialization.

## Demo video:

https://youtu.be/GQSAAjiR2sw

## Deliverables:
 - Infinite World
 - Entity types:
   - Player
   - HostileEntity
 - Simple platforms which entities can stand on
 - Collision detection
 - Health - entities can die, players will respawn
 - Score tracking (distance traveled)
 - Server & Player clients
 
 ## Plan:

### Week 1:
 - Client/Server classes
 - getNearbyEntities, other world features
 
### Week 2:
 - Send data between client and server
 - Begin work on World (with flat terrain generation, no entity spawning)
 
### Week 3:
 - Create entities, basic collision detection between entities
 - Create Player entity
 
### Week 4:
 - Display player health, score, etc.
 - Generate actual terrain, spawn entities
 
### Week 5:
 - Add platforms, generate platforms in world
 
### Week 6:
 - Polishing/Bug fixing
 
 
## Getting Started:

To install the program, import the project through any IDE of your choice.

To run the program, compile and run MainServer.java for the server, then compile and run MainClient.java for the clients.
    Note: You can connect to a server on a different computer by changing the IP in the MainClient's main method.
    Note: The default port used is 12345.
 
 
## Features:
- Multithreading
- JavaFX
- Custom packet sending
- Supports multiple clients
- Supports hostile entities
- Supports client player respawning
- Supports Health depletion and entity death


## Team Memers:
 Greg Lamb,
 Kevin Thierauf
