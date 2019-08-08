package society;

import actions.Action;
import util.Config;
import util.Updatable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;


/**
 * Defines functionality and parameters of a Cell
 *
 * @author ramilmsh
 */
public class Cell extends Updatable<Cell.Update> {

    /**
     * A comprehensive description an action that can be
     * applied to a cell (by itself or other cell)
     */
    public static class Update {
        Container container;
        double status;
        int priority;
        boolean remove;

        /**
         * Creates an update that moves a cell to a different container,
         * changes status and sets priority
         *
         * @param container: new container
         * @param status:    new status
         * @param priority:  priority, when applying updates
         */
        public Update(Container container, double status, int priority) {
            this.container = container;
            this.status = status;
            this.priority = priority;
        }

        /**
         * Creates an update that moves a cell to a different container and sets priority
         *
         * @param container: new container
         * @param priority:  priority, when applying updates
         */
        public Update(Container container, int priority) {
            this(container, -1, priority);
        }


        /**
         * Creates an update that moves a cell to a different container and changes status
         *
         * @param container: new container
         * @param status:    new status
         */
        public Update(Container container, double status) {
            this(container, status, -1);
        }


        /**
         * Creates an update that moves a cell to a different container
         *
         * @param container: new container
         */
        public Update(Container container) {
            this(container, -1, -1);
        }


        /**
         * Creates an update that changes status
         *
         * @param status: new status
         */
        public Update(double status) {
            this(null, status, -1);
        }

        /**
         * Creates and update that removes the cell
         *
         * @param remove: remove?
         */
        public Update(boolean remove) {
            this.remove = remove;
        }
    }

    private Container container = null;
    private PriorityQueue<Update> updates;

    private Config.CellType type;
    private Action action;
    private double status;

    public Cell(Config.CellType type) {
        setType(type);
        action = type.getAction();

        updates = new PriorityQueue<>();
    }

    void addTo(Container container) {
        this.container = container;
    }

    int getPriority() {
        return type.getPriority();
    }

    void execute(Grid grid) {
        this.action.execute(this, grid);
    }

    @Override
    protected void apply(Cell.Update update) {
        if (update.remove)
            container.remove();
        setStatus(update.status);

    }

    @Override
    public void add(Update update) {
        updates.add(update);
        if (update.container != null) {
            container.clearReserved();
            update.container.reserve(this);
        }
    }

    /**
     * Get type
     *
     * @return type
     */
    public Config.CellType getType() {
        return type;
    }

    /**
     * Set type
     *
     * @param type: type
     */
    public void setType(Config.CellType type) {
        this.type = type;
    }

    /**
     * Gets all existing cell neighbours
     *
     * @return ArrayList of existing neighbours
     */
    public ArrayList<Cell> getNeighbours() {
        Iterator<Container> neighbours = container.getAllNeighbours();
        ArrayList<Cell> cells = new ArrayList<>();
        while (neighbours.hasNext()) {
            Container neighbour = neighbours.next();
            if (!neighbour.vacant())
                cells.add(neighbour.getCell());
        }
        return cells;
    }

    /**
     * Get all cell neighbours, whether they exist or don't
     * if they do not, place will be filled by null
     *
     * @return ArrayList of all the neighbours
     */
    public ArrayList<Cell> getAllNeihgbours() {
        Iterator<Container> neighbours = container.getAllNeighbours();
        ArrayList<Cell> cells = new ArrayList<>();
        while (neighbours.hasNext()) {
            Container neighbour = neighbours.next();
            cells.add(neighbour == null ? null : neighbour.getCell());
        }
        return cells;
    }

    /**
     * Get the cell's container
     *
     * @return container
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Check if a cell belongs to a type, associated with a character
     *
     * @param c: character
     * @return same type?
     */
    public boolean sameType(Character c) {
        return type.equals(c);
    }

    /**
     * Checks if two cells are of the same type
     *
     * @param cell: other cell
     * @return same type?
     */
    public boolean sameType(Cell cell) {
        return cell.type.equals(type);
    }

    /**
     * Get status
     *
     * @return status
     */
    public double getStatus() {
        return status;
    }

    /**
     * Set status
     *
     * @param status: status
     */
    public void setStatus(double status) {
        this.status = status == -1 ? this.status : status;
    }
}
