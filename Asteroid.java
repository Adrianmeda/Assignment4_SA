import java.awt.*;

public class Asteroid extends AsteroidsSprite{

    public Asteroid(double asteroidsSpeed) {
        this.setShape(new Polygon());

        int s = Game.MIN_ROCK_SIDES + (int) (Math.random() * (Game.MAX_ROCK_SIDES - Game.MIN_ROCK_SIDES));

        for (int j = 0; j < s; j ++) {
            double theta = 2 * Math.PI / s * j;
            double r = Game.MIN_ROCK_SIZE + (int) (Math.random() * (Game.MAX_ROCK_SIZE - Game.MIN_ROCK_SIZE));
            int x = (int) -Math.round(r * Math.sin(theta));
            int y = (int)  Math.round(r * Math.cos(theta));
            this.getShape().addPoint(x, y);
        }

        this.setActive(true);
        this.setAngle(0.0);
        this.setDeltaAngle(Math.random() * 2 * Game.MAX_ROCK_SPIN - Game.MAX_ROCK_SPIN);

        if (Math.random() < 0.5) {
            this.setX(-AsteroidsSprite.width / 2);
            if (Math.random() < 0.5)
                this.setX(AsteroidsSprite.width / 2);
            this.setY(Math.random() * AsteroidsSprite.height);
        }
        else {
            this.setX(Math.random() * AsteroidsSprite.width);
            this.setY(-AsteroidsSprite.height / 2);
            if (Math.random() < 0.5)
                this.setY(AsteroidsSprite.height / 2);
        }

        this.setDeltaX(Math.random() * asteroidsSpeed);
        if (Math.random() < 0.5)
            this.setDeltaX(-this.getDeltaX());
        this.setDeltaY( Math.random() * asteroidsSpeed);
        if (Math.random() < 0.5)
            this.setDeltaY(-this.getDeltaY());

    }

    public void generateSmallAsteroid(double tempX, double tempY, double asteroidsSpeed) {
        this.setShape(new Polygon());
        int s = Game.MIN_ROCK_SIDES + (int) (Math.random() * (Game.MAX_ROCK_SIDES - Game.MIN_ROCK_SIDES));
        for (int j = 0; j < s; j ++) {
            double theta = 2 * Math.PI / s * j;
            double r = (Game.MIN_ROCK_SIZE + (int) (Math.random() * (Game.MAX_ROCK_SIZE - Game.MIN_ROCK_SIZE))) / 2;
            int x = (int) -Math.round(r * Math.sin(theta));
            int y = (int)  Math.round(r * Math.cos(theta));
            this.getShape().addPoint(x, y);
        }

        this.setActive(true);
        this.setAngle(0.0);
        this.setDeltaAngle(Math.random() * 2 * Game.MAX_ROCK_SPIN - Game.MAX_ROCK_SPIN);
        this.setX(tempX);
        this.setY(tempY);
        this.setDeltaX(Math.random() * 2 * asteroidsSpeed - asteroidsSpeed);
        this.setDeltaY(Math.random() * 2 * asteroidsSpeed - asteroidsSpeed);


    }

}
