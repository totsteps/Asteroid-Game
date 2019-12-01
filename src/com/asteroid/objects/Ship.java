package com.asteroid.objects;

import com.asteroid.Constants;

/**
 * Ship object.
 * This object can be controlled with arrows keys. And fires a bullet in the
 * current facing directing when pressing space-bar key.
 */
public class Ship extends AsteroidSprite implements Constants {
  // local variables
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

    // create shapes for the forward thrusters
    fwdThruster = new Thruster();
    fwdThruster.getShape().addPoint(0, 12);
    fwdThruster.getShape().addPoint(-3, 16);
    fwdThruster.getShape().addPoint(0, 26);
    fwdThruster.getShape().addPoint(3, 16);

    // create shapes for the reverse thrusters
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

  /**
   * Returns a bullet with given index.
   *
   * @param index index of the bullet to return.
   * @return a bullet object.
   */
  public Bullet getBullet(int index) {
    return bullets[index];
  }

  /**
   * Returns forward thruster of the Ship.
   *
   * @return forward thruster object.
   */
  public Thruster getFwdThruster() {
    return fwdThruster;
  }

  /**
   * Returns reverse thruster of the Ship.
   *
   * @return reverse thruster object.
   */
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
   * Update Ship Bullets or move currently active bullets.
   * A Ship can only fire some maximum number of Bullets at a given time,
   * which corresponds to MAX_SHOTS.
   * If a bullet is inactive or current number of bullets on the screen exceeds
   * MAX_SHOTS, the un-active or the newly fired bullet gets removed from the screen.
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