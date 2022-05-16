import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controller implements KeyListener {

    private Game game = new Game();

    public Game getGame() {
        return game;
    }

    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        // Check if any cursor keys where released and set flags.

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            game.setLeft(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            game.setRight(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP){
            game.setUp(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            game.setDown(false);

        }

        if (!game.isUp() && !game.isDown() && game.thrustersPlaying) {
            game.thrustersSound.stop();
            game.thrustersPlaying = false;
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {

        char c;

        // Check if any cursor keys have been pressed and set flags.

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            game.setLeft(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            game.setRight(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            game.setUp(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            game.setDown(true);
        }

        if ((game.isUp() || game.isDown()) && game.getShip().isActive() && !game.thrustersPlaying) {
            if (game.sound && !game.isPaused())
                game.thrustersSound.loop();
            game.thrustersPlaying = true;
        }

        // Spacebar: fire a photon and start its counter.

        if (e.getKeyChar() == ' ' && game.getShip().isActive()) {
            game.firePhoton();
        }

        // Allow upper or lower case characters for remaining keys.

        c = Character.toLowerCase(e.getKeyChar());

        // 'H' key: warp ship into hyperspace by moving to a random location and
        // starting counter.

        if (c == 'h' && game.getShip().isActive() && game.hyperCounter <= 0) {
           game.warpShip();
        }

        // 'P' key: toggle pause mode and start or stop any active looping sound
        // clips.

        if (c == 'p') {
            if (game.isPaused()) {
                if (game.sound && game.misslePlaying)
                    game.missleSound.loop();
                if (game.sound && game.saucerPlaying)
                    game.saucerSound.loop();
                if (game.sound && game.thrustersPlaying)
                    game.thrustersSound.loop();
            }
            else {
                if (game.misslePlaying)
                    game.missleSound.stop();
                if (game.saucerPlaying)
                    game.saucerSound.stop();
                if (game.thrustersPlaying)
                    game.thrustersSound.stop();
            }
            game.setPaused(!game.isPaused());

        }

        // 'M' key: toggle sound on or off and stop any looping sound clips.

        if (c == 'm' && game.isLoaded()) {
            if (game.sound) {
                game.mute();
            }
            else {
                if (game.misslePlaying && !game.paused)
                    game.missleSound.loop();
                if (game.saucerPlaying && !game.paused)
                    game.saucerSound.loop();
                if (game.thrustersPlaying && !game.paused)
                    game.thrustersSound.loop();
            }
            game.sound = !game.sound;
        }

        // 'D' key: toggle graphics detail on or off.

        if (c == 'd')
            game.setDetail(!game.isDetail());

        // 'S' key: start the game, if not already in progress.

        if (c == 's' && game.isLoaded() && !game.isPlaying())
            game.initGame();

        // 'HOME' key: jump to web site (undocumented).

    }


}



