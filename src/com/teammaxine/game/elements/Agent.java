/**
 * Created by Shreyash Patodia and Max Lee (Ho Suk Lee).
 * Student numbers: Shreyash - 767336, Max Lee - 719577
 * Login: Shreyash - spatodia, Max - hlee39
 * Subject: COMP30024 Artificial Intelligence.
 * Semester 1, 2017.
 */
package com.teammaxine.game.elements;

import com.teammaxine.game.actions.ActionFinisher;
import com.teammaxine.game.actions.ActionMove;
import com.teammaxine.game.actions.AgentAction;
import com.teammaxine.game.helpers.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AI agent in the game
 */
public abstract class Agent {
    private HashMap<Vector2, Cell> myCells;
    private ArrayList<String> legalDirections;
    int size;

    /**
     * Agent that plays out the game
     *
     * @param size size of the board the agent is in
     */
    public Agent(int size) {
        // cells that agent controls
        this.size = size;
        this.myCells = new HashMap<>(size);
        this.legalDirections = new ArrayList<>();
        this.setAllDirections();
    }

    /**
     * agents should be initialized with this function
     */
    public abstract void setAllDirections();

    /**
     * Check if the cell is in the edge where you can get out of the board
     *
     * @param cell to consider
     * @return true if the cell is on the end edge
     */
    public abstract boolean edgeCheck(Cell cell);

    /**
     * directions it is allowed to move
     *
     * @param direction the direction to be added to the legal directions.
     */
    public void addLegalDirection(String direction) {
        this.legalDirections.add(direction);
    }

    /**
     * add a cell that the agent controls
     *
     * @param newCell the new cell belonging to the agent i.e. a cell that
     * contains the agent's piece.
     */
    public void addCell(Cell newCell) {

        this.myCells.put(newCell.getPos(), newCell);

    }

    /**
     * remove a cell that agent controls
     *
     * @param oldCell the cell that is not longer belonging to the agent.
     */
    public void removeCell(Cell oldCell) {
        this.myCells.remove(oldCell);
    }

    /**
     * check if agent controls the cell
     *
     * @param cell the cell who's possession is to be checked for the agent
     * @return true if the cell is being controled by agent
     */
    public boolean hasCell(Cell cell) {
        return this.myCells.values().contains(cell);
    }

    /**
     * get all the legal moves possible
     *
     * @return array list of agent actions
     */
    public ArrayList<AgentAction> getLegalMoves() {
        ArrayList<AgentAction> moves = new ArrayList<>();

        for (Cell myCell : this.myCells.values()) {
            if(edgeCheck(myCell)) {
                moves.add(new ActionFinisher(myCell.getPos(), this));
            }

            for (String legalDirection : this.legalDirections) {
                char value;
                Vector2 neighbourPos = null;

                if (myCell.getNeighbours().containsKey(legalDirection)) {
                    value = myCell.getNeighbour(legalDirection).getValue();
                    neighbourPos = myCell.getNeighbour(legalDirection).getPos();
                } else {
                    value = Board.CELL_UNKNOWN;
                }

                if (value == Board.CELL_EMPTY) {
                    moves.add(new ActionMove(myCell.getPos(), neighbourPos));
                }
            }
        }

        return moves;
    }
}
