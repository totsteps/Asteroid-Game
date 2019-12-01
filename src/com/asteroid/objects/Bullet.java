package com.asteroid.objects;

/**
 * Bullet object that is fired by the Ship.
 */
public class Bullet extends AsteroidSprite {

  Bullet() {
    this.getShape().addPoint(1, 1);
    this.getShape().addPoint(1, -1);
    this.getShape().addPoint(-1, 1);
    this.getShape().addPoint(-1, -1);
  }
}