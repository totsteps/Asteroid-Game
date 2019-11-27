package com.asteroid.objects;

import java.awt.*;

import com.asteroid.Constants;
import com.asteroid.Screen;

public class Asteroid extends AsteroidSprite implements Constants {

  public void init(Screen screen) {
    int j;
    int s;
    double theta, r;
    int x, y;

    // Create a jagged shape for the asteroid and give it a random rotation.
    this.setShape(new Polygon());
    s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
    for (j = 0; j < s; j ++) {
      theta = 2 * Math.PI / s * j;
      r = MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE));
      x = (int) -Math.round(r * Math.sin(theta));
      y = (int)  Math.round(r * Math.cos(theta));
      this.getShape().addPoint(x, y);
    }
    this.setActive(true);
    this.setAngle(0.0);
    this.setDeltaAngle(Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);

    // Place the asteroid at one edge of the screen.
    if (Math.random() < 0.5) {
      this.setX(-AsteroidSprite.getWidth() >> 1);
      if (Math.random() < 0.5)
        this.setX(AsteroidSprite.getWidth() >> 1);
      this.setY(Math.random() * AsteroidSprite.getHeight());
    }
    else {
      this.setX(Math.random() * AsteroidSprite.getWidth());
      this.setY(-AsteroidSprite.getHeight() >> 1);
      if (Math.random() < 0.5)
        this.setY(AsteroidSprite.getHeight() >> 1);
    }

    // Set a random motion for the asteroid.
    this.setDeltaX(Math.random() * screen.getAsteroidsSpeed());
    if (Math.random() < 0.5)
      this.setDeltaX(-this.getDeltaX());
    this.setDeltaY(Math.random() * screen.getAsteroidsSpeed());
    if (Math.random() < 0.5)
      this.setDeltaY(-this.getDeltaY());

    this.render();
  }
}