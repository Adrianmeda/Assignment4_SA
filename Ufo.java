public class Ufo extends AsteroidsSprite{

    static final int UFO_PASSES = 3;
    static final int UFO_POINTS    = 250;
    static final int NEW_UFO_POINTS  = 2750;

    public Ufo() {
        this.shape.addPoint(-15, 0);
        this.shape.addPoint(-10, -5);
        this.shape.addPoint(-5, -5);
        this.shape.addPoint(-5, -8);
        this.shape.addPoint(5, -8);
        this.shape.addPoint(5, -5);
        this.shape.addPoint(10, -5);
        this.shape.addPoint(15, 0);
        this.shape.addPoint(10, 5);
        this.shape.addPoint(-10, 5);

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        this.active = true;
        this.x = -AsteroidsSprite.width / 2;
        this.y = Math.random() * 2 * AsteroidsSprite.height - AsteroidsSprite.height;
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = Game.MAX_ROCK_SPEED / 2 + Math.random() * (Game.MAX_ROCK_SPEED / 2);
        this.deltaX = speed * -Math.sin(angle);
        this.deltaY = speed *  Math.cos(angle);
        if (Math.random() < 0.5) {
            this.x = AsteroidsSprite.width / 2;
            this.deltaX = -this.deltaX;
        }
        if (this.y > 0)
            this.deltaY = this.deltaY;
    }

    public void stop() {
        this.active = false;
    }

}
