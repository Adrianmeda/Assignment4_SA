import java.applet.Applet;
import java.awt.*;


public class View extends Applet{

    String copyName = "Asteroids";
    String copyVers = "Version 1.3";
    String copyInfo = "Copyright 1998-2001 by Mike Hall";
    String copyLink = "http://www.brainjar.com";
    String copyText = copyName + '\n' + copyVers + '\n'
            + copyInfo + '\n' + copyLink;

    //Constants
    static final int DELAY = 20;
    static final int FPS = Math.round(1000 / DELAY);
    static final int MAX_SCRAP = 40;
    //Off screen image
    Graphics offGraphics;
    Image     offImage;
    Dimension offDimension;
    //Data for the screen font
    Font font      = new Font("Helvetica", Font.BOLD, 12);
    FontMetrics fm;
    int fontWidth  ;
    int fontHeight;
    Missile missile;
    Asteroid[] asteroids;
    Ship ship;
    Ufo ufo;
    Explosion[] explosions;
    Photon[] photons;
    Point[] stars;
    Dimension d;


    public View (FontMetrics fm, Dimension d, Graphics offGraphics, Image offImage, Dimension offDimension, Missile missile, Asteroid[] asteroids, Ship ship, Ufo ufo, Explosion[] explosions, Photon[] photons, Point[] stars){
        this.fm = fm;
        fontWidth = fm.getMaxAdvance();
        fontHeight = fm.getHeight();
        this.d = d;
        this.offGraphics = offGraphics;
        this.offImage = offImage;
        this.offDimension = offDimension;
        this.missile = missile;
        this.asteroids = asteroids;
        this.ship = ship;
        this.ufo = ufo;
        this.explosions = explosions;
        this.photons = photons;
        this.stars = stars;
    }

    public void paint(Graphics g) {

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
        if (Asteroids.detail) {
            offGraphics.setColor(Color.white);
            for (i = 0; i < Asteroids.numStars; i++)
                offGraphics.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
        }

        // Draw photon bullets.

        offGraphics.setColor(Color.white);
        for (i = 0; i < Asteroids.MAX_SHOTS; i++)
            if (photons[i].active)
                offGraphics.drawPolygon(photons[i].sprite);

        // Draw the guided missle, counter is used to quickly fade color to black
        // when near expiration.

        c = Math.min(Asteroids.missleCounter * 24, 255);
        offGraphics.setColor(new Color(c, c, c));
        if (missile.active) {
            offGraphics.drawPolygon(missile.sprite);
            offGraphics.drawLine(missile.sprite.xpoints[missile.sprite.npoints - 1], missile.sprite.ypoints[missile.sprite.npoints - 1],
                    missile.sprite.xpoints[0], missile.sprite.ypoints[0]);
        }

        // Draw the asteroids.

        for (i = 0; i < Asteroids.MAX_ROCKS; i++)
            if (asteroids[i].active) {
                if (Asteroids.detail) {
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
            if (Asteroids.detail) {
                offGraphics.setColor(Color.black);
                offGraphics.fillPolygon(ufo.sprite);
            }
            offGraphics.setColor(Color.white);
            offGraphics.drawPolygon(ufo.sprite);
            offGraphics.drawLine(ufo.sprite.xpoints[ufo.sprite.npoints - 1], ufo.sprite.ypoints[ufo.sprite.npoints - 1],
                    ufo.sprite.xpoints[0], ufo.sprite.ypoints[0]);
        }

        // Draw the ship, counter is used to fade color to white on hyperspace.

        c = 255 - (255 / Asteroids.HYPER_COUNT) * Asteroids.hyperCounter;
        if (ship.active) {
            if (Asteroids.detail && Asteroids.hyperCounter == 0) {
                offGraphics.setColor(Color.black);
                offGraphics.fillPolygon(ship.sprite);
            }
            offGraphics.setColor(new Color(c, c, c));
            offGraphics.drawPolygon(ship.sprite);
            offGraphics.drawLine(ship.sprite.xpoints[ship.sprite.npoints - 1], ship.sprite.ypoints[ship.sprite.npoints - 1],
                    ship.sprite.xpoints[0], ship.sprite.ypoints[0]);

            // Draw thruster exhaust if thrusters are on. Do it randomly to get a
            // flicker effect.

            if (!Asteroids.paused && Asteroids.detail && Math.random() < 0.5) {
                if (Asteroids.up) {
                    offGraphics.drawPolygon(ship.getFwdThruster().sprite);
                    offGraphics.drawLine(ship.getFwdThruster().sprite.xpoints[ship.getFwdThruster().sprite.npoints - 1], ship.getFwdThruster().sprite.ypoints[ship.getFwdThruster().sprite.npoints - 1],
                            ship.getFwdThruster().sprite.xpoints[0], ship.getFwdThruster().sprite.ypoints[0]);
                }
                if (Asteroids.down) {
                    offGraphics.drawPolygon(ship.getRevThruster().sprite);
                    offGraphics.drawLine(ship.getRevThruster().sprite.xpoints[ship.getRevThruster().sprite.npoints - 1], ship.getRevThruster().sprite.ypoints[ship.getRevThruster().sprite.npoints - 1],
                            ship.getRevThruster().sprite.xpoints[0], ship.getRevThruster().sprite.ypoints[0]);
                }
            }
        }

        // Draw any explosion debris, counters are used to fade color to black.

        for (i = 0; i < MAX_SCRAP; i++)
            if (explosions[i].active) {
                c = (255 / Asteroids.SCRAP_COUNT) * Asteroids.explosionCounter [i];
                offGraphics.setColor(new Color(c, c, c));
                offGraphics.drawPolygon(explosions[i].sprite);
            }

        // Display status and messages.

        offGraphics.setFont(font);
        offGraphics.setColor(Color.white);

        offGraphics.drawString("Score: " + Asteroids.score, fontWidth, fontHeight);
        offGraphics.drawString("Ships: " + Asteroids.shipsLeft, fontWidth, d.height - fontHeight);
        s = "High: " + Asteroids.highScore;
        offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);
        if (!Asteroids.sound) {
            s = "Mute";
            offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
        }

        if (!Asteroids.playing) {
            s = copyName;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
            s = copyVers;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
            s = copyInfo;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
            s = copyLink;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
            if (!Asteroids.loaded) {
                s = "Loading sounds...";
                w = 4 * fontWidth + fm.stringWidth(s);
                h = fontHeight;
                x = (d.width - w) / 2;
                y = 3 * d.height / 4 - fm.getMaxAscent();
                offGraphics.setColor(Color.black);
                offGraphics.fillRect(x, y, w, h);
                offGraphics.setColor(Color.gray);
                if (Asteroids.clipTotal > 0)
                    offGraphics.fillRect(x, y, (int) (w * Asteroids.clipsLoaded / Asteroids.clipTotal), h);
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
        else if (Asteroids.paused) {
            s = "Game Paused";
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
        }

        // Copy the off screen buffer to the screen.

        g.drawImage(offImage, 0, 0, this);
    }
}
