public class Ship extends AsteroidsSprite{

    static final double SHIP_ANGLE_STEP = Math.PI / Game.FPS;
    static final double SHIP_SPEED_STEP = 15.0 / Game.FPS;
    static final double MAX_SHIP_SPEED  = 1.25 * Game.MAX_ROCK_SPEED;

    private FwdThruster fwdThruster;
    private RevThruster revThruster;


    public Ship() {

        this.shape.addPoint(0, -10);
        this.shape.addPoint(7, 10);
        this.shape.addPoint(-7, 10);

        this.active = true;
        this.angle = 0.0;
        this.deltaAngle = 0.0;
        this.x = 0.0;
        this.y = 0.0;
        this.deltaX = 0.0;
        this.deltaY = 0.0;
        this.render();

        fwdThruster = new FwdThruster(this);
        fwdThruster.render();

        revThruster = new RevThruster(this);
        revThruster.render();

    }

    public AsteroidsSprite getFwdThruster() {
        return fwdThruster;
    }

    public AsteroidsSprite getRevThruster() {
        return revThruster;
    }

    public void update(boolean right, boolean left, boolean up, boolean down) {

        double dx, dy, speed;

        if (left) {
            this.angle += SHIP_ANGLE_STEP;
            if (this.angle > 2 * Math.PI)
                this.angle -= 2 * Math.PI;
        }
        if (right) {
            this.angle -= SHIP_ANGLE_STEP;
            if (this.angle < 0)
                this.angle += 2 * Math.PI;
        }

        // Fire thrusters if up or down cursor key is down.

        dx = SHIP_SPEED_STEP * -Math.sin(this.angle);
        dy = SHIP_SPEED_STEP *  Math.cos(this.angle);
        if (up) {
            this.deltaX += dx;
            this.deltaY += dy;
        }
        if (down) {
            this.deltaX -= dx;
            this.deltaY -= dy;
        }

        // Don't let ship go past the speed limit.

        if (up || down) {
            speed = Math.sqrt(this.deltaX * this.deltaX + this.deltaY * this.deltaY);
            if (speed > MAX_SHIP_SPEED) {
                dx = MAX_SHIP_SPEED * -Math.sin(this.angle);
                dy = MAX_SHIP_SPEED *  Math.cos(this.angle);
                if (up)
                    this.deltaX = dx;
                else
                    this.deltaX = -dx;
                if (up)
                    this.deltaY = dy;
                else
                    this.deltaY = -dy;
            }
        }
    }

    public void updateThrusters() {
        fwdThruster.x = this.x;
        fwdThruster.y = this.y;
        fwdThruster.angle = this.angle;

        revThruster.x = this.x;
        revThruster.y = this.y;
        revThruster.angle = this.angle;

    }

}
