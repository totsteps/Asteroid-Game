package com.asteroid.objects;

public class Bullet extends AsteroidSprite {

  Bullet() {
    this.getShape().addPoint(1, 1);
    this.getShape().addPoint(1, -1);
    this.getShape().addPoint(-1, 1);
    this.getShape().addPoint(-1, -1);
  }
}