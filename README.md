# Turning Bodies


a study of Flow Fields and their applications in generative art

Inpsired by Daniel Shiffman's The Nature of Code, focusing on chapter 6. Autonomous Agents.

http://natureofcode.com/book/chapter-6-autonomous-agents/

This sketch is a study of Flow Fields, an algorithm devised by Craig Reynolds, demonstrated by Shiffman.

http://www.red3d.com/cwr/steer/FlowFollow.html

This sketch uses a class 'Worm', a bezier curved with emulated motion through sin movement. A flowfield is generated which determines what direction the worm will head in. For each given point on the canvas, a vector points the worm in the desired direction. When many worms are added, they all scramble to head in the right direction.

The canvs size of 6000 x 3375 pixels results in a very very slow (but remarkable) output. To experience the worms in realtime please change the canvas size to 1920 x 1080, keeping in mind all of my variables are finely tuned to the larger canvas size.

A new FlowField is generated every now and then, to give the worms a new purpose in life.

Colour is determined by both position along the x axis, as well as the direction the worm is heading!

The resulting image is limited to a number of frames, which could be increased or decreased to scale complexity.

To exit the program and save an image befor the full frame count has been reached, press 's'.
