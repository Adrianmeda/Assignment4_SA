import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

public class Controller implements KeyListener, Runnable {

    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        // Check if any cursor keys where released and set flags.

        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            Asteroids.left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            Asteroids.right = false;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            Asteroids.up = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            Asteroids.down = false;

        if (!Asteroids.up && !Asteroids.down && Asteroids.thrustersPlaying) {
            Asteroids.thrustersSound.stop();
            Asteroids.thrustersPlaying = false;
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {

        char c;

        // Check if any cursor keys have been pressed and set flags.

        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            Asteroids.left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            Asteroids.right = true;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            Asteroids.up = true;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            Asteroids.down = true;

        if ((Asteroids.up || Asteroids.down) && Asteroids.ship.isActive() && !Asteroids.thrustersPlaying) {
            if (Asteroids.sound && !Asteroids.paused)
                Asteroids.thrustersSound.loop();
            Asteroids.thrustersPlaying = true;
        }

        // Spacebar: fire a photon and start its counter.

        if (e.getKeyChar() == ' ' && Asteroids.ship.isActive()) {
            if (Asteroids.sound & !Asteroids.paused)
                Asteroids.fireSound.play();
            Asteroids.photonTime = System.currentTimeMillis();
            Asteroids.photonIndex++;
            if (Asteroids.photonIndex >= Asteroids.MAX_SHOTS)
                Asteroids.photonIndex = 0;
            Asteroids.photons[Asteroids.photonIndex].active = true;
            Asteroids.photons[Asteroids.photonIndex].x = Asteroids.ship.x;
            Asteroids.photons[Asteroids.photonIndex].y = Asteroids.ship.y;
            Asteroids.photons[Asteroids.photonIndex].deltaX = 2 * Asteroids.MAX_ROCK_SPEED * -Math.sin(Asteroids.ship.angle);
            Asteroids.photons[Asteroids.photonIndex].deltaY = 2 * Asteroids.MAX_ROCK_SPEED *  Math.cos(Asteroids.ship.angle);
        }

        // Allow upper or lower case characters for remaining keys.

        c = Character.toLowerCase(e.getKeyChar());

        // 'H' key: warp ship into hyperspace by moving to a random location and
        // starting counter.

        if (c == 'h' && Asteroids.ship.isActive() && Asteroids.hyperCounter <= 0) {
            Asteroids.ship.x = Math.random() * AsteroidsSprite.width;
            Asteroids.ship.y = Math.random() * AsteroidsSprite.height;
            Asteroids.hyperCounter = Asteroids.HYPER_COUNT;
            if (Asteroids.sound & !Asteroids.paused)
                Asteroids.warpSound.play();
        }

        // 'P' key: toggle pause mode and start or stop any active looping sound
        // clips.

        if (c == 'p') {
            if (Asteroids.paused) {
                if (Asteroids.sound && Asteroids.misslePlaying)
                    Asteroids.missleSound.loop();
                if (Asteroids.sound && Asteroids.saucerPlaying)
                    Asteroids.saucerSound.loop();
                if (Asteroids.sound && Asteroids.thrustersPlaying)
                    Asteroids.thrustersSound.loop();
            }
            else {
                if (Asteroids.misslePlaying)
                    Asteroids.missleSound.stop();
                if (Asteroids.saucerPlaying)
                    Asteroids.saucerSound.stop();
                if (Asteroids.thrustersPlaying)
                    Asteroids.thrustersSound.stop();
            }
            Asteroids.paused = !Asteroids.paused;
        }

        // 'M' key: toggle sound on or off and stop any looping sound clips.

        if (c == 'm' && Asteroids.loaded) {
            if (Asteroids.sound) {
                Asteroids.crashSound.stop();
                Asteroids.explosionSound.stop();
                Asteroids.fireSound.stop();
                Asteroids.missleSound.stop();
                Asteroids.saucerSound.stop();
                Asteroids.thrustersSound.stop();
                Asteroids.warpSound.stop();
            }
            else {
                if (Asteroids.misslePlaying && !Asteroids.paused)
                    Asteroids.missleSound.loop();
                if (Asteroids.saucerPlaying && !Asteroids.paused)
                    Asteroids.saucerSound.loop();
                if (Asteroids.thrustersPlaying && !Asteroids.paused)
                    Asteroids.thrustersSound.loop();
            }
            Asteroids.sound = !Asteroids.sound;
        }

        // 'D' key: toggle graphics detail on or off.

        if (c == 'd')
            Asteroids.detail = !Asteroids.detail;

        // 'S' key: start the game, if not already in progress.

        if (c == 's' && Asteroids.loaded && !Asteroids.playing)
            Asteroids.initGame();

        // 'HOME' key: jump to web site (undocumented).

    }

    @Override
    public void run() {

    }
}



