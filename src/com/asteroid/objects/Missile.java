package com.asteroid.objects;

public class Missile extends AsteroidSprite {
  
  Missile() {
    this.getShape().addPoint(0, -4);
    this.getShape().addPoint(1, -3);
    this.getShape().addPoint(1, 3);
    this.getShape().addPoint(2, 4);
    this.getShape().addPoint(-2, 4);
    this.getShape().addPoint(-1, 3);
    this.getShape().addPoint(-1, -3);
  }
  
  public void init() {
    this.setActive(true);
    this.setAngle(0.0);
    this.setDeltaAngle(0.0);
    this.setDeltaX(0.0);
    this.setDeltaY(0.0);
  }
}