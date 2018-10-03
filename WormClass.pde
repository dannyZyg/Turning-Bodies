class Worm {

  PVector location;
  PVector velocity;
  PVector acceleration;
  float maxspeed;
  float maxforce;

  Worm(float x, float y) {
    acceleration = new PVector(0, 0);
    velocity = new PVector(0, 0);
    location = new PVector(x, y);
    maxspeed = 4;
    maxforce = 0.1;
  }

  // Implementing Reynolds' flow field following algorithm
  // http://www.red3d.com/cwr/steer/FlowFollow.html
  
  void follow(FlowField flow) {
    // What is the vector at that spot in the flow field?
    PVector desired = flow.lookup(location);
    // Scale it up by maxspeed
    desired.mult(maxspeed);
    // Steering is desired minus velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    applyForce(steer);
  }

  void update() {
    
    // update worm locations, taking into account velocity, acceleration.
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    location.add(velocity);
    acceleration.mult(0);
  }

  void applyForce(PVector force) {
    acceleration.add(force);
  }

  void seek(PVector target) {
    PVector desired = PVector.sub(target, location);
    desired.normalize();
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);
    applyForce(steer);
  }

  float angle(float input) {

    input += velocity.heading() + PI/2;
    return input;
  }


  void display(float offset) {

    //find direction of vector and rotate
    float theta = velocity.heading() + PI/2;
    pushMatrix();
    translate(location.x, location.y);
    rotate(theta);
    
    //create moving sin values for the two control points of the bezier curve
    float mov1 = map(sin(movStep * offset), -1, 1, -bezSz, bezSz);
    float mov2 = map(cos(movStep + 0.333 * offset), -1, 1, -bezSz, bezSz);
    bezier(0, 0, mov1, (bezSz/4)*1, mov2, (bezSz/4)*3, 0, bezSz);
    popMatrix();
  }

  void colorLerp(float h, float alpha) {  

    // take inpt of colorLerp(), heading and alpha value,
    // and calculate colour of each worm with regards to location.
    color from = color(48, 221, 193, alpha);
    color to = color(249, 224, 34, alpha);
    float xColor = map(location.x, 0, width, 0, 1);
    float yOffset = map(location.y, 0, height, -0.3, 0.3);
    xColor += yOffset;
    color lerped = lerpColor(from, to, (xColor));
    stroke(lerped);
  }

  // join canvas edges
  void borders() {
    if (location.x < 0) location.x = width;
    if (location.y < 0) location.y = height;
    if (location.x > width) location.x = 0;
    if (location.y > height) location.y = 0;
  }
}