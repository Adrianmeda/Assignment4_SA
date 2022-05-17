public class Missile extends AsteroidsSprite{

    public Missile(double ufoX, double ufoY) {
        this.getShape().addPoint(0, -4);
        this.getShape().addPoint(1, -3);
        this.getShape().addPoint(1, 3);
        this.getShape().addPoint(2, 4);
        this.getShape().addPoint(-2, 4);
        this.getShape().addPoint(-1, 3);
        this.getShape().addPoint(-1, -3);

        this.setActive(true);
        this.setAngle(0.0);
        this.setDeltaAngle(0.0);
        this.setX(ufoX);
        this.setY(ufoY);
        this.setX(0.0);
        this.setY(0.0);

    }

    public void guide(Ship ship) {
        // Find the angle needed to hit the ship.
        double dx, dy, angle;
        dx = ship.getX() - this.getX();
        dy = ship.getY() - this.getY();
        if (dx == 0 && dy == 0)
            angle = 0;
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

        this.setAngle(angle - Math.PI / 2);

        // Change the missle's angle so that it points toward the ship.

        this.setDeltaX(0.75 * Game.MAX_ROCK_SPEED * -Math.sin(this.getAngle()));
        this.setDeltaY(0.75 * Game.MAX_ROCK_SPEED *  Math.cos(this.getAngle()));
    }
}
