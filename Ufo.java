public class Ufo extends AsteroidsSprite{

    static final int UFO_PASSES = 3;
    static final int UFO_POINTS    = 250;
    static final int NEW_UFO_POINTS  = 2750;

    public Ufo() {
        this.getShape().addPoint(-15, 0);
        this.getShape().addPoint(-10, -5);
        this.getShape().addPoint(-5, -5);
        this.getShape().addPoint(-5, -8);
        this.getShape().addPoint(5, -8);
        this.getShape().addPoint(5, -5);
        this.getShape().addPoint(10, -5);
        this.getShape().addPoint(15, 0);
        this.getShape().addPoint(10, 5);
        this.getShape().addPoint(-10, 5);

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        this.setActive(true);
        this.setX(-AsteroidsSprite.width / 2);
        this.setY(Math.random() * 2 * AsteroidsSprite.height - AsteroidsSprite.height);
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = Game.MAX_ROCK_SPEED / 2 + Math.random() * (Game.MAX_ROCK_SPEED / 2);
        this.setDeltaX(speed * -Math.sin(angle));
        this.setDeltaY(speed *  Math.cos(angle));
        if (Math.random() < 0.5) {
            this.setX(AsteroidsSprite.width / 2);
            this.setDeltaX(-this.getDeltaX());
        }
        if (this.getY() > 0)
            this.setDeltaY(this.getDeltaY());
    }

    public void stop() {
        this.setActive(false);
    }

}
