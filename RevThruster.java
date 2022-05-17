public class RevThruster extends AsteroidsSprite{

    public RevThruster(Ship ship) {
        this.getShape().addPoint(-2, 12);
        this.getShape().addPoint(-4, 14);
        this.getShape().addPoint(-2, 20);
        this.getShape().addPoint(0, 14);
        this.getShape().addPoint(2, 12);
        this.getShape().addPoint(4, 14);
        this.getShape().addPoint(2, 20);
        this.getShape().addPoint(0, 14);

        this.setX(ship.getX());
        this.setY(ship.getY());
        this.setAngle(ship.getAngle());
    }
}
