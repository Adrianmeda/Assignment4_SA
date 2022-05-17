public class Ship extends AsteroidsSprite{

    static final double SHIP_ANGLE_STEP = Math.PI / Game.FPS;
    static final double SHIP_SPEED_STEP = 15.0 / Game.FPS;
    static final double MAX_SHIP_SPEED  = 1.25 * Game.MAX_ROCK_SPEED;

    private FwdThruster fwdThruster;
    private RevThruster revThruster;


    public Ship() {

        this.getShape().addPoint(0, -10);
        this.getShape().addPoint(7, 10);
        this.getShape().addPoint(-7, 10);

        this.setActive(true);
        this.setAngle(0.0);
        this.setDeltaAngle(0.0);
        this.setX(0.0);
        this.setY(0.0);
        this.setX(0.0);
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
        double tempAngle = this.getAngle();
        if (left) {
            this.setAngle(tempAngle += SHIP_ANGLE_STEP);
            if (this.getAngle() > 2 * Math.PI)
                tempAngle = this.getAngle();
                this.setAngle(tempAngle -= 2 * Math.PI); ;
        }
        if (right) {
            tempAngle = this.getAngle();
            this.setAngle(tempAngle -= SHIP_ANGLE_STEP);
            if (this.getAngle() < 0)
                tempAngle = this.getAngle();
                this.setAngle(tempAngle += 2 * Math.PI);
        }

        // Fire thrusters if up or down cursor key is down.

        dx = SHIP_SPEED_STEP * -Math.sin(this.getAngle());
        dy = SHIP_SPEED_STEP *  Math.cos(this.getAngle());
        if (up) {
            this.setDeltaX(this.getDeltaX() + dx);
            this.setDeltaY(this.getDeltaY() + dy);
        }
        if (down) {
            this.setDeltaX(this.getDeltaX() - dx);
            this.setDeltaY(this.getDeltaY() - dy);
        }

        // Don't let ship go past the speed limit.

        if (up || down) {
            speed = Math.sqrt(this.getDeltaX() * this.getDeltaX() + this.getDeltaY() * this.getDeltaY());
            if (speed > MAX_SHIP_SPEED) {
                dx = MAX_SHIP_SPEED * -Math.sin(this.getAngle());
                dy = MAX_SHIP_SPEED *  Math.cos(this.getAngle());
                if (up)
                    this.setDeltaX(dx);
                else
                    this.setDeltaX(-dx);
                if (up)
                    this.setDeltaY(dy);
                else
                    this.setDeltaY(-dy);
            }
        }
    }

    public void updateThrusters() {
        fwdThruster.setX(this.getX());
        fwdThruster.setY(this.getY());
        fwdThruster.setAngle(this.getAngle());

        revThruster.setX(this.getX());
        revThruster.setY(this.getY());
        revThruster.setAngle(this.getAngle());

    }

}
