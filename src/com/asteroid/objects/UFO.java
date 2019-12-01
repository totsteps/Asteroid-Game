package com.asteroid.objects;

import com.asteroid.Constants;

/**
 * UFO object.
 */
public class UFO extends AsteroidSprite implements Constants {
  // UFO missile
  private Missile missile;

  public UFO() {
    this.getShape().addPoint(-15, 0);
    this.getShape().addPoint(-10, -5);
    this.getShape().addPoint(-5, -5);
    this.getShape().addPoint(-5, -8);
    this.getShape().addPoint(5, -8);
    this.getShape().addPoint(5, -5);
    this.getShape().addPoint(10, -5);
    this.getShape().addPoint(15, 0);
    this.getShape().addPoint(10, 5);
    this.getShape().addPoint(-10, 5);

    missile = new Missile();
  }

  /**
   * Returns the UFO missile.
   *
   * @return missile object.
   */
  public Missile getMissile() {
    return missile;
  }

  public void init() {
    double angle, speed;

    // Randomly set flying saucer at left or right edge of the screen.
    this.setActive(true);
    this.setX(-AsteroidSprite.getWidth() >> 1);
    this.setY(Math.random() * 2 * AsteroidSprite.getHeight() - AsteroidSprite.getWidth());

    angle = Math.random() * Math.PI / 4 - Math.PI / 2;
    speed = MAX_ROCK_SPEED / 2 + Math.random() * (MAX_ROCK_SPEED / 2);

    this.setDeltaX(speed * -Math.sin(angle));
    this.setDeltaY(speed *  Math.cos(angle));

    if (Math.random() < 0.5) {
      this.setX(AsteroidSprite.getWidth() >> 1);
      this.setDeltaX(-this.getDeltaX());
    }

    if (this.getY() > 0) this.setDeltaY(this.getDeltaY());
    this.render();
  }
}