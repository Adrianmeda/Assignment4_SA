import java.awt.*;

/******************************************************************************
 The AsteroidsSprite class defines a game object, including it's shape,
 position, movement and rotation. It also can detemine if two objects collide.
 ******************************************************************************/

abstract class AsteroidsSprite {

    // Fields:

    public static int width;          // Dimensions of the graphics area.
    public static int height;

    private Polygon shape;             // Base sprite shape, centered at the origin (0,0).
    private boolean active;            // Active flag.
    private double angle;             // Current angle of rotation.
    private double deltaAngle;        // Amount to change the rotation angle.
    private double x, y;              // Current position on screen.
    private double deltaX, deltaY;    // Amount to change the screen position.
    private Polygon sprite;            // Final location and shape of sprite after
    // applying rotation and translation to get screen
    // position. Used for drawing on the screen and in
    // detecting collisions.

    // Constructors:

    public AsteroidsSprite() {

        this.shape = new Polygon();
        this.active = false;
        this.angle = 0.0;
        this.deltaAngle = 0.0;
        this.x = 0.0;
        this.y = 0.0;
        this.deltaX = 0.0;
        this.deltaY = 0.0;
        this.sprite = new Polygon();
    }


    // Methods:


    public Polygon getShape() {
        return shape;
    }

    public boolean isColliding(AsteroidsSprite s) {

        int i;

        // Determine if one sprite overlaps with another, i.e., if any vertice
        // of one sprite lands inside the other.

        for (i = 0; i < s.sprite.npoints; i++)
            if (this.sprite.contains(s.sprite.xpoints[i], s.sprite.ypoints[i]))
                return true;
        for (i = 0; i < this.sprite.npoints; i++)
            if (s.sprite.contains(this.sprite.xpoints[i], this.sprite.ypoints[i]))
                return true;
        return false;
    }

    public boolean isActive() {
        return this.active;
    }

    public double getAngle() {
        return angle;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Polygon getSprite() {
        return sprite;
    }


    public void setDeltaAngle(double deltaAngle) {
        this.deltaAngle = deltaAngle;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    public boolean advance() {

        boolean wrapped;

        // Update the rotation and position of the sprite based on the delta
        // values. If the sprite moves off the edge of the screen, it is wrapped
        // around to the other side and TRUE is returnd.

        this.angle += this.deltaAngle;
        if (this.angle < 0)
            this.angle += 2 * Math.PI;
        if (this.angle > 2 * Math.PI)
            this.angle -= 2 * Math.PI;
        wrapped = false;
        this.x += this.deltaX;
        if (this.x < -width / 2) {
            this.x += width;
            wrapped = true;
        }
        if (this.x > width / 2) {
            this.x -= width;
            wrapped = true;
        }
        this.y -= this.deltaY;
        if (this.y < -height / 2) {
            this.y += height;
            wrapped = true;
        }
        if (this.y > height / 2) {
            this.y -= height;
            wrapped = true;
        }

        return wrapped;
    }

    public void render() {

        int i;

        // Render the sprite's shape and location by rotating it's base shape and
        // moving it to it's proper screen position.

        this.sprite = new Polygon();
        for (i = 0; i < this.shape.npoints; i++)
            this.sprite.addPoint((int) Math.round(this.shape.xpoints[i] * Math.cos(this.angle) + this.shape.ypoints[i] * Math.sin(this.angle)) + (int) Math.round(this.x) + width / 2,
                    (int) Math.round(this.shape.ypoints[i] * Math.cos(this.angle) - this.shape.xpoints[i] * Math.sin(this.angle)) + (int) Math.round(this.y) + height / 2);
    }

    public void activate() {
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }

    public void explode(Explosion[] explosions, int[] explosionCounter, boolean detail) {
        int c, i, j;
        int cx, cy;

        c = 2;
        if (detail || this.sprite.npoints < 6)
            c = 1;
        for (i = 0; i < this.sprite.npoints; i += c) {
            Game.explosionIndex++;
            if (Game.explosionIndex >= Game.MAX_SCRAP)
                Game.explosionIndex = 0;
            explosions[Game.explosionIndex].activate();
            explosions[Game.explosionIndex].setShape(new Polygon());
            j = i + 1;
            if (j >= this.sprite.npoints)
                j -= this.sprite.npoints;
            cx = (int) ((this.shape.xpoints[i] + this.shape.xpoints[j]) / 2);
            cy = (int) ((this.shape.ypoints[i] + this.shape.ypoints[j]) / 2);
            explosions[Game.explosionIndex].getShape().addPoint(
                    this.shape.xpoints[i] - cx,
                    this.shape.ypoints[i] - cy);
            explosions[Game.explosionIndex].getShape().addPoint(
                    this.shape.xpoints[j] - cx,
                    this.shape.ypoints[j] - cy);
            explosions[Game.explosionIndex].setX(this.getX() + cx);
            explosions[Game.explosionIndex].setY(this.getY() + cy);
            explosions[Game.explosionIndex].setAngle(this.getAngle());
            explosions[Game.explosionIndex].setDeltaAngle( 4 * (Math.random() * 2 * Game.MAX_ROCK_SPIN - Game.MAX_ROCK_SPIN));
            explosions[Game.explosionIndex].setDeltaX((Math.random() * 2 * Game.MAX_ROCK_SPEED - Game.MAX_ROCK_SPEED + this.deltaX) / 2);
            explosions[Game.explosionIndex].setDeltaY((Math.random() * 2 * Game.MAX_ROCK_SPEED - Game.MAX_ROCK_SPEED + this.deltaY) / 2);
            explosionCounter[Game.explosionIndex] = Game.SCRAP_COUNT;
        }

    }

}
