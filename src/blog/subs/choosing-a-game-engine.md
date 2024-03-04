---
title: "Choosing a Game Engine"
date: 2023-03-18 12:50
description: "Explore the various challenges of making a multiplayer game and how to make the most important decision: choosing the engine."
tags:
- Unity
- Game Dev
- Subs:CE
---

<PageHeader/>

# Choosing a Game Engine

After using Unity as the game engine for a hobby project, Subterfuge: Community Edition for a few years, I've been able to try it out and have some insights on the engine.
However, before we jump into a post about the pros and cons of various game engines, let's talk about multiplayer games, how they work, and what is required by a game engine to support multiplayer.

# Some Background about the game

The game I am working on creating is a real-time multiplayer strategy game that plays out over a week.
Players can schedule attacks and prepare events to occur in the future to catch people offgaurd or supply reinforcements once troops are ready.
Because there is minimal real-time gameplay, the game can easily be power by REST APIs and a simple database for player and game data.

# Considerations for Multiplayer Games

Writing a multiplayer game requires you to have a server to allow game data to be shared between players so that they can play the game together.
In order to do this, a server runs the game's code and broadcasts the current game's data to each player in the game.
Traditionally, servers will only send the current game state to every listening player.
However, by only sending the current state, clients (player devices) are unable to reason about the future and are only able to see what is happening in the game "right now".
In Subterfuge, being able to predict the future is a key element of the game.
In order for players to be able to see into the game's future, each player needs to be able to simulate and run the game's core logic on their devices!
This is nothing new, clients will usually contain all of the game logic so that players experience the game as the developers want them to.

However, in order to create a fair multiplayer exierience and ensure that hackers are not maliciously altering the game, we need our server to do a little work.
The previous implementation of Subterfuge used a ["dumb server"](https://blog.subterfuge-game.com/post/111303603036/developing-a-live-game), as the developers called it.
Essentially, the server would accept events from everyone and not validate if the event is actually possible or not.
You could even submit events, like sending private chat messages, or even controlling the actions of other players! Yikes!

In order to avoid this, the server needs to have some knowledge about the game logic too.
Ideally, both the server and the client can run the same game logic.
If the server and client are written in two different languages, that means all of the game logic implemented on one side (ie. the client), would need to get replicated into the server. That is a lot of duplication and it doesn't really make sense to write the same code twice...

# Choosing the engine

Given our requirements to create a multiplayer game without wanting to re-write the code, we had the following options:

1. Write code for the server, and then re-write the code for the client.
2. Find a game-engine that can share code between a server and client.

Ideally, I didn't want to write code twice so I was able to narrow my selection down to three options.

### Unreal Engine

[Unreal Engine](https://www.unrealengine.com/en-US) is a game engine that is written in C++ and would allow 'sharing' code between a server and client.
Unfortunately, I don't have much experience with C++.
As much as I would like to learn C++ a bit more, I didn't want to waste time trying to debug problems with the development environment, and therefore I opted not to use Unreal.

### Godot

[Godot](https://godotengine.org/) is a new game engine that can be used for both 2D and 3D games.
Godot is extremely appealing to developers because it is a completely open-source and free game engine that is able to compete with AAA engines.
Godot uses a language called GDScript which is very similar to python.
At the time of writing this blog, Godot's C# implementation is very experimental, and I am uncertain if GDScript supports both client and server implementations.

::: tip Editor's note
It turns out that [GDScript does support dedicated servers](https://docs.godotengine.org/en/stable/tutorials/export/exporting_for_dedicated_servers.html). However, at the time of writing, the documentation was fairly young and I wasn't sure if it would support everything I needed.
For example, there was no information about library support or how to connect my GDScript server to a database.</p>

<hr/>

That being said, Godot has been maturing rapidly and I would give it a shot if you are interested in it!
:::

### Unity

The last game engine in consideration is Unity.
Unity is written in C# which is a language that is very similar to Java.
Additionally, C# has library support and frameworks that are heavily supported by Microsoft like ASP.NET which allows easily creating a REST API and server backend.
Using C#, I can also write a core library, and export that library as a `.dll`.
That dll can then be used in both my backend ASP.NET server, as well as within the Unity client.

# Reflecting

Using Unity has allowed us to only write core game logic once which has saved significant development time.
At the end of the day, your game's requirements might not be the same.
When choosing an engine, be sure to consider your game's requirements and select the engine that best supports them to create the game of your dreams.