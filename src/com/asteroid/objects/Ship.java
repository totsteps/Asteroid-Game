package com.asteroid.objects;

import com.asteroid.Constants;

public class Ship extends AsteroidSprite implements Constants {

  private Bullet[] bullets;
  private Thruster fwdThruster;
  private Thruster revThruster;

  public Ship() {
    this.getShape().addPoint(0, -10);
    this.getShape().addPoint(7, 10);
    this.getShape().addPoint(-7, 10);

    // create bullets
    bullets = new Bullet[MAX_SHOTS];
    for (int i = 0; i < MAX_SHOTS; i++) bullets[i] = new Bullet();

    // Create shapes for the ship thrusters
    fwdThruster = new Thruster();
    fwdThruster.getShape().addPoint(0, 12);
    fwdThruster.getShape().addPoint(-3, 16);
    fwdThruster.getShape().addPoint(0, 26);
    fwdThruster.getShape().addPoint(3, 16);

    revThruster = new Thruster();
    revThruster.getShape().addPoint(-2, 12);
    revThruster.getShape().addPoint(-4, 14);
    revThruster.getShape().addPoint(-2, 20);
    revThruster.getShape().addPoint(0, 14);
    revThruster.getShape().addPoint(2, 12);
    revThruster.getShape().addPoint(4, 14);
    revThruster.getShape().addPoint(2, 20);
    revThruster.getShape().addPoint(0, 14);
  }

  public Bullet getBullet(int index) {
    return bullets[index];
  }

  public Thruster getFwdThruster() {
    return fwdThruster;
  }

  public Thruster getRevThruster() {
    return revThruster;
  }

  @Override
  public void init() {
    // Reset the ship sprite at the center of the screen.
    this.setActive(true);
    this.render();

    // Initialize thruster sprites.
    fwdThruster.setX(this.getX());
    fwdThruster.setY(this.getY());
    fwdThruster.setAngle(this.getAngle());
    fwdThruster.render();
    revThruster.setX(this.getX());
    revThruster.setY(this.getY());
    revThruster.setAngle(this.getAngle());
    revThruster.render();
  }

  /**
   * Update Ship Bullets.
   * Move any active bullets. Stop it when its counter has expired.
   */
  public void updateBullets() {
    for (int i = 0; i < MAX_SHOTS; i++) {
      if (bullets[i].isActive()) {
        if (!bullets[i].advance()) bullets[i].render();
        else bullets[i].setActive(false);
      }
    }
  }
}