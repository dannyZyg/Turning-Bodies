import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.pdf.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class autonomousAgentsFlowField extends PApplet {


boolean record;


Vehicle [] vehicle = new Vehicle [20];
FlowField flowfield;
PVector mouse;
float colorAngle;
float test;
float heading;
float alpha;
boolean spaceBar;
float sz = 50;
boolean grow;
boolean debug = false;
float movStep;
float bezSz;



public void setup(){

 beginRecord(PDF, "everything-2018-" + day()+hour()+minute()+second()+ ".pdf");
background(255);


for(int i = 0; i < vehicle.length; i++){
  vehicle[i] = new Vehicle(random(width), random(height));
}


flowfield = new FlowField(20);
noFill();
colorAngle = 10;
spaceBar = false;
sz = 50;
grow = false;
movStep = 0;
bezSz = 50;



}


public void draw(){


strokeWeight(1);
// background(255);

// translate(500,500);
// strokeWeight(5);
// float mov1 = map(sin(movStep ), -1, 1, -sz, sz);
// float mov2 = map(cos(movStep + 0.333 ), -1, 1, -sz, sz);
// bezier(0, 0, mov1, (sz/4)*1, mov2, (sz/4)*3, 0, sz);

mouse = new PVector(mouseX, mouseY);
if(debug) flowfield.display();

alpha = 50;

for(int i = 0; i < vehicle.length; i ++){
heading = vehicle[i].angle(0);
}

colorAngle = map(heading, 0, 5.0f, 0, 1.0f);
int from = color(48, 221, 193, alpha);
int to = color(249, 224, 34, alpha);
int lerped = lerpColor(from, to, colorAngle);

stroke(lerped);

for(int i = 0; i < vehicle.length; i ++){
if(spaceBar) vehicle[i].display2(i*0.2f);
}



if(grow) bezSz += 0.1f;
if(!grow) bezSz -= 0.1f;
if(bezSz < 2) bezSz = 2;

for (int i = 0; i < vehicle.length; i++){
  vehicle[i].update();
  vehicle[i].borders();
  vehicle[i].follow(flowfield);

}

//vehicle.seek(mouse);


movStep += 0.1f;



}


class Vehicle {

PVector location;
PVector velocity;
PVector acceleration;
float maxspeed;
float maxforce;
float r;


Vehicle(float x, float y) {
  acceleration = new PVector(0,0);
  velocity = new PVector(0,0);
  location = new PVector(x,y);
  r = 9.0f;

  maxspeed = 4;
  maxforce = 0.1f;

  }

    // Implementing Reynolds' flow field following algorithm
    // http://www.red3d.com/cwr/steer/FlowFollow.html
  public void follow(FlowField flow) {
    // What is the vector at that spot in the flow field?
    PVector desired = flow.lookup(location);
    // Scale it up by maxspeed
    desired.mult(maxspeed);
    // Steering is desired minus velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    applyForce(steer);
  }

  public void update() {
     velocity.add(acceleration);
     velocity.limit(maxspeed);
     location.add(velocity);
     acceleration.mult(0);
   }

 public void applyForce(PVector force) {
    acceleration.add(force);
  }

public void seek(PVector target) {
    PVector desired = PVector.sub(target,location);
    desired.normalize();
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired,velocity);
    steer.limit(maxforce);
    applyForce(steer);
  }

public float angle(float input){

  input += velocity.heading() + PI/2;
  return input;

  }

  public void display1() {

    float theta = velocity.heading() + PI/2;
    fill(175);
    pushMatrix();
    translate(location.x,location.y);
    rotate(theta);
    beginShape();
    vertex(0, -r*2);
    vertex(-r, r*2);
    vertex(r, r*2);
    endShape(CLOSE);
    popMatrix();

    }

  public void display2(float offset) {

    float theta = velocity.heading() + PI/2;
    //fill(175);
    pushMatrix();
    translate(location.x,location.y);
    rotate(theta);
    // beginShape();
    // vertex(0, -r*2);
    // vertex(-r, r*2);
    // vertex(r, r*2);
    // endShape(CLOSE);
  //  bezier(0, -r*sz, -r, r*sz, r, r*sz, 0, r*sz);
  float mov1 = map(sin(movStep * offset), -1, 1, -bezSz, bezSz);
  float mov2 = map(cos(movStep + 0.333f * offset), -1, 1, -bezSz, bezSz);
  bezier(0, 0, mov1, (bezSz/4)*1, mov2, (bezSz/4)*3, 0, bezSz);




    popMatrix();
    }

    // join canvas edges
    public void borders() {
      if (location.x < -r) location.x = width+r;
      if (location.y < -r) location.y = height+r;
      if (location.x > width+r) location.x = -r;
      if (location.y > height+r) location.y = -r;
    }

}

class FlowField {

  // A flow field is a two dimensional array of PVectors
  PVector[][] field;
  int cols, rows; // Columns and Rows
  int resolution; // How large is each "cell" of the flow field

  FlowField(int r) {
    resolution = r;
    // Determine the number of columns and rows based on sketch's width and height
    cols = width/resolution;
    rows = height/resolution;
    field = new PVector[cols][rows];
    init();
  }

  public void init() {
    // Reseed noise so we get a new flow field every time
    noiseSeed((int)random(10000));
    float xoff = 0;
    for (int i = 0; i < cols; i++) {
      float yoff = 0;
      for (int j = 0; j < rows; j++) {
        float theta = map(noise(xoff,yoff),0,1,0,TWO_PI);
        // Polar to cartesian coordinate transformation to get x and y components of the vector
        field[i][j] = new PVector(cos(theta),sin(theta));
        yoff += 0.1f;
      }
      xoff += 0.1f;
    }
  }

  // Draw every vector
  public void display() {
    for (int i = 0; i < cols; i++) {
      for (int j = 0; j < rows; j++) {
        drawVector(field[i][j],i*resolution,j*resolution,resolution-2);
      }
    }
  }

  // Renders a vector object 'v' as an arrow and a location 'x,y'
  public void drawVector(PVector v, float x, float y, float scayl) {
    pushMatrix();
    float arrowsize = 4;
    // Translate to location to render vector
    translate(x,y);
    stroke(0,100);
    // Call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
    rotate(v.heading2D());
    // Calculate length of vector & scale it to be bigger or smaller if necessary
    float len = v.mag()*scayl;
    // Draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
    line(0,0,len,0);
    //line(len,0,len-arrowsize,+arrowsize/2);
    //line(len,0,len-arrowsize,-arrowsize/2);
    popMatrix();
  }

  public PVector lookup(PVector lookup) {
    int column = PApplet.parseInt(constrain(lookup.x/resolution,0,cols-1));
    int row = PApplet.parseInt(constrain(lookup.y/resolution,0,rows-1));
    return field[column][row].get();
  }
}


public void mousePressed(){
grow = true;

}

public void mouseReleased(){
grow = false;

}

public void keyPressed() {
  if (key == ' ');{
  spaceBar = !spaceBar;
  }
  if (key == '1') {
    debug = !debug;
  }
  if (key == '2') {
  flowfield.init();
  }


  if (key == 's') {
    endRecord();
      exit();
  }

}
  public void settings() { 
size (1080 ,1920); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "autonomousAgentsFlowField" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
