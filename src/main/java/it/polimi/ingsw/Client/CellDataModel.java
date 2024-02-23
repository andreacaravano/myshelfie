package it.polimi.ingsw.Client;


import javafx.scene.image.ImageView;

/**
 * This class defines board cells data model
 */
public class CellDataModel {
    /**
     * Cell's x coordinate
     */
    private int x;

    /**
     * Cell's y coordinate
     */
    private int y;

    /**
     * Cell's ImageView
     */
    private ImageView cell;

    /**
     * Constructor for CellDataModel class
     *
     * @param x    coordinate
     * @param y    coordinate
     * @param cell ImageView
     */
    public CellDataModel(int x, int y, ImageView cell) {
        this.x = x;
        this.y = y;
        this.cell = cell;
    }

    /**
     * Getter method for x coordinate
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Setter method for x coordinate
     *
     * @param x coordinate to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter method for y coordinate
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Setter method for y coordinate
     *
     * @param y coordinate to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Getter method for cell's ImageView
     *
     * @return cell's ImageView
     */
    public ImageView getCell() {
        return cell;
    }

    /**
     * Setter method for cell's ImageView
     *
     * @param cell ImageView to set
     */
    public void setCell(ImageView cell) {
        this.cell = cell;
    }
}
