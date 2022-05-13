public class Photon extends AsteroidsSprite{

    static final int MAX_SHOTS = 8;

    private int photonIndex = 0;

    public Photon() {
        this.shape.addPoint(1, 1);
        this.shape.addPoint(1, -1);
        this.shape.addPoint(-1, 1);
        this.shape.addPoint(-1, -1);

        this.active = false;
    }

}
