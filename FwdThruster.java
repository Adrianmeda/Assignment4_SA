public class FwdThruster extends AsteroidsSprite{

    public FwdThruster(Ship ship) {
        this.shape.addPoint(0, 12);
        this.shape.addPoint(-3, 16);
        this.shape.addPoint(0, 26);
        this.shape.addPoint(3, 16);

        this.x = ship.x;
        this.y = ship.y;
        this.angle = ship.angle;
    }
}
