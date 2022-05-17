public class FwdThruster extends AsteroidsSprite{

    public FwdThruster(Ship ship) {
        this.getShape().addPoint(0, 12);
        this.getShape().addPoint(-3, 16);
        this.getShape().addPoint(0, 26);
        this.getShape().addPoint(3, 16);

        this.setX(ship.getX());
        this.setY(ship.getY());
        this.setAngle(ship.getAngle());
    }
}
