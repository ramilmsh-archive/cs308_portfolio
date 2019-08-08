package objects;

import javafx.scene.Group;
import utils.Triplet;
import utils.Tuple;

import java.util.function.Consumer;


/**
 * Paddle that can trigger actions when hit by a ball
 */
public class ActionPaddle extends Paddle {
    private Consumer<Triplet<Block, Paddle, Ball>> action;
    private Consumer<Triplet<Block, Paddle, Ball>> defaultAction;

    public ActionPaddle(double x, double y, Consumer<Triplet<Block, Paddle, Ball>> action) {
        this(x, y, WIDTH, action);
    }

    public ActionPaddle(double x, double y, double w, Consumer<Triplet<Block, Paddle, Ball>> action) {
        super(x, y, w);
        this.action = action;
        this.defaultAction = action;
    }

    /**
     * Define paddle's action, when a ball hits it
     *
     * @param t: action, permanent tuple
     */
    public void setAction(Tuple<Consumer<Triplet<Block, Paddle, Ball>>, Boolean> t) {
        System.out.println(t.b ? true : false);
        defaultAction = t.b ? t.a : defaultAction;
        this.action = t.a;
    }

    @Override
    public void hit(Ball ball) {
        action.accept(new Triplet<>(null, this, ball));
        action = defaultAction;
    }

    @Override
    public ActionPaddle addTo(Group root) {
        root.getChildren().add(this);
        return this;
    }
}
