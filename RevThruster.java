public class RevThruster extends AsteroidsSprite{

    public RevThruster(Ship ship) {
        this.shape.addPoint(-2, 12);
        this.shape.addPoint(-4, 14);
        this.shape.addPoint(-2, 20);
        this.shape.addPoint(0, 14);
        this.shape.addPoint(2, 12);
        this.shape.addPoint(4, 14);
        this.shape.addPoint(2, 20);
        this.shape.addPoint(0, 14);

        this.x = ship.x;
        this.y = ship.y;
        this.angle = ship.angle;
    }
}
