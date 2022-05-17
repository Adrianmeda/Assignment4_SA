import java.applet.AudioClip;

public class Game {

    // Constants

   public static final int DELAY = 20;             // Milliseconds between screen and
   public static final int FPS =                 // the resulting frame rate.
            Math.round(1000 / DELAY);

   public static final int MAX_SHOTS = 8;          // Maximum number of sprites
   public static final int MAX_ROCKS = 8;          // for photons, asteroids and
   public static final int MAX_SCRAP = 40;          // explosions.

   public static final int SCRAP_COUNT = 2 * FPS;  // Timer counter starting values
   public static final int HYPER_COUNT = 3 * FPS;  // calculated using number of
   public static final int MISSLE_COUNT = 4 * FPS;  // seconds x frames per second.
   public static final int STORM_PAUSE = 2 * FPS;

   public static final int MIN_ROCK_SIDES = 6; // Ranges for asteroid shape, size
   public static final int MAX_ROCK_SIDES = 16; // speed and rotation.
   public static final int MIN_ROCK_SIZE = 20;
   public static final int MAX_ROCK_SIZE = 40;
   public static final double MIN_ROCK_SPEED = 40.0 / FPS;
   public static final double MAX_ROCK_SPEED = 240.0 / FPS;
   public static final double MAX_ROCK_SPIN = Math.PI / FPS;

    static final int MAX_SHIPS = 3;           // Starting number of ships for
    // each game.

    static final int UFO_PASSES = 3;          // Number of passes for flying
    // saucer per appearance.

    static final int FIRE_DELAY = 50;         // Minimum number of milliseconds
    // required between photon shots.

    // Probablility of flying saucer firing a missle during any given frame
    // (other conditions must be met).

    static final double MISSLE_PROBABILITY = 0.45 / FPS;

    static final int BIG_POINTS = 25;     // Points scored for shooting
    static final int SMALL_POINTS = 50;     // various objects.
    static final int UFO_POINTS = 250;
    static final int MISSLE_POINTS = 500;

    // Number of points the must be scored to earn a new ship or to cause the
    // flying saucer to appear.

    static final int NEW_SHIP_POINTS = 5000;
    static final int NEW_UFO_POINTS = 2750;

    // Game data.

    private int score;
    private int highScore;
    private int newShipScore;
    private int newUfoScore;

    // Flags for game state and options.

    private boolean loaded = false;
    private boolean paused;
    private boolean playing;
    private boolean sound;
    private boolean detail;

    // Key flags.

    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;

    // Sprite objects.

    private Ship ship;
    private Ufo ufo;
    private Missile missle;
    private Photon[] photons = new Photon[Photon.MAX_SHOTS];
    private Asteroid[] asteroids = new Asteroid[MAX_ROCKS];
    private Explosion[] explosions = new Explosion[MAX_SCRAP];

    // Ship data.

    private int shipsLeft;       // Number of ships left in game, including current one.
    private int shipCounter;     // Timer counter for ship explosion.
    public static int hyperCounter;    // Timer counter for hyperspace.


    // Photon data.

    private int photonIndex = 0;    // Index to next available photon sprite.
    private long photonTime;     // Time value used to keep firing rate constant.

    // Flying saucer data.

    private int ufoPassesLeft;    // Counter for number of flying saucer passes.
    private int ufoCounter;       // Timer counter used to track each flying saucer pass.

    // Missle data.

    private int missleCounter;    // Counter for life of missle.

    // Asteroid data.

    private boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
    private int asteroidsCounter;                            // Break-time counter.
    private double asteroidsSpeed;                              // Asteroid speed.
    private int asteroidsLeft;                               // Number of active asteroids.

    // Explosion data.

    private int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
    static int explosionIndex;                         // Next available explosion sprite.

    // Sound clips.

    AudioClip crashSound;
    AudioClip explosionSound;
    AudioClip fireSound;
    AudioClip missleSound;
    AudioClip saucerSound;
    AudioClip thrustersSound;
    AudioClip warpSound;

    // Flags for looping sound clips.

    private boolean thrustersPlaying;
    boolean saucerPlaying;
    boolean misslePlaying;

    // Counter and total used to track the loading of the sound clips.

    public int clipTotal = 0;
    public int clipsLoaded = 0;


    public Game() {

        // Create shape for the ship sprite.
        // Create shapes for the ship thrusters.
        ship = new Ship();

        for (int i = 0; i < MAX_SHOTS; i++) {
            photons[i] = new Photon();
        }

        // Create shape for the flying saucer.
        ufo = new Ufo();

        // Create shape for the guided missle.
        missle = new Missile(ufo.getX(), ufo.getY());

        // Create asteroid sprites.

        for (int i = 0; i < MAX_ROCKS; i++)
            asteroids[i] = new Asteroid(asteroidsSpeed);

        // Create explosion sprites.

        for (int i = 0; i < MAX_SCRAP; i++)
            explosions[i] = new Explosion();

        // Initialize game data and put us in 'game over' mode.

        highScore = 0;
        sound = true;
        detail = true;
    }

    //getters --------------------------------------------

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isPlaying() { return playing; }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isDetail() {
        return detail;
    }

    public boolean isThrustersPlaying() {
        return thrustersPlaying;
    }

    public boolean hasSound() {
        return this.sound;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getShipsLeft() {
        return shipsLeft;
    }

    public int[] getExplosionCounter() {
        return explosionCounter;
    }

    public int getMissleCounter() {
        return missleCounter;
    }

    public int getClipsLoaded() {
        return clipsLoaded;
    }

    public int getClipTotal() {
        return clipTotal;
    }

    public Asteroid[] getAsteroids() {
        return asteroids;
    }

    public Ship getShip() {
        return ship;
    }

    public Photon[] getPhotons() {
        return photons;
    }

    public Missile getMissle() {
        return missle;
    }

    public Ufo getUfo() {
        return ufo;
    }

    public Explosion[] getExplosions() {
        return explosions;
    }

    //setters ----------------------------------------------

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {this.right = right;}

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {this.down = down;}

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setDetail(boolean detail) {
        this.detail = detail;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public void setThrustersPlaying(boolean thrustersPlaying) {
        this.thrustersPlaying = thrustersPlaying;
    }

    //initiators ----------------------------------------------

    public void initGame() {
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

    private void initShip() {

        // Reset the ship sprite at the center of the screen.

        ship = new Ship();

        if (loaded)
            thrustersSound.stop();
        thrustersPlaying = false;
        hyperCounter = 0;
    }

    private void initAsteroids() {

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

    private void initSmallAsteroids(int n) {

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

    private void initExplosions() {

        int i;

        for (i = 0; i < MAX_SCRAP; i++) {
            explosions[i] = new Explosion();
            explosionCounter[i] = 0;
        }
        explosionIndex = 0;
    }

    private void initUfo() {
        ufo.render();
        saucerPlaying = true;
        if (sound)
            saucerSound.loop();
        ufoCounter = (int) Math.abs(AsteroidsSprite.width / ufo.getDeltaX());
    }

    private void initMissle() {
        missle.render();
        missleCounter = MISSLE_COUNT;
        if (sound)
            missleSound.loop();
        misslePlaying = true;
    }

    //stop methods ---------------------------------------------

    public void stopUfo() {
        ufo.stop();
        ufoCounter = 0;
        ufoPassesLeft = 0;
        if (loaded)
            saucerSound.stop();
        saucerPlaying = false;
    }

    public void stopMissle() {

        missle.stop();
        missleCounter = 0;
        if (loaded)
            missleSound.stop();
        misslePlaying = false;
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

    //update methods -----------------------------------------

    public boolean updateGame() {
        // Move and process all sprites.

        boolean shipUpdate = updateShip();
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

        return shipUpdate;

    }

    private boolean updateShip() {
        if (!playing)
            return false;

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
                return true; //add if true endGame in asteroids

        return false;
    }

    private void updatePhotons() {

        int i;

        // Move any active photons. Stop it when its counter has expired.

        for (i = 0; i < MAX_SHOTS; i++)
            if (photons[i].isActive()) {
                if (!photons[i].advance())
                    photons[i].render();
                else
                    photons[i].setActive(false);
            }
    }

    private void updateUfo() {

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
                    if (photons[i].isActive() && ufo.isColliding(photons[i])) {
                        if (sound)
                            crashSound.play();
                        explode(ufo);
                        stopUfo();
                        score += UFO_POINTS;
                    }

                // On occassion, fire a missle at the ship if the saucer is not too
                // close to it.

                d = (int) Math.max(Math.abs(ufo.getX() - ship.getY()), Math.abs(ufo.getY() - ship.getY()));
                if (ship.isActive() && hyperCounter <= 0 &&
                        ufo.isActive() && !missle.isActive() &&
                        d > MAX_ROCK_SPEED * FPS / 2 &&
                        Math.random() < MISSLE_PROBABILITY)
                    initMissle();
            }
        }
    }

    private void updateMissle() {

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

    private void updateAsteroids() {

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
                        asteroids[i].isActive() && asteroids[i].isColliding(ship)) {
                    if (sound)
                        crashSound.play();
                    explode(ship);
                    stopShip();
                    stopUfo();
                    stopMissle();
                }
            }
    }

    private void updateExplosions() {

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

    //others -------------------------------------------------

    public void explode(AsteroidsSprite s) {

        // Create sprites for explosion animation. The each individual line segment
        // of the given sprite is used to create a new sprite that will move
        // outward  from the sprite's original position with a random rotation.

        s.render();
        s.explode(explosions, explosionCounter, detail);

    }

    public void guideMissle() {

        if (!ship.isActive() || hyperCounter > 0)
            return;

        missle.guide(ship);
    }

    public void firePhoton() {
        if (sound & !paused)
            fireSound.play();
        photonTime = System.currentTimeMillis();
        photonIndex++;
        if (photonIndex >= Game.MAX_SHOTS)
            photonIndex = 0;
        photons[photonIndex].setActive(true);
        photons[photonIndex].setX(ship.getX());
        photons[photonIndex].setY(ship.getY());
        photons[photonIndex].setDeltaX(2 * MAX_ROCK_SPEED * -Math.sin(ship.getAngle()));
        photons[photonIndex].setDeltaY(2 * MAX_ROCK_SPEED *  Math.cos(ship.getAngle()));
    }

    public void warpShip() {
        ship.setX(Math.random() * AsteroidsSprite.width);
        ship.setY(Math.random() * AsteroidsSprite.height);
        hyperCounter = HYPER_COUNT;

        if (sound & !paused)
            warpSound.play();
    }

    public void mute() {
        crashSound.stop();
        explosionSound.stop();
        fireSound.stop();
        missleSound.stop();
        saucerSound.stop();
        thrustersSound.stop();
        warpSound.stop();
    }
}
