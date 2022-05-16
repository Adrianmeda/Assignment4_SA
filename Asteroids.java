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
  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public Polygon getSprite() {
    return sprite;
  }

  public boolean isActive() {
    return this.active;
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



  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }



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

  public void activate() {
    this.active = true;
  }

  public void stop() {
    this.active = false;
  }

  public void explode(Explosion[] explosions, int[] explosionCounter, boolean detail) {
    int c, i, j;
    int cx, cy;

    c = 2;
    if (detail || this.sprite.npoints < 6)
      c = 1;
    for (i = 0; i < this.sprite.npoints; i += c) {
      Game.explosionIndex++;
      if (Game.explosionIndex >= Game.MAX_SCRAP)
        Game.explosionIndex = 0;
      explosions[Game.explosionIndex].activate();
      explosions[Game.explosionIndex].shape = new Polygon();
      j = i + 1;
      if (j >= this.sprite.npoints)
        j -= this.sprite.npoints;
      cx = (int) ((this.shape.xpoints[i] + this.shape.xpoints[j]) / 2);
      cy = (int) ((this.shape.ypoints[i] + this.shape.ypoints[j]) / 2);
      explosions[Game.explosionIndex].shape.addPoint(
              this.shape.xpoints[i] - cx,
              this.shape.ypoints[i] - cy);
      explosions[Game.explosionIndex].shape.addPoint(
              this.shape.xpoints[j] - cx,
              this.shape.ypoints[j] - cy);
      explosions[Game.explosionIndex].x = this.x + cx;
      explosions[Game.explosionIndex].y = this.y + cy;
      explosions[Game.explosionIndex].angle = this.angle;
      explosions[Game.explosionIndex].deltaAngle = 4 * (Math.random() * 2 * Game.MAX_ROCK_SPIN - Game.MAX_ROCK_SPIN);
      explosions[Game.explosionIndex].deltaX = (Math.random() * 2 * Game.MAX_ROCK_SPEED - Game.MAX_ROCK_SPEED + this.deltaX) / 2;
      explosions[Game.explosionIndex].deltaY = (Math.random() * 2 * Game.MAX_ROCK_SPEED - Game.MAX_ROCK_SPEED + this.deltaY) / 2;
      explosionCounter[Game.explosionIndex] = Game.SCRAP_COUNT;
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

  // Background stars.
  static int     numStars;
  static Point[] stars;

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

    game.initGame();
    endGame();
  }

  public void endGame() {

    // Stop ship, flying saucer, guided missle and associated sounds.
    game.setPlaying(false);

    game.stopShip();
    game.stopUfo();
    game.stopMissle();
  }

  public void start() {

    if (loopThread == null) {
      loopThread = new Thread(this);
      loopThread.start();
    }
    if (!game.isLoaded() && loadThread == null) {
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
    long startTime;

    // Lower this thread's priority and get the current time.
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    startTime = System.currentTimeMillis();

    // Run thread for loading sounds.
    if (!game.isLoaded() && Thread.currentThread() == loadThread) {
      loadSounds();
      game.setLoaded(true);
      loadThread.stop();
    }

    // This is the main loop.
    while (Thread.currentThread() == loopThread) {
      if (!game.isPaused()) {

        boolean shipUpdate = game.updateGame();
        if (shipUpdate) endGame();

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
  }

  public void loadSounds() {

    // Load all sound clips by playing and immediately stopping them. Update
    // counter and total for display.

    try {
      game.crashSound = getAudioClip(new URL(getCodeBase(), "crash.au"));
      game.clipTotal++;
      game.explosionSound = getAudioClip(new URL(getCodeBase(), "explosion.au"));
      game.clipTotal++;
      game.fireSound      = getAudioClip(new URL(getCodeBase(), "fire.au"));
      game.clipTotal++;
      game.missleSound    = getAudioClip(new URL(getCodeBase(), "missle.au"));
      game.clipTotal++;
      game.saucerSound    = getAudioClip(new URL(getCodeBase(), "saucer.au"));
      game.clipTotal++;
      game.thrustersSound = getAudioClip(new URL(getCodeBase(), "thrusters.au"));
      game.clipTotal++;
      game.warpSound      = getAudioClip(new URL(getCodeBase(), "warp.au"));
      game.clipTotal++;

    }
    catch (MalformedURLException e) {}

    try {
      game.crashSound.play();     game.crashSound.stop();     game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.explosionSound.play(); game.explosionSound.stop(); game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.fireSound.play();      game.fireSound.stop();      game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.missleSound.play();    game.missleSound.stop();    game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.saucerSound.play();    game.saucerSound.stop();    game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.thrustersSound.play(); game.thrustersSound.stop(); game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
      game.warpSound.play();      game.warpSound.stop();      game.clipsLoaded++;
      repaint(); Thread.currentThread().sleep(DELAY);
    }
    catch (InterruptedException e) {}
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    Dimension d = getSize();

    // Create the off screen graphics context, if no good one exists.
    if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
      this.createOfScreen(d);
    }

    // Fill in background and stars.
    this.fillBackgroundAndStars(d, game.isDetail());

    // Draw photon bullets.
    this.drawPhotons(game.getPhotons());

    // Draw the guided missle, counter is used to quickly fade color to black
    // when near expiration.
    this.drawMissle(game.getMissleCounter(), game.getMissle());

    // Draw the asteroids.
    this.drawAsteroids(game.getAsteroids(), game.isDetail());

    // Draw the flying saucer.
    if (game.ufo.active) {
      this.drawFlyingSaucer(game.getUfo(), game.isDetail());
    }

    // Draw the ship, counter is used to fade color to white on hyperspace.
    this.drawShip(game.getShip(), game.isDetail(), game.isPaused(), game.isUp(), game.isDown());

    // Draw any explosion debris, counters are used to fade color to black.
    this.drawDebris(game.getExplosions(), game.getExplosionCounter());

    // Display status and messages.
    this.displayMessagesAndStatus(d);

    // Copy the off screen buffer to the screen.
    g.drawImage(offImage, 0, 0, this);
  }

  private void createOfScreen(Dimension d) {
    offDimension = d;
    offImage = createImage(d.width, d.height);
    offGraphics = offImage.getGraphics();
  }

  private void fillBackgroundAndStars(Dimension d, boolean detailed) {
    offGraphics.setColor(Color.black);
    offGraphics.fillRect(0, 0, d.width, d.height);
    if (detailed) {
      offGraphics.setColor(Color.white);
      for (int i = 0; i < numStars; i++)
        offGraphics.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
    }
  }

  private void drawPhotons(Photon[] photons) {
    offGraphics.setColor(Color.white);
    for (int i = 0; i < Game.MAX_SHOTS; i++)
      if (photons[i].isActive())
        offGraphics.drawPolygon(photons[i].getSprite());
  }

  private void drawMissle(int missleCounter, Missile missile) {
    int c = Math.min(missleCounter * 24, 255);
    offGraphics.setColor(new Color(c, c, c));
    if (missile.isActive()) {
      offGraphics.drawPolygon(missile.getSprite());
      offGraphics.drawLine(missile.getSprite().xpoints[missile.getSprite().npoints - 1], missile.getSprite().ypoints[missile.getSprite().npoints - 1],
              missile.getSprite().xpoints[0], missile.getSprite().ypoints[0]);
    }

  }

  private void drawAsteroids(Asteroid[] asteroids, boolean detailed) {
    for (int i = 0; i < Game.MAX_ROCKS; i++)
      if (asteroids[i].isActive()) {
        if (detailed) {
          offGraphics.setColor(Color.black);
          offGraphics.fillPolygon(game.getAsteroids()[i].getSprite());
        }
        offGraphics.setColor(Color.white);
        offGraphics.drawPolygon(asteroids[i].getSprite());
        offGraphics.drawLine(asteroids[i].getSprite().xpoints[asteroids[i].getSprite().npoints - 1], asteroids[i].getSprite().ypoints[asteroids[i].getSprite().npoints - 1],
                asteroids[i].getSprite().xpoints[0], asteroids[i].getSprite().ypoints[0]);
      }
  }

  private void drawFlyingSaucer(Ufo ufo, boolean detailed) {
    if (detailed) {
      offGraphics.setColor(Color.black);
      offGraphics.fillPolygon(ufo.getSprite());
    }
    offGraphics.setColor(Color.white);
    offGraphics.drawPolygon(ufo.getSprite());
    offGraphics.drawLine(ufo.getSprite().xpoints[game.ufo.sprite.npoints - 1], ufo.getSprite().ypoints[ufo.getSprite().npoints - 1],
            ufo.getSprite().xpoints[0], ufo.getSprite().ypoints[0]);

  }

  private void drawShip(Ship ship, boolean detailed, boolean paused, boolean up, boolean down) {
    int c = 255 - (255 / Game.HYPER_COUNT) * Game.hyperCounter;
    if (ship.isActive()) {
      if (detailed && Game.hyperCounter == 0) {
        offGraphics.setColor(Color.black);
        offGraphics.fillPolygon(ship.getSprite());
      }
      offGraphics.setColor(new Color(c, c, c));
      offGraphics.drawPolygon(ship.getSprite());
      offGraphics.drawLine(ship.getSprite().xpoints[ship.getSprite().npoints - 1], ship.getSprite().ypoints[ship.getSprite().npoints - 1],
              ship.getSprite().xpoints[0], ship.getSprite().ypoints[0]);

    }

    if (!paused && detailed && Math.random() < 0.5) {
      if (up) {
        offGraphics.drawPolygon(ship.getFwdThruster().getSprite());
        offGraphics.drawLine(ship.getFwdThruster().getSprite().xpoints[ship.getFwdThruster().getSprite().npoints - 1], ship.getFwdThruster().getSprite().ypoints[ship.getFwdThruster().getSprite().npoints - 1],
                ship.getFwdThruster().getSprite().xpoints[0], ship.getFwdThruster().getSprite().ypoints[0]);
      }
      if (down) {
        offGraphics.drawPolygon(ship.getRevThruster().getSprite());
        offGraphics.drawLine(ship.getRevThruster().getSprite().xpoints[ship.getRevThruster().getSprite().npoints - 1], ship.getRevThruster().getSprite().ypoints[ship.getRevThruster().getSprite().npoints - 1],
                ship.getRevThruster().getSprite().xpoints[0], ship.getRevThruster().getSprite().ypoints[0]);
      }
    }
  }

  private void drawDebris(Explosion[] explosions, int[] explosionCounter) {
    for (int i = 0; i < Game.MAX_SCRAP; i++)
      if (explosions[i].isActive()) {
        int c = (255 / Game.SCRAP_COUNT) * explosionCounter [i];
        offGraphics.setColor(new Color(c, c, c));
        offGraphics.drawPolygon(explosions[i].getSprite());
      }
  }

  private void displayMessagesAndStatus(Dimension d) {
    offGraphics.setFont(font);
    offGraphics.setColor(Color.white);

    offGraphics.drawString("Score: " + game.getScore(), fontWidth, fontHeight);
    offGraphics.drawString("Ships: " + game.getShipsLeft(), fontWidth, d.height - fontHeight);
    String s = "High: " + game.getHighScore();
    offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);
    if (!game.sound) {
      s = "Mute";
      offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
    }

    if (!game.isPlaying()) {
      s = copyName;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
      s = copyVers;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
      s = copyInfo;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
      s = copyLink;
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
      if (!game.isLoaded()) {
        s = "Loading sounds...";
        int w = 4 * fontWidth + fm.stringWidth(s);
        int h = fontHeight;
        int x = (d.width - w) / 2;
        int y = 3 * d.height / 4 - fm.getMaxAscent();
        offGraphics.setColor(Color.black);
        offGraphics.fillRect(x, y, w, h);
        offGraphics.setColor(Color.gray);
        if (game.clipTotal > 0)
          offGraphics.fillRect(x, y, (int) (w * game.getClipsLoaded() / game.getClipTotal()), h);
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
    else if (game.isPaused()) {
      s = "Game Paused";
      offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
    }
  }

}
