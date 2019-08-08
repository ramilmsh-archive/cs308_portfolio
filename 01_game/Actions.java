import javafx.scene.paint.Paint;
import math.num.Vector;
import objects.Ball;
import objects.Block;
import objects.Paddle;
import utils.Triplet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


/**
 * Actions manager class. Implements an interface for the elements to interact with
 * the game
 */
public class Actions {
    private ArrayList<Block> blocks;
    private ArrayList<Ball> balls;
    private Bounce bounce;

    private static final double BALL_SPEED_SCALE = 2.;
    private static final long BALL_SPEED_SCALE_DURATION = 2;

    private static final int BALL_SPAWN_NUMBER = 2;

    private static final int BALL_INVINCIBILITY_DURATION = 5;

    private static double DEFAULT_PADDLE_SCALE = 2;

    Actions(Bounce bounce) {
        this.bounce = bounce;
        setElementLists(new ArrayList<>(), new ArrayList<>());
    }

    void setElementLists(ArrayList<Block> blocks, ArrayList<Ball> balls) {
        this.blocks = blocks;
        this.balls = balls;
    }

    /**
     * Multiplies the balls' speed by a factor for a limited time
     *
     * @param t:        event arguments
     * @param scale:    scaling factor
     * @param duration: duration in seconds
     */
    void scaleBallsSpeed(Triplet<Block, Paddle, Ball> t, double scale, long duration) {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) iterator.next().scaleSpeed(scale, duration);
        if (t.c != null)
            defaultPaddleAction(t);
    }

    void scaleBallsSpeed(Triplet<Block, Paddle, Ball> t) {
        scaleBallsSpeed(t, BALL_SPEED_SCALE, BALL_SPEED_SCALE_DURATION);
    }

    /**
     * Spawns n balls
     *
     * @param t: event arguments
     * @param n: number of balls to spawn
     */
    void spawnBalls(Triplet<Block, Paddle, Ball> t, int n) {
        Random r = new Random();
        for (int i = 0; i < n; ++i) {
            Ball ball = new Ball(bounce.HEIGHT, bounce.WIDTH, 4 + 2 * r.nextDouble());
            ball.setFill(Paint.valueOf("red"));
            bounce.add(ball);
        }
    }

    void spawnBalls(Triplet<Block, Paddle, Ball> t) {
        spawnBalls(t, BALL_SPAWN_NUMBER);
    }

    /**
     * Make all balls invincible for a limited time
     *
     * @param t: time in seconds
     */
    void makeInvincible(Triplet<Block, Paddle, Ball> t) {
        Iterator<Ball> iteratorForward = balls.iterator();
        while (iteratorForward.hasNext()) iteratorForward.next().setInvincible(true);
        new Thread(() -> {
            try {
                Thread.sleep(BALL_INVINCIBILITY_DURATION * 1000);
            } catch (InterruptedException ignored) {
            }
            Iterator<Ball> iteratorBackward = balls.iterator();
            while (iteratorBackward.hasNext()) iteratorBackward.next().setInvincible(false);
        }).start();
    }

    /**
     * Scale paddle by a factor
     *
     * @param t: event arguments
     * @param s: scaling factor
     */
    void scalePaddle(Triplet<Block, Paddle, Ball> t, double s) {
        Paddle paddle = t.b;
        paddle.setWidth(paddle.getWidth() * s);
    }

    void scalePaddle(Triplet<Block, Paddle, Ball> t) {
        scalePaddle(t, DEFAULT_PADDLE_SCALE);
        if (t.c != null)
            defaultPaddleAction(t);
    }

    /**
     * Reverse paddle reflection
     *
     * @param t: event arguments
     */
    void reversePaddle(Triplet<Block, Paddle, Ball> t) {
        Paddle paddle = t.b;
        Ball ball = t.c;
        Vector vel = getProportionalReflection(paddle, ball);
        ball.setVel(new Vector(-vel.x, vel.y));
    }

    /**
     * Define default paddle action
     *
     * @param t: event arguments
     */
    void defaultPaddleAction(Triplet<Block, Paddle, Ball> t) {
        Paddle paddle = t.b;
        Ball ball = t.c;
        ball.setVel(getProportionalReflection(paddle, ball));
    }

    /**
     * Calculates velocity vector based on part of the paddle the ball touched
     *
     * @param paddle: paddle
     * @param ball:   ball
     * @return velocity vector
     */
    private static Vector getProportionalReflection(Paddle paddle, Ball ball) {
        double portion = (ball.getCenterX() - paddle.getX()) / paddle.getWidth();
        double angle = Math.PI * (.1 + .8 * (1 - portion));
        return new Vector(Math.cos(angle), -Math.sin(angle)).mult(ball.magnitude);
    }
}
