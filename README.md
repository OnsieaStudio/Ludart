# Ludart

2D-3D video game framework composed of pre-existing modules themselves based on the LWJGL (which includes OpenGL, Vulkan, NanoVG, OpenAL, GLFW, STB...). They will manage a whole bunch of domains (windowing, rendering, physics, entities, data, networks, game logic etc)

**In progress, the initial sources will arrive and updates will be regular. For now I'm focusing on the architecture (repo, documentations, maven, gradle, ...)**

## License

This project is released under the GNU General Public License (GPL) version 3, available at https://www.gnu.org/licenses/gpl-3.0.html and [copied into](https://github.com/OnsieaStudio/Ludart/blob/main/LICENSE), for more details read https://github.com/OnsieaStudio/Ludart/blob/main/LICENSE

## Branches

feature[temporary]->dev->prerelease->main|maven
![Ludart - Game framework BRANCHS](https://github.com/OnsieaStudio/Ludart/assets/30376290/7746385b-c7e3-43ee-83aa-bc07f3006ecc)



## Contraints

### 1st: “modularity”
- Tools in the form of trees of completely independent modules (parents->children), themselves in "packages" ("folders" of modules of the same theme). Allows you to easily modify, replace, delete them or add without having to modify all the rest of the source code. (Everything fits "automatically" [as long as the implementation is correct]).

- Allows you to download only what you need (via a small script and later a website [a bit like LWJGL does: https://www.lwjgl.org/customize]). To include only the necessary elements in our final "binary" and therefore to save disk space, memory, a little performance etc.

- Allows you to make a game easily modifiable (modding)

- This architecture of independent modules improves stability with multithreading. A module can be on a separate thread and communicate with others via a packet system.

### 2ᵉ: “packets”
- Packet : snapshots resulting from the iteration of a module, involving differences with the previous state. [Example: entity module, put the new positions of entities that moved during the iteration]

- If the packet's destination module is not present, the packet returns to the sender (or is not created/filled/sent at all).

- Endpoint/conversion logic; as soon as a module receives a packet, it uses it to create a "converted" (easier to use in its domain, keeping what interests it and rendering the source packet instance more quickly [using the convert in place]).

- The use of a received packet is recursive. A parent module can pass it to child modules and so on, if necessary. As soon as all modules no longer use the packet, it returns to the sender.

- Allows you to synchronize the "speed" of threads in addition to time limitation systems. If a module can fill X packets maximum, as long as the destination module(s) do not return it, it doesn't fill in any others and therefore waits. This makes it possible to make up for the delay of some while slowing down the advance of others. Offer additional control (example: allowing a module to create infinite packets or limiting it, setting different limits from one module to another)

- The packet system simplifies the sending of strictly necessary information, no more, no less, over the network

### 3ᵉ: "optimization"
Most games are not sufficiently optimized, although they could be even if it takes a long time. The machine is never used to its full capacity (example: a game using only 'a single core on a processor with 32...), the goal is to make optimizations easy or even automatic)

- Meta-optimization: optimization of the system of modules, which thanks to its architecture allows in turn to optimize certain actions carried out by them.

- Optimize all module actions using a whole bunch of more or less innovative strategies, making them multithreadable...

### Example :
- PACKAGE		; includes “<...>” modules
- CORE			; "physics, entities, logic mathematics"
- GAME			; "world, chunks"
- CLIENT 		; "window, rendering, user inputs and outputs"

Each package -> thread separately.
8 maximum packets [transmissible/receivable] per module pair endpoint (8 CLIENT <-> CORE, 8 CLIENT <-> GAME, 8 GAME <-> CORE).

CLIENT receives user input/output -> transmits a packet to GAME -> converts and returns packet -> loads world and entities -> transmits a packet to CORE -> converts and returns packet -> collision calculation, entity movement, if multiplayer transmits on the network -> transmits a packet to CLIENT -> conversion and renders packet -> displays.

This avoids blocking the display, for example as long as the generation of the world is not completely finished or the action of an entity's AI is still being developed.
