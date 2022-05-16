public class Missile extends AsteroidsSprite{

    public Missile(double ufoX, double ufoY) {
        this.shape.addPoint(0, -4);
        this.shape.addPoint(1, -3);
        this.shape.addPoint(1, 3);
        this.shape.addPoint(2, 4);
        this.shape.addPoint(-2, 4);
        this.shape.addPoint(-1, 3);
        this.shape.addPoint(-1, -3);

        this.active = true;
        this.angle = 0.0;
        this.deltaAngle = 0.0;
        this.x = ufoX;
        this.y = ufoY;
        this.deltaX = 0.0;
        this.deltaY = 0.0;

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

        this.angle = angle - Math.PI / 2;

        // Change the missle's angle so that it points toward the ship.

        this.deltaX = 0.75 * Game.MAX_ROCK_SPEED * -Math.sin(this.angle);
        this.deltaY = 0.75 * Game.MAX_ROCK_SPEED *  Math.cos(this.angle);
    }
}
