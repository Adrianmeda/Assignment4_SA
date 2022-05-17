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
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

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
  private Thread loadThread;
  private Thread loopThread;

  // Constants
  public static final int DELAY = 20;             // Milliseconds between screen and
  public static final int FPS   =                 // the resulting frame rate.
    Math.round(1000 / DELAY);

  // Background stars.
  private int     numStars;
  private Point[] stars;

  // Off screen image.
  private Dimension offDimension;
  private Image     offImage;
  private Graphics  offGraphics;

  // Data for the screen font.
  private Font font      = new Font("Helvetica", Font.BOLD, 12);
  private FontMetrics fm = getFontMetrics(font);
  private int fontWidth  = fm.getMaxAdvance();
  private int fontHeight = fm.getHeight();

  private static Controller controller = new Controller();
  private Game game = controller.getGame();

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
    this.fillBackgroundAndStars(d, stars, numStars, game.isDetail());

    // Draw photon bullets.
    this.drawPhotons(game.getPhotons());

    // Draw the guided missle, counter is used to quickly fade color to black
    // when near expiration.
    this.drawMissle(game.getMissleCounter(), game.getMissle());

    // Draw the asteroids.
    this.drawAsteroids(game.getAsteroids(), game.isDetail());

    // Draw the flying saucer.
    if (game.getUfo().isActive()) {
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

  private void fillBackgroundAndStars(Dimension d, Point[] stars, int numStars,boolean detailed) {
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
    offGraphics.drawLine(ufo.getSprite().xpoints[ufo.getSprite().npoints - 1], ufo.getSprite().ypoints[ufo.getSprite().npoints - 1],
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
    if (!game.hasSound()) {
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
