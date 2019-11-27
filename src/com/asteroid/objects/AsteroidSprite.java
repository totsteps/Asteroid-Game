package com.asteroid.objects;

import java.awt.*;

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

 public static int getWidth() {
  return width;
 }

 public static void setWidth(int w) {
  width = w;
 }

 public static int getHeight() {
  return height;
 }

 public static void setHeight(int h) {
  height = h;
 }

 public Polygon getShape() {
  return shape;
 }

 public void setShape(Polygon shape) {
  this.shape = shape;
 }

 public boolean isActive() {
  return isActive;
 }

 public void setActive(boolean active) {
  isActive = active;
 }

 public double getAngle() {
  return angle;
 }

 public void setAngle(double angle) {
  this.angle = angle;
 }

 public void setDeltaAngle(double deltaAngle) {
  this.deltaAngle = deltaAngle;
 }

 public double getX() {
  return x;
 }

 public void setX(double x) {
  this.x = x;
 }

 public double getY() {
  return y;
 }

 public void setY(double y) {
  this.y = y;
 }

 public double getDeltaX() {
  return deltaX;
 }

 public void setDeltaX(double deltaX) {
  this.deltaX = deltaX;
 }

 public double getDeltaY() {
  return deltaY;
 }

 public void setDeltaY(double deltaY) {
  this.deltaY = deltaY;
 }

 public Polygon getSprite() {
  return sprite;
 }

 public void init() {}

 public boolean advance() {
  boolean wrapped;

  this.angle += this.deltaAngle;

  if (this.angle < 0) this.angle += 2 * Math.PI;
  if (this.angle > 2 * Math.PI) this.angle -= 2 * Math.PI;

  wrapped = false;
  this.x += this.deltaX;

  if (this.x < (double)-width / 2) {
   this.x += width;
   wrapped = true;
  }
  if (this.x > (double)width / 2) {
   this.x -= width;
   wrapped = true;
  }

  this.y -= this.deltaY;
  if (this.y < (double)-height / 2) {
   this.y += height;
   wrapped = true;
  }

  if (this.y > (double)height / 2) {
   this.y -= height;
   wrapped = true;
  }

  return wrapped;
 }

 public void render() {
  int i;

  this.sprite = new Polygon();
  for (i = 0; i < this.shape.npoints; i++) {
   this.sprite.addPoint((int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) +
           this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
       (int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) +
           (int) Math.round(this.y) + height / 2);
  }
 }

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