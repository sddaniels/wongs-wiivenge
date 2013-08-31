Wong's Wiivenge
===============

![Gameplay Screenshot](https://raw.github.com/sddaniels/Wongs-Wiivenge/master/screenshot.jpg "Gameplay Screenshot")

This was the final project for two classes in my last year of college at Iowa State: 

* Cpr E/ME 557 - Computer Graphics and Geometric Modeling
* Com Sci 486 - Fundamental Concepts in Computer Networking 

The game consists of fairly simple lightsaber combat between two players connected over
a network. No matter which player you are, the other guy is always Darth Vader. Whoever
knocks the life bar of the other player down to empty first wins. It's also possible to
dodge and block blows.

We also added Wiimote support (badly!) by modifying some code that we found that would
allow Wiimotes to connect to a Macbook over Bluetooth. There were two basic slashes that
would cause a simulated keypress to swing the lightsaber. This was the origin of the name
of the game, along with the name of our professor Dr. Wong.

The game was written by myself and two other team members from my networking class. I
wrote all of the 3D graphics engine using OpenGL, while my teammates wrote the networking
code and game engine.

The original HTML readme is included here, but I have not included the Wiimote or JOGL
code since I do not own the copyright on those.
