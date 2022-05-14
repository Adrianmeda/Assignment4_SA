/******************************************************************************
  Asteroids, Version 1.3

  Copyright 1998-2001 by Mike Hall.
  Please see http://www.brainjar.com for terms of use.

  Revision History:

  1.01, 12/18/1999: Increased number of active photons allowed.
                    Improved explosions for more realism.
                    Added progress bar for loading of sound clips.
  1.2,  12/23/1999: Increased frame rate for smoother animation.
                    Modified code to calculate game object speeds and timer
                    counters based on the frame rate so they will remain
                    constant.
                    Improved speed limit checking for ship.
                    Removed wrapping of photons around screen and set a fixed
                    firing rate.
                    Added sprites for ship's thrusters.
  1.3,  01/25/2001: Updated to JDK 1.1.8.

  Usage:

  <applet code="Asteroids.class" width=w height=h></applet>

  Keyboard Controls:

  S            - Start Game    P           - Pause Game
  Cursor Left  - Rotate Left   Cursor Up   - Fire Thrusters
  Cursor Right - Rotate Right  Cursor Down - Fire Retro Thrusters
  Spacebar     - Fire Cannon   H           - Hyperspace
  M            - Toggle Sound  D           - Toggle Graphics Detail

******************************************************************************/

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;

/******************************************************************************
  The AsteroidsSprite class defines a game object, including it's shape,
  position, movement and rotation. It also can detemine if two objects collide.
******************************************************************************/

abstract class AsteroidsSprite {

  // Fields:

  static int width;          // Dimensions of the graphics area.
  static int height;

  Polygon shape;             // Base sprite shape, centered at the origin (0,0).
  boolean active;            // Active flag.
  double  angle;             // Current angle of rotation.
  double  deltaAngle;        // Amount to change the rotation angle.
  double  x, y;              // Current position on screen.
  double  deltaX, deltaY;    // Amount to change the screen position.
  Polygon sprite;            // Final location and shape of sprite after
                             // applying rotation and translation to get screen
                             // position. Used for drawing on the screen and in
                             // detecting collisions.

  // Constructors:

  public AsteroidsSprite() {

    this.shape = new Polygon();
    this.active = false;
    this.angle = 0.0;
    this.deltaAngle = 0.0;
    this.x = 0.0;
    this.y = 0.0;
    this.deltaX = 0.0;
    this.deltaY = 0.0;
    this.sprite = new Polygon();
  }

  // Methods:

  public boolean advance() {

    boolean wrapped;

    // Update the rotation and position of the sprite based on the delta
    // values. If the sprite moves off the edge of the screen, it is wrapped
    // around to the other side and TRUE is returnd.

    this.angle += this.deltaAngle;
    if (this.angle < 0)
      this.angle += 2 * Math.PI;
    if (this.angle > 2 * Math.PI)
      this.angle -= 2 * Math.PI;
    wrapped = false;
    this.x += this.deltaX;
    if (this.x < -width / 2) {
      this.x += width;
      wrapped = true;
    }
    if (this.x > width / 2) {
      this.x -= width;
      wrapped = true;
    }
    this.y -= this.deltaY;
    if (this.y < -height / 2) {
      this.y += height;
      wrapped = true;
    }
    if (this.y > height / 2) {
      this.y -= height;
      wrapped = true;
    }

    return wrapped;
  }

  public void render() {

    int i;

    // Render the sprite's shape and location by rotating it's base shape and
    // moving it to it's proper screen position.

    this.sprite = new Polygon();
    for (i = 0; i < this.shape.npoints; i++)
      this.sprite.addPoint((int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) + this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
                           (int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) + (int) Math.round(this.y) + height / 2);
  }

  public boolean isColliding(AsteroidsSprite s) {

    int i;

    // Determine if one sprite overlaps with another, i.e., if any vertice
    // of one sprite lands inside the other.

    for (i = 0; i < s.sprite.npoints; i++)
      if (this.sprite.contains(s.sprite.xpoints[i], s.sprite.ypoints[i]))
        return true;
    for (i = 0; i < this.sprite.npoints; i++)
      if (s.sprite.contains(this.sprite.xpoints[i], this.sprite.ypoints[i]))
        return true;
    return false;
  }

  public void stop() {
    this.active = false;
  }

  public boolean isActive() {
    return this.active;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public void activate() {
    this.active = true;
  }

  public Shape getShape() { return  this.shape; }

  public void explode(Explosion[] explosions, int[] explosionCounter, boolean detail) {
    int c, i, j;
    int cx, cy;

    c = 2;
    if (detail || this.sprite.npoints < 6)
      c = 1;
    for (i = 0; i < this.sprite.npoints; i += c) {
      Asteroids.explosionIndex++;
      if (Asteroids.explosionIndex >= Asteroids.MAX_SCRAP)
        Asteroids.explosionIndex = 0;
      explosions[Asteroids.explosionIndex].activate();
      explosions[Asteroids.explosionIndex].shape = new Polygon();
      j = i + 1;
      if (j >= this.sprite.npoints)
        j -= this.sprite.npoints;
      cx = (int) ((this.shape.xpoints[i] + this.shape.xpoints[j]) / 2);
      cy = (int) ((this.shape.ypoints[i] + this.shape.ypoints[j]) / 2);
      explosions[Asteroids.explosionIndex].shape.addPoint(
              this.shape.xpoints[i] - cx,
              this.shape.ypoints[i] - cy);
      explosions[Asteroids.explosionIndex].shape.addPoint(
              this.shape.xpoints[j] - cx,
              this.shape.ypoints[j] - cy);
      explosions[Asteroids.explosionIndex].x = this.x + cx;
      explosions[Asteroids.explosionIndex].y = this.y + cy;
      explosions[Asteroids.explosionIndex].angle = this.angle;
      explosions[Asteroids.explosionIndex].deltaAngle = 4 * (Math.random() * 2 * Asteroids.MAX_ROCK_SPIN - Asteroids.MAX_ROCK_SPIN);
      explosions[Asteroids.explosionIndex].deltaX = (Math.random() * 2 * Asteroids.MAX_ROCK_SPEED - Asteroids.MAX_ROCK_SPEED + this.deltaX) / 2;
      explosions[Asteroids.explosionIndex].deltaY = (Math.random() * 2 * Asteroids.MAX_ROCK_SPEED - Asteroids.MAX_ROCK_SPEED + this.deltaY) / 2;
      explosionCounter[Asteroids.explosionIndex] = Asteroids.SCRAP_COUNT;
    }

  }

}

/******************************************************************************
  Main applet code.
******************************************************************************/

public class Asteroids extends Applet implements Runnable {

  // Copyright information.

  String copyName = "Asteroids";
  String copyVers = "Version 1.3";
  String copyInfo = "Copyright 1998-2001 by Mike Hall";
  static String copyLink = "http://www.brainjar.com";
  String copyText = copyName + '\n' + copyVers + '\n'
                  + copyInfo + '\n' + copyLink;

  // Thread control variables.

  Thread loadThread;
  Thread loopThread;

  // Constants

  static final int DELAY = 20;             // Milliseconds between screen and
  static final int FPS   =                 // the resulting frame rate.
    Math.round(1000 / DELAY);

  static final int MAX_SHOTS =  8;          // Maximum number of sprites
  static final int MAX_ROCKS =  8;          // for photons, asteroids and
  static final int MAX_SCRAP = 40;          // explosions.

  static final int SCRAP_COUNT  = 2 * FPS;  // Timer counter starting values
  static final int HYPER_COUNT  = 3 * FPS;  // calculated using number of
  static final int MISSLE_COUNT = 4 * FPS;  // seconds x frames per second.
  static final int STORM_PAUSE  = 2 * FPS;

  static final int    MIN_ROCK_SIDES =   6; // Ranges for asteroid shape, size
  static final int    MAX_ROCK_SIDES =  16; // speed and rotation.
  static final int    MIN_ROCK_SIZE  =  20;
  static final int    MAX_ROCK_SIZE  =  40;
  static final double MIN_ROCK_SPEED =  40.0 / FPS;
  static final double MAX_ROCK_SPEED = 240.0 / FPS;
  static final double MAX_ROCK_SPIN  = Math.PI / FPS;

  static final int MAX_SHIPS = 3;           // Starting number of ships for
                                            // each game.
  static final int UFO_PASSES = 3;          // Number of passes for flying
                                            // saucer per appearance.

  // Ship's rotation and acceleration rates and maximum speed.

  static final double SHIP_ANGLE_STEP = Math.PI / FPS;
  static final double SHIP_SPEED_STEP = 15.0 / FPS;
  static final double MAX_SHIP_SPEED  = 1.25 * MAX_ROCK_SPEED;

  static final int FIRE_DELAY = 50;         // Minimum number of milliseconds
                                            // required between photon shots.

  // Probablility of flying saucer firing a missle during any given frame
  // (other conditions must be met).

  static final double MISSLE_PROBABILITY = 0.45 / FPS;

  static final int BIG_POINTS    =  25;     // Points scored for shooting
  static final int SMALL_POINTS  =  50;     // various objects.
  static final int UFO_POINTS    = 250;
  static final int MISSLE_POINTS = 500;

  // Number of points the must be scored to earn a new ship or to cause the
  // flying saucer to appear.

  static final int NEW_SHIP_POINTS = 5000;
  static final int NEW_UFO_POINTS  = 2750;

  // Background stars.

  static int     numStars;
  static Point[] stars;

  // Game data.

  static int score;
  static int highScore;
  static int newShipScore;
  static int newUfoScore;

  // Flags for game state and options.

  static boolean loaded = false;
  static boolean paused;
  static boolean playing;
  static boolean sound;
  static boolean detail;

  // Key flags.

  static boolean left  = false;
  static boolean right = false;
  static boolean up    = false;
  static boolean down  = false;

  // Sprite objects.

  static Ship     ship;
  static Ufo      ufo;
  static Missile missle;
  static Photon[] photons    = new Photon[Photon.MAX_SHOTS];
  static Asteroid[]  asteroids  = new Asteroid[MAX_ROCKS];
  static Explosion[] explosions = new Explosion[MAX_SCRAP];

  // Ship data.

  static int shipsLeft;       // Number of ships left in game, including current one.
  int shipCounter;     // Timer counter for ship explosion.
  static int hyperCounter;    // Timer counter for hyperspace.

  // Photon data.

  static int   photonIndex = 0;    // Index to next available photon sprite.
  static long  photonTime;     // Time value used to keep firing rate constant.

  // Flying saucer data.

  static int ufoPassesLeft;    // Counter for number of flying saucer passes.
  static int ufoCounter;       // Timer counter used to track each flying saucer pass.

  // Missle data.

  static int missleCounter;    // Counter for life of missle.

  // Asteroid data.

  static boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
  static int       asteroidsCounter;                            // Break-time counter.
  static double    asteroidsSpeed;                              // Asteroid speed.
  static int       asteroidsLeft;                               // Number of active asteroids.

  // Explosion data.

  static int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
  static int   explosionIndex;                         // Next available explosion sprite.

  // Sound clips.

  static AudioClip crashSound;
  static AudioClip explosionSound;
  static AudioClip fireSound;
  static AudioClip missleSound;
  static AudioClip saucerSound;
  static AudioClip thrustersSound;
  static AudioClip warpSound;

  // Flags for looping sound clips.

  static boolean thrustersPlaying;
  static boolean saucerPlaying;
  static boolean misslePlaying;

  // Counter and total used to track the loading of the sound clips.

  static int clipTotal   = 0;
  static int clipsLoaded = 0;

  // Off screen image.

  Dimension offDimension;
  Image     offImage;
  Graphics  offGraphics;

  // Data for the screen font.

  Font font      = new Font("Helvetica", Font.BOLD, 12);
  FontMetrics fm = getFontMetrics(font);
  int fontWidth  = fm.getMaxAdvance();
  int fontHeight = fm.getHeight();

  static Controller controller = new Controller();
  Game game = controller.getGame();

  public String getAppletInfo() {

    // Return copyright information.

    return(copyText);
  }

  public void init() {

    Dimension d = getSize();
    int i;

    // Display copyright information.

    System.out.println(copyText);

    // Set up key event handling and set focus to applet window.

    addKeyListener(controller);
    requestFocus();

    // Save the screen size.

    AsteroidsSprite.width = d.width;
    AsteroidsSprite.height = d.height;

    // Generate the starry background.

    numStars = AsteroidsSprite.width * AsteroidsSprite.height / 5000;
    stars = new Point[numStars];
    for (i = 0; i < numStars; i++)
      stars[i] = new Point((int) (Math.random() * AsteroidsSprite.width), (int) (Math.random() * AsteroidsSprite.height));

    // Create shape for the ship sprite.
    // Create shapes for the ship thrusters.
    ship = new Ship();

    // Create shape for each photon sprites.
    for (i = 0; i < MAX_SHOTS; i++) {
      photons[i] = new Photon();
    }

    // Create shape for the flying saucer.
    ufo = new Ufo();

    // Create shape for the guided missle.
    missle = new Missile(ufo.getX(), ufo.getY());

    // Create asteroid sprites.

    for (i = 0; i < MAX_ROCKS; i++)
      asteroids[i] = new Asteroid(asteroidsSpeed);

    // Create explosion sprites.

    for (i = 0; i < MAX_SCRAP; i++)
      explosions[i] = new Explosion();

    // Initialize game data and put us in 'game over' mode.

    highScore = 0;
    sound = true;
    detail = true;
    game.initGame();
    initGame();
    endGame();
  }

  public static void initGame() {

    // Initialize game data and sprites.

    score = 0;
    shipsLeft = MAX_SHIPS;
    asteroidsSpeed = MIN_ROCK_SPEED;
    newShipScore = NEW_SHIP_POINTS;
    newUfoScore = NEW_UFO_POINTS;
    initShip();

    stopUfo();
    stopMissle();
    initAsteroids();
    initExplosions();
    playing = true;
    paused = false;
    photonTime = System.currentTimeMillis();
  }

  public void endGame() {

    // Stop ship, flying saucer, guided missle and associated sounds.

    playing = false;
    stopShip();
    stopUfo();
    stopMissle();
  }

  public void start() {

    if (loopThread == null) {
      loopThread = new Thread(this);
      loopThread.start();
    }
    if (!loaded && loadThread == null) {
      loadThread = new Thread(this);
      loadThread.start();
    }
  }

  public void stop() {

    if (loopThread != null) {
      loopThread.stop();
      loopThread = null;
    }
    if (loadThread != null) {
      loadThread.stop();
      loadThread = null;
    }
  }

  public void run() {

    int i, j;
    long startTime;

    // Lower this thread's priority and get the current time.

    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    startTime = System.currentTimeMillis();

    // Run thread for loading sounds.

    if (!loaded && Thread.currentThread() == loadThread) {
      loadSounds();
      loaded = true;
      loadThread.stop();
    }

    // This is the main loop.

    while (Thread.currentThread() == loopThread) {

      if (!paused) {

        // Move and process all sprites.

        updateShip();
        updatePhotons();
        updateUfo();
        updateMissle();
        updateAsteroids();
        updateExplosions();

        // Check the score and advance high score, add a new ship or start the
        // flying saucer as necessary.

        if (score > highScore)
          highScore = score;
        if (score > newShipScore) {
          newShipScore += NEW_SHIP_POINTS;
          shipsLeft++;
        }
        if (playing && score > newUfoScore && !ufo.isActive()) {
          newUfoScore += NEW_UFO_POINTS;
          ufoPassesLeft = UFO_PASSES;
          initUfo();
        }

        // If all asteroids have been destroyed create a new batch.

        if (asteroidsLeft <= 0)
            if (--asteroidsCounter <= 0)
              initAsteroids();
      }

      // Update the screen and set the timer for the next loop.

      repaint();
      try {
        startTime += DELAY;
        Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
      }
      catch (InterruptedException e) {
        break;
      }
    }
  }

  public void loadSounds() {

    // Load all sound clips by playing and immediately stopping them. Update
    // counter and total for display.



    try {
      game.crashSound = getAudioClip(new URL(getCodeBase(), "crash.au"));
      clipTotal++;
      game.explosionSound = getAudioClip(new URL(getCodeBase(), "explosion.au"));
      clipTotal++;
      game.fireSound      = getAudioClip(new URL(getCodeBase(), "fire.au"));
      clipTotal++;
      game.missleSound    = getAudioClip(new URL(getCodeBase(), "missle.au"));
      clipTotal++;
      game.saucerSound    = getAudioClip(new URL(getCodeBase(), "saucer.au"));
      clipTotal++;
      game.thrustersSound = getAudioClip(new URL(getCodeBase(), "thrusters.au"));
      clipTotal++;
      game.warpSound      = getAudioClip(new URL(getCodeBase(), "warp.au"));
      clipTotal++;

      crashSound     = getAudioClip(new URL(getCodeBase(), "crash.au"));

      explosionSound = getAudioClip(new URL(getCodeBase(), "explosion.au"));

      fireSound      = getAudioClip(new URL(getCodeBase(), "fire.au"));

      missleSound    = getAudioClip(new URL(getCodeBase(), "missle.au"));

      saucerSound    = getAudioClip(new URL(getCodeBase(), "saucer.au"));

      thrustersSound = getAudioClip(new URL(getCodeBase(), "thrusters.au"));

      warpSound      = getAudioClip(new URL(getCodeBase(), "warp.au"));

    }
    catch (MalformedURLException e) {}

    try {
      game.crashSound.play();     game.crashSound.stop();     clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.explosionSound.play(); game.explosionSound.stop(); clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.fireSound.play();      game.fireSound.stop();      clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.missleSound.play();    game.missleSound.stop();    clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.saucerSound.play();    game.saucerSound.stop();    clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.thrustersSound.play(); game.thrustersSound.stop(); clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.warpSound.play();      game.warpSound.stop();      clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
    }
    catch (InterruptedException e) {}
  }

  public static void initShip() {

    // Reset the ship sprite at the center of the screen.

    ship = new Ship();

    if (loaded)
      thrustersSound.stop();
    thrustersPlaying = false;
    hyperCounter = 0;
  }

  public void updateShip() {

    if (!playing)
      return;

    // Rotate the ship if left or right cursor key is down.

    ship.update(right, left, up, down);

    // Move the ship. If it is currently in hyperspace, advance the countdown.

    if (ship.isActive()) {
      ship.advance();
      ship.render();
      if (hyperCounter > 0)
        hyperCounter--;

      // Update the thruster sprites to match the ship sprite.

      ship.updateThrusters();
      ship.getFwdThruster().render();
      ship.getRevThruster().render();
    }

    // Ship is exploding, advance the countdown or create a new ship if it is
    // done exploding. The new ship is added as though it were in hyperspace.
    // (This gives the player time to move the ship if it is in imminent
    // danger.) If that was the last ship, end the game.

    else
      if (--shipCounter <= 0)
        if (shipsLeft > 0) {
          initShip();
          hyperCounter = HYPER_COUNT;
        }
        else
          endGame();
  }

  public void stopShip() {

    ship.stop();
    shipCounter = SCRAP_COUNT;
    if (shipsLeft > 0)
      shipsLeft--;
    if (loaded)
      thrustersSound.stop();
    thrustersPlaying = false;
  }

  public void updatePhotons() {

    int i;

    // Move any active photons. Stop it when its counter has expired.

    for (i = 0; i < MAX_SHOTS; i++)
      if (photons[i].active) {
        if (!photons[i].advance())
          photons[i].render();
        else
          photons[i].active = false;
      }
  }

  public void initUfo() {
    ufo.render();
    saucerPlaying = true;
    if (sound)
      saucerSound.loop();
    ufoCounter = (int) Math.abs(AsteroidsSprite.width / ufo.deltaX);
  }

  public void updateUfo() {

    int i, d;
    boolean wrapped;

    // Move the flying saucer and check for collision with a photon. Stop it
    // when its counter has expired.

    if (ufo.isActive()) {
      if (--ufoCounter <= 0) {
        if (--ufoPassesLeft > 0)
          initUfo();
        else
          stopUfo();
      }
      if (ufo.isActive()) {
        ufo.advance();
        ufo.render();
        for (i = 0; i < MAX_SHOTS; i++)
          if (photons[i].active && ufo.isColliding(photons[i])) {
            if (sound)
              crashSound.play();
            explode(ufo);
            stopUfo();
            score += UFO_POINTS;
          }

          // On occassion, fire a missle at the ship if the saucer is not too
          // close to it.

          d = (int) Math.max(Math.abs(ufo.x - ship.x), Math.abs(ufo.y - ship.y));
          if (ship.isActive() && hyperCounter <= 0 &&
              ufo.active && !missle.active &&
              d > MAX_ROCK_SPEED * FPS / 2 &&
              Math.random() < MISSLE_PROBABILITY)
            initMissle();
       }
    }
  }

  public static void stopUfo() {
    ufo.stop();
    ufoCounter = 0;
    ufoPassesLeft = 0;
    if (loaded)
      saucerSound.stop();
    saucerPlaying = false;
  }

  public void initMissle() {
    missle.render();
    missleCounter = MISSLE_COUNT;
    if (sound)
      missleSound.loop();
    misslePlaying = true;
  }

  public void updateMissle() {

    // Move the guided missle and check for collision with ship or photon. Stop
    // it when its counter has expired.

    if (missle.isActive()) {
      if (--missleCounter <= 0)
        stopMissle();
      else {
        guideMissle();
        missle.advance();
        missle.render();
        for (int i = 0; i < MAX_SHOTS; i++)
          if (photons[i].isActive() && missle.isColliding(photons[i])) {
            if (sound)
              crashSound.play();
            explode(missle);
            stopMissle();
            score += MISSLE_POINTS;
          }
        if (missle.isActive() && ship.isActive() &&
            hyperCounter <= 0 && ship.isColliding(missle)) {
          if (sound)
            crashSound.play();
          explode(ship);
          stopShip();
          stopUfo();
          stopMissle();
        }
      }
    }
  }

  public void guideMissle() {

    if (!ship.isActive() || hyperCounter > 0)
      return;

    missle.guide(ship);
  }

  public static void stopMissle() {

    missle.stop();
    missleCounter = 0;
    if (loaded)
      missleSound.stop();
    misslePlaying = false;
  }

  public static void initAsteroids() {

    // Create random shapes, positions and movements for each asteroid.

    for (int i = 0; i < MAX_ROCKS; i++) {
      asteroids[i] = new Asteroid(asteroidsSpeed);
      asteroids[i].render();
      asteroidIsSmall[i] = false;
    }

    asteroidsCounter = STORM_PAUSE;
    asteroidsLeft = MAX_ROCKS;
    if (asteroidsSpeed < MAX_ROCK_SPEED)
      asteroidsSpeed += 0.5;
  }

  public void initSmallAsteroids(int n) {

    int count;
    int i;
    double tempX, tempY;

    // Create one or two smaller asteroids from a larger one using inactive
    // asteroids. The new asteroids will be placed in the same position as the
    // old one but will have a new, smaller shape and new, randomly generated
    // movements.

    count = 0;
    i = 0;
    tempX = asteroids[n].getX();
    tempY = asteroids[n].getY();
    do {
      if (!asteroids[i].isActive()) {
        asteroids[i].generateSmallAsteroid(tempX, tempY, asteroidsSpeed);
        asteroids[i].render();
        asteroidIsSmall[i] = true;
        count++;
        asteroidsLeft++;
      }
      i++;
    } while (i < MAX_ROCKS && count < 2);
  }

  public void updateAsteroids() {

    int i, j;

    // Move any active asteroids and check for collisions.

    for (i = 0; i < MAX_ROCKS; i++)
      if (asteroids[i].isActive()) {
        asteroids[i].advance();
        asteroids[i].render();

        // If hit by photon, kill asteroid and advance score. If asteroid is
        // large, make some smaller ones to replace it.

        for (j = 0; j < MAX_SHOTS; j++)
          if (photons[j].isActive() && asteroids[i].isActive() && asteroids[i].isColliding(photons[j])) {
            asteroidsLeft--;
            asteroids[i].stop();
            photons[j].stop();
            if (sound)
              explosionSound.play();
            explode(asteroids[i]);
            if (!asteroidIsSmall[i]) {
              score += BIG_POINTS;
              initSmallAsteroids(i);
            }
            else
              score += SMALL_POINTS;
          }

        // If the ship is not in hyperspace, see if it is hit.

        if (ship.isActive() && hyperCounter <= 0 &&
            asteroids[i].active && asteroids[i].isColliding(ship)) {
          if (sound)
            crashSound.play();
          explode(ship);
          stopShip();
          stopUfo();
          stopMissle();
        }
    }
  }

  public static void initExplosions() {

    int i;

    for (i = 0; i < MAX_SCRAP; i++) {
      explosions[i] = new Explosion();
      explosionCounter[i] = 0;
    }
    explosionIndex = 0;
  }

  public void explode(AsteroidsSprite s) {

    // Create sprites for explosion animation. The each individual line segment
    // of the given sprite is used to create a new sprite that will move
    // outward  from the sprite's original position with a random rotation.

    s.render();
    s.explode(explosions, explosionCounter, detail);

  }

  public void updateExplosions() {

    int i;

    // Move any active explosion debris. Stop explosion when its counter has
    // expired.

    for (i = 0; i < MAX_SCRAP; i++)
      if (explosions[i].isActive()) {
        explosions[i].advance();
        explosions[i].render();
        if (--explosionCounter[i] < 0)
          explosions[i].stop();
      }
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {


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

    // Draw photon bullets.

    offGraphics.setColor(Color.white);
    for (i = 0; i < MAX_SHOTS; i++)
      if (photons[i].active)
        offGraphics.drawPolygon(photons[i].sprite);

    // Draw the guided missle, counter is used to quickly fade color to black
    // when near expiration.

    c = Math.min(missleCounter * 24, 255);
    offGraphics.setColor(new Color(c, c, c));
    if (missle.active) {
      offGraphics.drawPolygon(missle.sprite);
      offGraphics.drawLine(missle.sprite.xpoints[missle.sprite.npoints - 1], missle.sprite.ypoints[missle.sprite.npoints - 1],
                           missle.sprite.xpoints[0], missle.sprite.ypoints[0]);
    }

    // Draw the asteroids.

    for (i = 0; i < MAX_ROCKS; i++)
      if (asteroids[i].active) {
        if (detail) {
          offGraphics.setColor(Color.black);
          offGraphics.fillPolygon(asteroids[i].sprite);
        }
        offGraphics.setColor(Color.white);
        offGraphics.drawPolygon(asteroids[i].sprite);
        offGraphics.drawLine(asteroids[i].sprite.xpoints[asteroids[i].sprite.npoints - 1], asteroids[i].sprite.ypoints[asteroids[i].sprite.npoints - 1],
                             asteroids[i].sprite.xpoints[0], asteroids[i].sprite.ypoints[0]);
      }

    // Draw the flying saucer.

    if (ufo.active) {
      if (detail) {
        offGraphics.setColor(Color.black);
        offGraphics.fillPolygon(ufo.sprite);
      }
      offGraphics.setColor(Color.white);
      offGraphics.drawPolygon(ufo.sprite);
      offGraphics.drawLine(ufo.sprite.xpoints[ufo.sprite.npoints - 1], ufo.sprite.ypoints[ufo.sprite.npoints - 1],
                           ufo.sprite.xpoints[0], ufo.sprite.ypoints[0]);
    }

    // Draw the ship, counter is used to fade color to white on hyperspace.

    c = 255 - (255 / HYPER_COUNT) * hyperCounter;
    if (ship.isActive()) {
      if (detail && hyperCounter == 0) {
        offGraphics.setColor(Color.black);
        offGraphics.fillPolygon(ship.sprite);
      }
      offGraphics.setColor(new Color(c, c, c));
      offGraphics.drawPolygon(ship.sprite);
      offGraphics.drawLine(ship.sprite.xpoints[ship.sprite.npoints - 1], ship.sprite.ypoints[ship.sprite.npoints - 1],
                           ship.sprite.xpoints[0], ship.sprite.ypoints[0]);

      // Draw thruster exhaust if thrusters are on. Do it randomly to get a
      // flicker effect.

      if (!paused && detail && Math.random() < 0.5) {
        if (up) {
          offGraphics.drawPolygon(ship.getFwdThruster().sprite);
          offGraphics.drawLine(ship.getFwdThruster().sprite.xpoints[ship.getFwdThruster().sprite.npoints - 1], ship.getFwdThruster().sprite.ypoints[ship.getFwdThruster().sprite.npoints - 1],
                  ship.getFwdThruster().sprite.xpoints[0], ship.getFwdThruster().sprite.ypoints[0]);
        }
        if (down) {
          offGraphics.drawPolygon(ship.getRevThruster().sprite);
          offGraphics.drawLine(ship.getRevThruster().sprite.xpoints[ship.getRevThruster().sprite.npoints - 1], ship.getRevThruster().sprite.ypoints[ship.getRevThruster().sprite.npoints - 1],
                  ship.getRevThruster().sprite.xpoints[0], ship.getRevThruster().sprite.ypoints[0]);
        }
      }
    }

    // Draw any explosion debris, counters are used to fade color to black.

    for (i = 0; i < MAX_SCRAP; i++)
      if (explosions[i].isActive()) {
        c = (255 / SCRAP_COUNT) * explosionCounter [i];
        offGraphics.setColor(new Color(c, c, c));
        offGraphics.drawPolygon(explosions[i].sprite);
      }

    // Display status and messages.

    offGraphics.setFont(font);
    offGraphics.setColor(Color.white);

    offGraphics.drawString("Score: " + score, fontWidth, fontHeight);
    offGraphics.drawString("Ships: " + shipsLeft, fontWidth, d.height - fontHeight);
    s = "High: " + highScore;
    offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);
    if (!sound) {
      s = "Mute";
      offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
    }

    if (!playing) {
      s = copyName;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
      s = copyVers;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
      s = copyInfo;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
      s = copyLink;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
      if (!loaded) {
        s = "Loading sounds...";
        w = 4 * fontWidth + fm.stringWidth(s);
        h = fontHeight;
        x = (d.width - w) / 2;
        y = 3 * d.height / 4 - fm.getMaxAscent();
        offGraphics.setColor(Color.black);
          offGraphics.fillRect(x, y, w, h);
        offGraphics.setColor(Color.gray);
        if (clipTotal > 0)
          offGraphics.fillRect(x, y, (int) (w * clipsLoaded / clipTotal), h);
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

    g.drawImage(offImage, 0, 0, this);
  }
}
