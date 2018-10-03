// Danny Keig 2018
// a study of Flow Fields and their applications in generative art

// Inpsired by Daniel Shiffman's The Nature of Code, focusing on chapter 6. Autonomous Agents.
// http://natureofcode.com/book/chapter-6-autonomous-agents/
// This sketch is a study of Flow Fields, an algorithm devised by Craig Reynolds, demonstrated by Shiffman.
// http://www.red3d.com/cwr/steer/FlowFollow.html

// This sketch uses a class 'Worm', a bezier curved with emulated motion through sin movement.
// A flowfield is generated which determines what direction the worm will head in. For each given point on
// the canvas, a vector points the worm in the desired direction.
// When many worms are added, they all scramble to head in the right direction.

// The canvs size of 6000 x 3375 pixels results in a very very slow (but remarkable) output.
// To experience the worms in realtime please change the canvas size to 1920 x 1080, keeping in mind
// all of my variables are finely tuned to the larger canvas size.

// A new FlowField is generated every now and then, to give the worms a new purpose in life.

// Colour is determined by both position along the x axis, as well as the direction the worm is heading!

// The resulting image is limited to a number of frames, which could be increased or decreased to 
// scale complexity.

// to exit the program and save an image befor the full frame count has been reached, press 's'.

import processing.pdf.*;
boolean record;
int frame;

Worm [] worm = new Worm [20];
FlowField flowfield;
float colorAngle;
float heading;
float alpha;
float movStep;
float bezSz;
float maxSz = 300;
float minSz = 50;
float growthFactor;



void setup() {

  //try 1920 x 1080 to experience the worms! Please see note at top.
  //size (1920, 1080);
  size (6000, 3375);
  beginRecord(PDF, "FinalOutput.pdf");
  background(255);
  frame = 0;

  //assign generic starting positions for all the worms!
  for (int i = 0; i < worm.length; i++) {
    worm[i] = new Worm(random(width), random(height));
  }

  flowfield = new FlowField(20);
  noFill();
  colorAngle = 10;
  movStep = 0;
  bezSz = 50;
}

void draw() {

  strokeWeight(1);

  // generate a new FlowField every 2000 frames.
  int newFlow = frame%2000;

  if (newFlow == 0) {
    flowfield.init();
  }

  // for the number of worms in the Worm array, draw worms!
  for (int i = 0; i < worm.length; i ++) {
    //calculate what direction each one is facing
    heading = worm[i].angle(0);

    // alpha value of the colour is determined by direction
    float alpha = map(heading, -2, 4, 150, 15);

    //pass heading value and alpha value to colorLerp() for use in WormClass
    worm[i].colorLerp(heading, alpha);

    //scale the worm size for each one added.
    float scaledBez = map(i, 0, worm.length, 30, 350);
    bezSz = scaledBez;
    worm[i].display(i*0.2);
  }

  for (int i = 0; i < worm.length; i++) {

    //update worm positions
    worm[i].update();

    //make sure the come back onto the canvas
    worm[i].borders();

    //make sure they follow the flowfield!
    worm[i].follow(flowfield);
  }

  // move all worms!
  movStep += 0.1;

  //frame count for keeping track of composition.
  frame++;
  println(frame);

  //exit program at this frame and save the image!
  if (frame == 9500) {
    endRecord();
    exit();
  }
}

// to exit out of the program early and save the result
void keyPressed() {
  if (key == 's') {
    endRecord();
    exit();
  }
}