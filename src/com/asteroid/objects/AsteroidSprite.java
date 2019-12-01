package com.asteroid.objects;

import java.awt.*;

/**
 * An AsteroidSprite represents abstract behavior of different types of objects
 * in the game. Each of these objects have associated data such as shape, angle,
 * deltaAngle, x and y position and sprite. And state such as isActive.
 */
public abstract class AsteroidSprite {

 // Dimensions of the graphics area
 private static int width;
 private static int height;

 private Polygon shape;          // Base sprite shape, centered at the origin (0,0).
 private boolean isActive;       // Active flag.
 private double angle;           // Current angle of rotation.
 private double deltaAngle;      // Amount to change the rotation angle.
 private double x, y;            // Current position on screen.
 private double deltaX, deltaY;  // Amount to change the screen position.
 private Polygon sprite;         // Final location and shape of sprite after

 AsteroidSprite() {
  this.shape = new Polygon();
  this.isActive = false;
  this.angle = 0.0;
  this.deltaAngle = 0.0;
  this.x = 0.0;
  this.y = 0.0;
  this.deltaX = 0.0;
  this.deltaY = 0.0;
  this.sprite = new Polygon();
 }

  /**
   * Returns the current width of the screen.
   * @return width.
   */
 public static int getWidth() {
  return width;
 }

  /**
   * Sets the current width of the screen.
   *
   * @param w width of the screen.
   */
 public static void setWidth(int w) {
  width = w;
 }

  /**
   * Returns the current height of the screen.
   *
   * @return height.
   */
 public static int getHeight() {
  return height;
 }

  /**
   * Sets the current height of the screen.
   *
   * @param h height of the screen.
   */
 public static void setHeight(int h) {
  height = h;
 }

  /**
   * Returns the shape sprite of an object that is how the object looks
   * on the screen. Example: Ship, UFO, etc.
   *
   * @return shape.
   */
 public Polygon getShape() {
  return shape;
 }

  /**
   * Sets the shape sprite of an object.
   *
   * @param shape shape of an object.
   */
 public void setShape(Polygon shape) {
  this.shape = shape;
 }

  /**
   * Returns whether this object is currently active on the screen.
   *
   * @return boolean.
   */
 public boolean isActive() {
  return isActive;
 }

  /**
   * Sets whether this object is currently active on the screen.
   *
   * @param active whether the object is active or not.
   */
 public void setActive(boolean active) {
  isActive = active;
 }

  /**
   * Returns the current angle of rotation of an object on the screen.
   *
   * @return angle.
   */
 public double getAngle() {
  return angle;
 }

  /**
   * Sets the current angle of rotation of an object on the screen.
   *
   * @param angle angle of an object.
   */
 public void setAngle(double angle) {
  this.angle = angle;
 }

  /**
   * Sets the amount(deltaAngle) to change the current rotation angle of an object.
   *
   * @param deltaAngle deltaAngle of an object.
   */
 public void setDeltaAngle(double deltaAngle) {
  this.deltaAngle = deltaAngle;
 }

  /**
   * Returns the current x-position of an object on the screen.
   *
   * @return x.
   */
 public double getX() {
  return x;
 }

  /**
   * Sets the current x-position of an object on the screen.
   *
   * @param x x-position.
   */
 public void setX(double x) {
  this.x = x;
 }

  /**
   * Returns the current y-position of an object on the screen.
   *
   * @return y.
   */
 public double getY() {
  return y;
 }

  /**
   * Sets the current y-position of an object on the screen.
   *
   * @param y y-position.
   */
 public void setY(double y) {
  this.y = y;
 }

  /**
   * Returns the amount in change of the current x-position of an object on
   * the screen.
   *
   * @return change in x-position.
   */
 public double getDeltaX() {
  return deltaX;
 }

  /**
   * Sets the amount to change the current x-position of an object on the screen.
   *
   * @param deltaX amount to change x-position.
   */
 public void setDeltaX(double deltaX) {
  this.deltaX = deltaX;
 }

  /**
   * Returns the amount in change of the current y-position of an object on
   * the screen.
   *
   * @return change in y-position.
   */
 public double getDeltaY() {
  return deltaY;
 }

  /**
   * Sets the amount to change the current y-position of an object on the screen.
   *
   * @param deltaY amount to change y-position.
   */
 public void setDeltaY(double deltaY) {
  this.deltaY = deltaY;
 }

  /**
   * Returns the final location and shape of sprite after an object is born.
   *
   * @return sprite.
   */
 public Polygon getSprite() {
  return sprite;
 }

 public void init() {}

  /**
   * Control method on how each game object should move on the screen.
   * If the object goes off the screen, it gets wrapped on the opposite side
   * of the screen.
   *
   * @return boolean.
   */
 public boolean advance() {
  boolean wrapped;

  this.angle += this.deltaAngle;

  if (this.angle < 0) this.angle += 2 * Math.PI;
  if (this.angle > 2 * Math.PI) this.angle -= 2 * Math.PI;

  wrapped = false;
  this.x += this.deltaX;

  if (this.x < (double) -width / 2) {
   this.x += width;
   wrapped = true;
  }
  if (this.x > (double) width / 2) {
   this.x -= width;
   wrapped = true;
  }

  this.y -= this.deltaY;
  if (this.y < (double) -height / 2) {
   this.y += height;
   wrapped = true;
  }

  if (this.y > (double) height / 2) {
   this.y -= height;
   wrapped = true;
  }

  return wrapped;
 }

  /**
   * Render(draw) an object on the screen.
   * An object has sprites which can be adjusted to give
   * whatever shape to an object.
   */
 public void render() {
  int i;

  this.sprite = new Polygon();
  for (i = 0; i < this.shape.npoints; i++) {
   this.sprite.addPoint(
     (int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) + this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
       (int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) + (int) Math.round(this.y) + height / 2);
  }
 }

  /**
   * Check whether an object is colliding with this object when
   * on the screen.
   *
   * @param asteroidSprite sprite(another object).
   * @return boolean.
   */
 public boolean isColliding(AsteroidSprite asteroidSprite) {
  int i;

  for (i = 0; i < asteroidSprite.sprite.npoints; i++) {
   if (this.sprite.contains(asteroidSprite.sprite.xpoints[i], asteroidSprite.sprite.ypoints[i])) {
    return true;
   }
  }

  for (i = 0; i < this.sprite.npoints; i++) {
   if (asteroidSprite.sprite.contains(this.sprite.xpoints[i], this.sprite.ypoints[i]))
    return true;
  }

  return false;
 }
}