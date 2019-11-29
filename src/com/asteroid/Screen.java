package com.asteroid;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.asteroid.objects.*;

/**
 * The screen object which displays different game objects like,
 * Ship, Asteroids, UFO, explosions etc.
 *
 */
public class Screen extends JPanel implements Runnable, KeyListener, Constants {

  // background stars
  private int numStars;
  private Star[] stars;

  // game data
  private int score;
  private int highScore;
  private int newShipScore;
  private int newUFOScore;

  // flags for game state and options
  private boolean isLoaded;
  private boolean paused;
  private boolean playing;
  private boolean sound;
  private boolean detail;

  // key flags
  private boolean left = false;
  private boolean right = false;
  private boolean up = false;
  private boolean down = false;

  // game objects
  private Ship ship;
  private UFO ufo;
  private Asteroid[] asteroids = new Asteroid[MAX_ROCKS];
  private Explosion[] explosions = new Explosion[MAX_SCRAP];

  private int shipsLeft;       // number of ships left in game, including current one
  // ship data
  private int shipCounter;     // timer counter for ship explosion
  private int hyperCounter;    // timer counter for hyperspace

  // bullet data
  private int bulletIndex;     // index to next available bullet
  private long bulletTime;     // time value used to keep firing rate constant

  // flying saucer data
  private int ufoPassesLeft;   // counter for number of flying saucer passes
  private int ufoCounter;      // timer counter used to track each flying saucer pass

  // missile data
  private int missileCounter;  // counter for life of missile

  // asteroid data
  private boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];  // asteroid size flag
  private int asteroidsCounter;    // break-time counter

  private double asteroidsSpeed;   // asteroid speed
  private int asteroidsLeft;       // number of active asteroids

  // explosion data
  private int[] explosionCounter = new int[MAX_SCRAP];  // time counters for explosions
  private int explosionIndex;

  // flags for looping sounds
  private boolean thrustersPlaying;
  private boolean saucerPlaying;
  private boolean missilePlaying;

  // off screen image
  private Dimension offDimension;
  private Image offImage;
  private Graphics offGraphics;

  // data for the screen font
  private Font font = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
  private FontMetrics fm = getFontMetrics(font);
  private int fontWidth = fm.getMaxAdvance();
  private int fontHeight = fm.getHeight();

  // thread control variables
  private Thread loadThread;
  private Thread loopThread;

  /**
   * Returns Asteroid's speed
   *
   * @return double - asteroid's speed
   */
  public double getAsteroidsSpeed() {
    return asteroidsSpeed;
  }

  /**
   * Start the screen by initializing the window width, height
   * and the frame. When resized, repaint the screen.
   */
  void start() {
    Screen screen = new Screen();
    screen.setBounds(0, 0, 1200, 800);
    screen.init();

    JFrame mainFrame= new JFrame("Asteroids Game");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.add(screen);
    mainFrame.setSize(1200, 800);
    mainFrame.setLayout(null);
    mainFrame.setVisible(true);

    // add resize listener
    mainFrame.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        screen.setBounds(0, 0, e.getComponent().getWidth(), e.getComponent().getHeight());
        screen.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
        AsteroidSprite.setWidth(e.getComponent().getWidth());
        AsteroidSprite.setHeight(e.getComponent().getHeight());

        screen.repaint();
      }
    });

    screen.startThread();
  }

  /**
   * Create all game objects, initialize game data and state variables.
   */
  private void init() {
    Dimension dimension = getSize();
    int i;

    // display copyright information
    System.out.println(COPY_TEXT);

    // set up key event handling and set focus to applet window
    addKeyListener(this);
    setFocusable(true);

    // save the screen size
    AsteroidSprite.setWidth(dimension.width);
    AsteroidSprite.setHeight(dimension.height);

    // generate the starry background
    numStars = AsteroidSprite.getWidth() * AsteroidSprite.getHeight() / 5000;
    stars = new Star[numStars];
    for (i = 0; i < numStars; i++) { // create star objects
      stars[i] = new Star((int) (Math.random() * AsteroidSprite.getWidth()),
        (int) (Math.random() * AsteroidSprite.getHeight()));
    }

    // create shape for the ship sprite
    ship = new Ship();

    bulletIndex = 0;

    // create shape for the flying saucer
    ufo = new UFO();

    // create asteroid sprites
    for (i = 0; i < MAX_ROCKS; i++) asteroids[i] = new Asteroid();

    // Create explosion sprites
    for (i = 0; i < MAX_SCRAP; i++) {
      explosions[i] = new Explosion();
      explosionCounter[i] = 0;
    }
    explosionIndex = 0;

    // initialize game data and put us in 'game over' mode
    highScore = 0;
    sound = true;
    detail = true;
    initGame();
    endGame();
  }

  /**
   * Initialize game data and sprites.
   */
  private void initGame() {
    score = 0;
    shipsLeft = MAX_SHIPS;
    asteroidsSpeed = MIN_ROCK_SPEED;
    newShipScore = NEW_SHIP_POINTS;
    newUFOScore = NEW_UFO_POINTS;
    initShip();
    stopUFO();
    stopMissile();
    initAsteroids();
    playing = true;
    paused = false;
    bulletTime = System.currentTimeMillis();
  }

  /**
   * Initialize Ship and corresponding sounds.
   */
  private void initShip() {
    ship.init();

    if (this.isLoaded) Sound.getThrustersSound().stop();
    this.thrustersPlaying = false;
    this.hyperCounter = 0;
  }

  /**
   * Stops Ship. If the Ship stops its sound will be stopped if playing.
   */
  private void stopShip() {
    ship.setActive(false);
    this.shipCounter = SCRAP_COUNT;

    if (playing && this.shipsLeft > 0) this.shipsLeft--;
    if (this.isLoaded) Sound.getThrustersSound().stop();

    thrustersPlaying = false;
  }

  /**
   * Update Ship's position on the screen.
   */
  private void updateShip() {
    double dx, dy, speed;

    if (!playing) return;

    // rotate the ship if left or right arrow key is down
    if (left) {
      ship.setAngle(ship.getAngle() + SHIP_ANGLE_STEP);
      if (ship.getAngle() > 2 * Math.PI)
        ship.setAngle(ship.getAngle() - 2 * Math.PI);
    }
    if (right) {
      ship.setAngle(ship.getAngle() - SHIP_ANGLE_STEP);
      if (ship.getAngle() < 0)
        ship.setAngle(ship.getAngle() + 2 * Math.PI);
    }

    // fire thrusters if up or down arrow key is down
    dx = SHIP_SPEED_STEP * -Math.sin(ship.getAngle());
    dy = SHIP_SPEED_STEP *  Math.cos(ship.getAngle());

    if (up) {
      ship.setDeltaX(ship.getDeltaX() + dx);
      ship.setDeltaY(ship.getDeltaY() + dy);
    }

    if (down) {
      ship.setDeltaX(ship.getDeltaX() - dx);
      ship.setDeltaY(ship.getDeltaY() - dy);
    }

    // don't let ship go past the speed limit
    if (up || down) {
      speed = Math.sqrt(ship.getDeltaX() * ship.getDeltaX() + ship.getDeltaY() * ship.getDeltaY());

      if (speed > MAX_SHIP_SPEED) {
        dx = MAX_SHIP_SPEED * -Math.sin(ship.getAngle());
        dy = MAX_SHIP_SPEED *  Math.cos(ship.getAngle());
        if (up)
          ship.setDeltaX(dx);
        else
          ship.setDeltaX(-dx);
        if (up)
          ship.setDeltaY(dy);
        else
          ship.setDeltaY(-dy);
      }
    }

    // move the ship if it's currently in hyperspace, advance the countdown
    if (ship.isActive()) {
      ship.advance();
      ship.render();
      if (hyperCounter > 0) hyperCounter--;

      // update the thruster sprites to match the ship sprite
      ship.getFwdThruster().setX(ship.getX());
      ship.getFwdThruster().setY(ship.getY());
      ship.getFwdThruster().setAngle(ship.getAngle());
      ship.getFwdThruster().render();

      ship.getRevThruster().setX(ship.getX());
      ship.getRevThruster().setY(ship.getY());
      ship.getRevThruster().setAngle(ship.getAngle());
      ship.getRevThruster().render();
    }

    // ship is exploding, advance the countdown or create a new ship if it is
    // done exploding. The new ship is added as though it were in hyperspace.
    // (This gives the player time to move the ship if it is in imminent
    // danger.) If that was the last ship, end the game.
    else {
      if (--shipCounter <= 0)
        if (shipsLeft > 0) {
          initShip();
          hyperCounter = HYPER_COUNT;
        } else endGame();
    }
  }

//  /**
//   * Update Ship Bullets.
//   * Move any active bullets. Stop it when its counter has expired.
//   */
//  private void updateBullets() {
//    for (int i = 0; i < MAX_SHOTS; i++) {
//      if (ship.getBullet(i).isActive()) {
//        if (!ship.getBullet(i).advance()) ship.getBullet(i).render();
//        else ship.getBullet(i).setActive(false);
//      }
//    }
//  }

  /**
   * Initialize UFO and corresponding sound.
   */
  private void initUFO() {
    ufo.init();
    saucerPlaying = true;

    if (sound) {
      Sound.getSaucerSound().setFramePosition(0);
      Sound.getSaucerSound().start();
      Sound.getSaucerSound().loop(Clip.LOOP_CONTINUOUSLY);
    }

    ufoCounter = (int) Math.abs(AsteroidSprite.getWidth() / ufo.getDeltaX());
  }

  /**
   * Stop UFO. If the UFO stops its sound will be stopped if playing.
   */
  private void stopUFO() {
    ufo.setActive(false);
    ufoCounter = 0;
    ufoPassesLeft = 0;

    if (isLoaded) Sound.getSaucerSound().stop();
    saucerPlaying = false;
  }

  /**
   * Update UFO position on the screen.
   */
  private void updateUFO() {
    int i, d;

    // move the flying saucer and check for collision with a bullet. Stop it
    // when its counter has expired.
    if (ufo.isActive()) {
      if (--ufoCounter <= 0) {

        if (--ufoPassesLeft > 0) initUFO();
        else stopUFO();
      }

      if (ufo.isActive()) {
        ufo.advance();
        ufo.render();
        for (i = 0; i < MAX_SHOTS; i++)
          if (ship.getBullet(i).isActive() && ufo.isColliding(ship.getBullet(i))) {
            if (sound) {
              Sound.getCrashSound().setFramePosition(0);
              Sound.getCrashSound().start();
            }
            explode(ufo);
            stopUFO();
            score += UFO_POINTS;
          }

        // on occasion, fire a missile at the ship if the saucer is not too
        // close to it.
        d = (int) Math.max(Math.abs(ufo.getX() - ship.getX()), Math.abs(ufo.getY() - ship.getY()));
        if (ship.isActive() && hyperCounter <= 0 &&
            ufo.isActive() && !ufo.getMissile().isActive() &&
            d > MAX_ROCK_SPEED * FPS / 2 &&
            Math.random() < MISSILE_PROBABILITY)
          initMissile();
      }
    }
  }

  /**
   * Initialize UFO Missile and corresponding sound.
   */
  private void initMissile() {
    ufo.getMissile().init();
    ufo.getMissile().setX(ufo.getX());
    ufo.getMissile().setY(ufo.getY());
    ufo.getMissile().render();

    missileCounter = MISSILE_COUNT;

    if (sound) {
      Sound.getMissileSound().start();
      Sound.getMissileSound().loop(Clip.LOOP_CONTINUOUSLY);
    }

    missilePlaying = true;
  }

  /**
   * Stop UFO Missile. If the Missile stops its sound will be stopped if playing.
   */
  private void stopMissile() {
    ufo.getMissile().setActive(false);

    missileCounter = 0;
    if (isLoaded) Sound.getMissileSound().stop();
    missilePlaying = false;
  }

  /**
   * Update UFO Missile position on the screen.
   */
  private void updateMissile() {
    int i;

    // move the guided missile and check for collision with ship or bullet. Stop
    // it when its counter has expired.
    if (ufo.getMissile().isActive()) {
      if (--missileCounter <= 0)
        stopMissile();
      else {
        guideMissile();
        ufo.getMissile().advance();
        ufo.getMissile().render();

        for (i = 0; i < MAX_SHOTS; i++)
          if (ship.getBullet(i).isActive() && ufo.getMissile().isColliding(ship.getBullet(i))) {
            if (sound) {
              Sound.getCrashSound().setFramePosition(0);
              Sound.getCrashSound().start();
            }

            explode(ufo.getMissile());
            stopMissile();
            score += MISSILE_POINTS;
          }

        if (ufo.getMissile().isActive() && ship.isActive() &&
            hyperCounter <= 0 && ship.isColliding(ufo.getMissile())) {
          if (sound) {
            Sound.getCrashSound().setFramePosition(0);
            Sound.getCrashSound().start();
          }

          explode(ship);
          stopShip();
          stopUFO();
          stopMissile();
        }
      }
    }
  }

  /**
   * Guide UFO Missile towards the Ship.
   * Missile's position is updated respective to the position of the Ship.
   */
  private void guideMissile() {
    double dx, dy, angle;

    if (!ship.isActive() || hyperCounter > 0) return;

    // Find the angle needed to hit the ship.
    dx = ship.getX() - ufo.getMissile().getX();
    dy = ship.getY() - ufo.getMissile().getY();
    if (dx == 0) {
      if (dy < 0)
        angle = -Math.PI / 2;
      else
        angle = Math.PI / 2;
    }
    else {
      angle = Math.atan(Math.abs(dy / dx));
      if (dy > 0)
        angle = -angle;
      if (dx < 0)
        angle = Math.PI - angle;
    }

    // Adjust angle for screen coordinates.
    ufo.getMissile().setAngle(angle - Math.PI / 2);

    // Change the missile's angle so that it points toward the ship.
    ufo.getMissile().setDeltaX(0.75 * MAX_ROCK_SPEED * -Math.sin(ufo.getMissile().getAngle()));
    ufo.getMissile().setDeltaY(0.75 * MAX_ROCK_SPEED *  Math.cos(ufo.getMissile().getAngle()));
  }

  /**
   * Initialize Asteroids and corresponding sound.
   */
  private void initAsteroids() {
    for (int i = 0; i < MAX_ROCKS; i++) {
      asteroids[i].init(this);
      asteroidIsSmall[i] = false;
    }

    asteroidsCounter = STORM_PAUSE;
    asteroidsLeft = MAX_ROCKS;

    if (asteroidsSpeed < MAX_ROCK_SPEED) asteroidsSpeed += 0.5;
  }

  /**
   * Update Asteroids position on the screen.
   */
  private void updateAsteroids() {
    int i, j;

    // Move any active asteroids and check for collisions.
    for (i = 0; i < MAX_ROCKS; i++) {
      if (asteroids[i].isActive()) {
        asteroids[i].advance();
        asteroids[i].render();

        // If hit by bullet, kill asteroid and advance score. If asteroid is
        // large, make some smaller ones to replace it.
        for (j = 0; j < MAX_SHOTS; j++)
          if (ship.getBullet(j).isActive() && asteroids[i].isActive() && asteroids[i].isColliding(ship.getBullet(j))) {
            asteroidsLeft--;
            asteroids[i].setActive(false);
            ship.getBullet(j).setActive(false);
            if (sound) {
              Sound.getExplosionSound().setFramePosition(0);
              Sound.getExplosionSound().start();
            }

            explode(asteroids[i]);
            if (!asteroidIsSmall[i]) {
              score += BIG_POINTS;
              initSmallAsteroids(i);
            } else
              score += SMALL_POINTS;
          }

        // If the ship is not in hyperspace, see if it is hit.
        if (ship.isActive() && hyperCounter <= 0 &&
            asteroids[i].isActive() && asteroids[i].isColliding(ship)) {
          if (sound) {
            Sound.getCrashSound().setFramePosition(0);
            Sound.getCrashSound().start();
          }

          explode(ship);
          stopShip();
          stopUFO();
          stopMissile();
        }
      }
    }
  }

  /**
   * Create one or two smaller asteroids from a larger one using inactive
   * asteroids. The new asteroids will be placed in the same position as the
   * old one but will have a new, smaller shape and new, randomly generated
   * movements
   */
  private void initSmallAsteroids(int n) {
    int count;
    int i, j;
    int s;
    double tempX, tempY;
    double theta, r;
    int x, y;

    count = 0;
    i = 0;
    tempX = asteroids[n].getX();
    tempY = asteroids[n].getY();

    do {
      if (!asteroids[i].isActive()) {
        asteroids[i].setShape(new Polygon());
        s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
        for (j = 0; j < s; j ++) {
          theta = 2 * Math.PI / s * j;
          r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) >> 1;
          x = (int) -Math.round(r * Math.sin(theta));
          y = (int)  Math.round(r * Math.cos(theta));
          asteroids[i].getShape().addPoint(x, y);
        }
        asteroids[i].setActive(true);
        asteroids[i].setAngle(0.0);
        asteroids[i].setDeltaAngle(Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);
        asteroids[i].setX(tempX);
        asteroids[i].setY(tempY);
        asteroids[i].setDeltaX(Math.random() * 2 * asteroidsSpeed - asteroidsSpeed);
        asteroids[i].setDeltaY(Math.random() * 2 * asteroidsSpeed - asteroidsSpeed);
        asteroids[i].render();
        asteroidIsSmall[i] = true;
        count++;
        asteroidsLeft++;
      }

      i++;
    } while (i < MAX_ROCKS && count < 2);
  }

  /**
   * Create sprites for explosion animation. The each individual line segment
   * of the given sprite is used to create a new sprite that will move
   * outward from the sprite's original position with a random rotation.
   */
  private void explode(AsteroidSprite s) {
    int c, i, j;
    int cx, cy;

    s.render();
    c = 2;

    if (detail || s.getSprite().npoints < 6) c = 1;
    for (i = 0; i < s.getSprite().npoints; i += c) {
      explosionIndex++;

      if (explosionIndex >= MAX_SCRAP) explosionIndex = 0;

      explosions[explosionIndex].setActive(true);
      explosions[explosionIndex].setShape(new Polygon());
      j = i + 1;

      if (j >= s.getSprite().npoints) j -= s.getSprite().npoints;

      cx = (s.getShape().xpoints[i] + s.getShape().xpoints[j]) / 2;
      cy = (s.getShape().ypoints[i] + s.getShape().ypoints[j]) / 2;
      explosions[explosionIndex].getShape().addPoint(
          s.getShape().xpoints[i] - cx,
          s.getShape().ypoints[i] - cy);
      explosions[explosionIndex].getShape().addPoint(
          s.getShape().xpoints[j] - cx,
          s.getShape().ypoints[j] - cy);
      explosions[explosionIndex].setX(s.getX() + cx);
      explosions[explosionIndex].setY(s.getY() + cy);
      explosions[explosionIndex].setAngle(s.getAngle());
      explosions[explosionIndex].setDeltaAngle(4 * (Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN));
      explosions[explosionIndex].setDeltaX((Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.getDeltaX()) / 2);
      explosions[explosionIndex].setDeltaY((Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.getDeltaY()) / 2);
      explosionCounter[explosionIndex] = SCRAP_COUNT;
    }
  }

  /**
   * Move any active explosion debris. Stop explosion when its counter has
   * expired.
   */
  private void updateExplosions() {
    for (int i = 0; i < MAX_SCRAP; i++) {
      if (explosions[i].isActive()) {
        explosions[i].advance();
        explosions[i].render();

        if (--explosionCounter[i] < 0) explosions[i].setActive(false);
      }
    }
  }

  /**
   * Stop ship, flying saucer, guided missile and associated sounds.
   */
  private void endGame() {
    playing = false;
    stopShip();
    stopUFO();
    stopMissile();
  }

  /**
   * Load Sounds.
   */
  private void loadSounds() {
    Sound.loadSounds(this);
  }

  /**
   * Start thread
   */
  private void startThread() {
    if (loopThread == null) {
      loopThread = new Thread(this);
      loopThread.start();
    }

    if (!isLoaded && loadThread == null) {
      loadThread = new Thread(this);
      loadThread.start();
    }
  }

  /**
   * Run the game loop.
   */
  @Override
  public void run() {
    long startTime;

    // Lower this thread's priority and get the current time.
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    startTime = System.currentTimeMillis();

    // Run thread for loading sounds.
    if (!isLoaded && Thread.currentThread() == loadThread) {
      loadSounds();
      isLoaded = true;

      try {
        loadThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // game loop
    while (Thread.currentThread() == loopThread) {
      if (!paused) {

        // Move and process all sprites.
        updateShip();
        ship.updateBullets();
        updateUFO();
        updateMissile();
        updateAsteroids();
        updateExplosions();

        // Check the score and advance high score, add a new ship or start the
        // flying saucer as necessary.
        if (score > highScore) highScore = score;
        if (score > newShipScore) {
          newShipScore += NEW_SHIP_POINTS;
          shipsLeft++;
        }

        if (playing && score > newUFOScore && !ufo.isActive()) {
          newUFOScore += NEW_UFO_POINTS;
          ufoPassesLeft = UFO_PASSES;
          initUFO();
        }

        // If all asteroids have been destroyed create a new batch.
        if (asteroidsLeft <= 0) {
          if (--asteroidsCounter <= 0) initAsteroids();
        }
      }

      // Update the screen and set the timer for the next loop.
      repaint();

      try {
        startTime += DELAY;
        Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  /**
   * Listen for key presses.
   */
  @Override
  public void keyPressed(KeyEvent event) {
    char c;

    // Check if any cursor keys have been pressed and set flags.
    if (event.getKeyCode() == KeyEvent.VK_LEFT) left = true;
    if (event.getKeyCode() == KeyEvent.VK_RIGHT) right = true;
    if (event.getKeyCode() == KeyEvent.VK_UP) up = true;
    if (event.getKeyCode() == KeyEvent.VK_DOWN) down = true;

    if ((up || down) && ship.isActive() && !thrustersPlaying) {
      if (sound && !paused) {
        Sound.getThrustersSound().setFramePosition(0);
        Sound.getThrustersSound().start();
        Sound.getThrustersSound().loop(Clip.LOOP_CONTINUOUSLY);
      }

      thrustersPlaying = true;
    }

    // SpaceBar: fire a bullet and start its counter.
    if (event.getKeyChar() == ' ' && ship.isActive()) {
      if (sound & !paused) {
        Sound.getFireSound().setFramePosition(0);
        Sound.getFireSound().start();
      }

      bulletTime = System.currentTimeMillis();
      bulletIndex++;

      if (bulletIndex >= MAX_SHOTS) bulletIndex = 0;

      ship.getBullet(bulletIndex).setActive(true);
      ship.getBullet(bulletIndex).setX(ship.getX());
      ship.getBullet(bulletIndex).setY(ship.getY());
      ship.getBullet(bulletIndex).setDeltaX(2 * MAX_ROCK_SPEED * -Math.sin(ship.getAngle()));
      ship.getBullet(bulletIndex).setDeltaY(2 * MAX_ROCK_SPEED *  Math.cos(ship.getAngle()));
    }

    // Allow upper or lower case characters for remaining keys.
    c = Character.toLowerCase(event.getKeyChar());

    // 'H' key: warp ship into hyperspace by moving to a random location and
    // starting counter.
    if (c == 'h' && ship.isActive() && hyperCounter <= 0) {
      ship.setX(Math.random() * AsteroidSprite.getWidth());
      ship.setY(Math.random() * AsteroidSprite.getHeight());
      hyperCounter = HYPER_COUNT;

      if (sound & !paused) Sound.getWarpSound().setFramePosition(0);
      Sound.getWarpSound().start();
    }

    // 'P' key: toggle pause mode and start or stop any active looping sound
    // clips.
    if (c == 'p') {
      if (paused) {
        if (sound && missilePlaying) {
          Sound.getMissileSound().setFramePosition(0);
          Sound.getMissileSound().start();
          Sound.getMissileSound().loop(Clip.LOOP_CONTINUOUSLY);
        }

        if (sound && saucerPlaying) {
          Sound.getSaucerSound().setFramePosition(0);
          Sound.getSaucerSound().start();
          Sound.getSaucerSound().loop(Clip.LOOP_CONTINUOUSLY);
        }

        if (sound && thrustersPlaying) {
          Sound.getThrustersSound().setFramePosition(0);
          Sound.getThrustersSound().start();
          Sound.getThrustersSound().loop(Clip.LOOP_CONTINUOUSLY);
        }
      }
      else {

        if (missilePlaying)
          Sound.getMissileSound().stop();
        if (saucerPlaying)
          Sound.getSaucerSound().stop();
        if (thrustersPlaying)
          Sound.getThrustersSound().stop();
      }

      paused = !paused;
    }

    // 'M' key: toggle sound on or off and stop any looping sound clips.
    if (c == 'm' && isLoaded) {
      if (sound) {
        Sound.getCrashSound().stop();
        Sound.getExplosionSound().stop();
        Sound.getFireSound().stop();
        Sound.getMissileSound().stop();
        Sound.getSaucerSound().stop();
        Sound.getThrustersSound().stop();
        Sound.getWarpSound().stop();
      }
      else {
        if (missilePlaying && !paused) {
          Sound.getMissileSound().setFramePosition(0);
          Sound.getMissileSound().start();
          Sound.getMissileSound().loop(Clip.LOOP_CONTINUOUSLY);
        }

        if (saucerPlaying && !paused) {
          Sound.getSaucerSound().setFramePosition(0);
          Sound.getSaucerSound().start();
          Sound.getSaucerSound().loop(Clip.LOOP_CONTINUOUSLY);
        }

        if (thrustersPlaying && !paused) {
          Sound.getThrustersSound().setFramePosition(0);
          Sound.getThrustersSound().start();
          Sound.getThrustersSound().loop(Clip.LOOP_CONTINUOUSLY);
        }
      }

      sound = !sound;
    }

    // 'D' key: toggle graphics detail on or off.
    if (c == 'd') detail = !detail;

    // 'S' key: start the game, if not already in progress.
    if (c == 's' && isLoaded && !playing) initGame();

    if (c == 'x' && isLoaded) endGame();

    // 'HOME' key: jump to web site (undocumented).
    // Applets are no longer supported in modern browsers.
//    if (event.getKeyCode() == KeyEvent.VK_HOME)
//      try {
//         getAppletContext().showDocument(new URL(copyLink)); //replace [dave]
//      }
//      catch (Exception excp) {}
  }

  /**
   * Listen for key releases.
   */
  @Override
  public void keyReleased(KeyEvent event) {
    // Check if any cursor keys where released and set flags.
    if (event.getKeyCode() == KeyEvent.VK_LEFT) left = false;
    if (event.getKeyCode() == KeyEvent.VK_RIGHT) right = false;
    if (event.getKeyCode() == KeyEvent.VK_UP) up = false;
    if (event.getKeyCode() == KeyEvent.VK_DOWN) down = false;

    if (!up && !down && thrustersPlaying) {
      Sound.getThrustersSound().stop();
      thrustersPlaying = false;
    }
  }

  @Override
  public void keyTyped(KeyEvent event) {}

  /**
   * Paint all graphics(objects) onto the screen.
   */
  public void paint(Graphics graphics) {
    Dimension d = getSize();
    int i;
    int c;
    String s;
    int w, h;
    int x, y;

    // Create the off screen graphics context, if no good one exists.
    if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
      offDimension = d;
      offImage = createImage(d.width, d.height);
      offGraphics = offImage.getGraphics();
    }

    // Fill in background and stars.
    offGraphics.setColor(Color.black);
    offGraphics.fillRect(0, 0, d.width, d.height);
    if (detail) {
      offGraphics.setColor(Color.white);
      for (i = 0; i < numStars; i++)
        offGraphics.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
    }

    // Draw bullets
    offGraphics.setColor(Color.white);
    for (i = 0; i < MAX_SHOTS; i++)
      if (ship.getBullet(i).isActive())
        offGraphics.drawPolygon(ship.getBullet(i).getSprite());

    // Draw the guided missile, counter is used to quickly fade color to black
    // when near expiration.
    c = Math.min(missileCounter * 24, 255);
    offGraphics.setColor(new Color(c, c, c));
    if (ufo.getMissile().isActive()) {
      offGraphics.drawPolygon(ufo.getMissile().getSprite());
      offGraphics.drawLine(ufo.getMissile().getSprite().xpoints[ufo.getMissile().getSprite().npoints - 1],
          ufo.getMissile().getSprite().ypoints[ufo.getMissile().getSprite().npoints - 1],
          ufo.getMissile().getSprite().xpoints[0], ufo.getMissile().getSprite().ypoints[0]);
    }

    // Draw the asteroids.
    for (i = 0; i < MAX_ROCKS; i++)
      if (asteroids[i].isActive()) {
        if (detail) {
          offGraphics.setColor(Color.black);
          offGraphics.fillPolygon(asteroids[i].getSprite());
        }
        offGraphics.setColor(Color.white);
        offGraphics.drawPolygon(asteroids[i].getSprite());
        offGraphics.drawLine(asteroids[i].getSprite().xpoints[asteroids[i].getSprite().npoints - 1],
            asteroids[i].getSprite().ypoints[asteroids[i].getSprite().npoints - 1],
            asteroids[i].getSprite().xpoints[0], asteroids[i].getSprite().ypoints[0]);
      }

    // Draw the flying saucer.
    if (ufo.isActive()) {
      if (detail) {
        offGraphics.setColor(Color.black);
        offGraphics.fillPolygon(ufo.getSprite());
      }
      offGraphics.setColor(Color.white);
      offGraphics.drawPolygon(ufo.getSprite());
      offGraphics.drawLine(ufo.getSprite().xpoints[ufo.getSprite().npoints - 1],
          ufo.getSprite().ypoints[ufo.getSprite().npoints - 1],
          ufo.getSprite().xpoints[0], ufo.getSprite().ypoints[0]);
    }

    // Draw the ship, counter is used to fade color to white on hyperspace.
    c = 255 - (255 / HYPER_COUNT) * hyperCounter;
    if (ship.isActive()) {
      if (detail && hyperCounter == 0) {
        offGraphics.setColor(Color.black);
        offGraphics.fillPolygon(ship.getSprite());
      }
      offGraphics.setColor(new Color(c, c, c));
      offGraphics.drawPolygon(ship.getSprite());

      offGraphics.drawLine(ship.getSprite().xpoints[ship.getSprite().npoints - 1],
          ship.getSprite().ypoints[ship.getSprite().npoints - 1],
          ship.getSprite().xpoints[0], ship.getSprite().ypoints[0]);

      // Draw thruster exhaust if thrusters are on. Do it randomly to get a
      // flicker effect.
      if (!paused && detail && Math.random() < 0.5) {
        if (up) {
          offGraphics.drawPolygon(ship.getFwdThruster().getSprite());
          offGraphics.drawLine(ship.getFwdThruster().getSprite().xpoints[ship.getFwdThruster().getSprite().npoints - 1],
              ship.getFwdThruster().getSprite().ypoints[ship.getFwdThruster().getSprite().npoints - 1],
              ship.getFwdThruster().getSprite().xpoints[0], ship.getFwdThruster().getSprite().ypoints[0]);
        }
        if (down) {
          offGraphics.drawPolygon(ship.getRevThruster().getSprite());
          offGraphics.drawLine(ship.getRevThruster().getSprite().xpoints[ship.getRevThruster().getSprite().npoints - 1],
              ship.getRevThruster().getSprite().ypoints[ship.getRevThruster().getSprite().npoints - 1],
              ship.getRevThruster().getSprite().xpoints[0], ship.getRevThruster().getSprite().ypoints[0]);
        }
      }
    }

    // Draw any explosion debris, counters are used to fade color to black.
    for (i = 0; i < MAX_SCRAP; i++)
      if (explosions[i].isActive()) {
        c = (255 / SCRAP_COUNT) * explosionCounter [i];
        offGraphics.setColor(new Color(c, c, c));
        offGraphics.drawPolygon(explosions[i].getSprite());
      }

    // Display status and messages.
    offGraphics.setFont(font);
    offGraphics.setColor(Color.white);
    offGraphics.drawString("Score: " + score, fontWidth, fontHeight);
    offGraphics.drawString("Ships: " + shipsLeft, fontWidth, d.height - (fontHeight * 2));
    s = "High: " + highScore;
    offGraphics.drawString(s, d.width - ((fontWidth) + fm.stringWidth(s)), fontHeight);
    if (!sound) {
      s = "Mute";
      offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - (fontHeight * 2));
    }

    if (!playing) {
      s = COPY_NAME;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
      s = COPY_VERSION;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
      s = COPY_INFO;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
      s = COPY_LINK;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
      if (!isLoaded) {
        s = "Loading sounds...";
        w = 4 * fontWidth + fm.stringWidth(s);
        h = fontHeight;
        x = (d.width - w) / 2;
        y = 3 * d.height / 4 - fm.getMaxAscent();
        offGraphics.setColor(Color.black);
        offGraphics.fillRect(x, y, w, h);
        offGraphics.setColor(Color.gray);
        if (Sound.getClipTotal() > 0)
          offGraphics.fillRect(x, y, w * Sound.getClipsLoaded() / Sound.getClipTotal(), h);
        offGraphics.setColor(Color.white);
        offGraphics.drawRect(x, y, w, h);
        offGraphics.drawString(s, x + 2 * fontWidth, y + fm.getMaxAscent());
      }
      else {
        s = "Game Over";
        offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
        s = "'S' to Start";
        offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4 + fontHeight);
      }
    }
    else if (paused) {
      s = "Game Paused";
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
    }

    // Copy the off screen buffer to the screen.
    graphics.drawImage(offImage, 0, 0, this);
  }

  /**
   * Update screen graphics if needed.
   * Doesn't clears the background.
   */
  public void update(Graphics graphics) {
    paint(graphics);
  }
}