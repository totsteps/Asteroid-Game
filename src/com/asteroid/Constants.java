package com.asteroid;

public interface Constants {

  // Copyright information.
  String COPY_NAME = "Screen";
  String COPY_VERSION = "Version 1.3";
  String COPY_INFO = "Copyright 1998-2001 by Mike Hall";
  String COPY_LINK = "http://www.brainjar.com";
  String COPY_TEXT = COPY_NAME + '\n' + COPY_VERSION + '\n'
      + COPY_INFO + '\n' + COPY_LINK;

  // Font
  String FONT_NAME = "Helvetica";
  int FONT_SIZE = 14;

  int DELAY = 20;                     // Milliseconds between a screen and
  int FPS = Math.round(1000 / DELAY); // the resulting frame rate.

  int MAX_SHOTS =  8;          // Maximum number of sprites
  int MAX_ROCKS =  8;          // for photons, asteroids and
  int MAX_SCRAP = 40;          // explosions.

  int SCRAP_COUNT  = 2 * FPS;  // Timer counter starting values
  int HYPER_COUNT  = 3 * FPS;  // calculated using number of
  int MISSILE_COUNT = 4 * FPS; // seconds currentX frames per second.
  int STORM_PAUSE  = 2 * FPS;

  int    MIN_ROCK_SIDES =   6; // Ranges for asteroid shape, size
  int    MAX_ROCK_SIDES =  16; // speed and rotation.
  int    MIN_ROCK_SIZE  =  20;
  int    MAX_ROCK_SIZE  =  40;
  double MIN_ROCK_SPEED =  40.0 / FPS;
  double MAX_ROCK_SPEED = 240.0 / FPS;
  double MAX_ROCK_SPIN  = Math.PI / FPS;

  int MAX_SHIPS = 3;    // Starting number of ships for each game.
  int UFO_PASSES = 3;   // Number of passes for flying saucer per appearance.

  // Ship's rotation and acceleration rates and maximum speed.
  double SHIP_ANGLE_STEP = Math.PI / FPS;
  double SHIP_SPEED_STEP = 15.0 / FPS;
  double MAX_SHIP_SPEED  = 1.25 * MAX_ROCK_SPEED;

  // int FIRE_DELAY = 50; // Minimum number of milliseconds required between photon shots.

  // Probability of flying saucer firing a missile during any given frame
  // (other conditions must be met).
  double MISSILE_PROBABILITY = 0.45 / FPS;

  int BIG_POINTS    =  25;   // Points scored for shooting
  int SMALL_POINTS  =  50;   // various objects.
  int UFO_POINTS    = 250;
  int MISSILE_POINTS = 500;

  // Number of points that must be scored to earn a new ship or to cause the
  // a flying saucer to appear.
  int NEW_SHIP_POINTS = 5000;
  int NEW_UFO_POINTS  = 2750;
}