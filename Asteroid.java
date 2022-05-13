import java.awt.*;

public class Asteroid extends AsteroidsSprite{

    public Asteroid(double asteroidsSpeed) {
        this.shape = new Polygon();

        int s = Asteroids.MIN_ROCK_SIDES + (int) (Math.random() * (Asteroids.MAX_ROCK_SIDES - Asteroids.MIN_ROCK_SIDES));

        for (int j = 0; j < s; j ++) {
            double theta = 2 * Math.PI / s * j;
            double r = Asteroids.MIN_ROCK_SIZE + (int) (Math.random() * (Asteroids.MAX_ROCK_SIZE - Asteroids.MIN_ROCK_SIZE));
            int x = (int) -Math.round(r * Math.sin(theta));
            int y = (int)  Math.round(r * Math.cos(theta));
            this.shape.addPoint(x, y);
        }

        this.active = true;
        this.angle = 0.0;
        this.deltaAngle = Math.random() * 2 * Asteroids.MAX_ROCK_SPIN - Asteroids.MAX_ROCK_SPIN;

        if (Math.random() < 0.5) {
            this.x = -AsteroidsSprite.width / 2;
            if (Math.random() < 0.5)
                this.x = AsteroidsSprite.width / 2;
            this.y = Math.random() * AsteroidsSprite.height;
        }
        else {
            this.x = Math.random() * AsteroidsSprite.width;
            this.y = -AsteroidsSprite.height / 2;
            if (Math.random() < 0.5)
                this.y = AsteroidsSprite.height / 2;
        }

        this.deltaX = Math.random() * asteroidsSpeed;
        if (Math.random() < 0.5)
            this.deltaX = -this.deltaX;
        this.deltaY = Math.random() * asteroidsSpeed;
        if (Math.random() < 0.5)
            this.deltaY = -this.deltaY;

    }

}
