/**
 * Created by Shreyash Patodia and Max Lee (Ho Suk Lee).
 * Student numbers: Shreyash - 767336, Max Lee - 719577
 * Login: Shreyash - spatodia, Max - hol2
 * Subject: COMP30024 Artificial Intelligence.
 * Semester 1, 2017.
 */
package com.teammaxine.agents;

import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;
import com.teammaxine.board.elements.Board;
import com.teammaxine.board.elements.Cell;
import com.teammaxine.board.helpers.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Agent that plays out the board, base for different implementations
 * of our agent. Does the init and update stuff for us, so that we
 * can focus on the move with future implementations. This is one of
 * design choices that allowed us to make a very extensible design.
 */
public abstract class Agent implements SliderPlayer {

    /** Cells controlled by the Agents */
    private HashMap<Vector2, Cell> myCells;
    /** The directions the Agent can move in */
    private ArrayList<Move.Direction> legalDirections;
    /** The row/column dimension for the board */
    private int dimension;
    /** The type of player we control, either 'H' or 'V' */
    private char player;
    /** The character of the other player */
    private char otherPlayer;

    /** The board representation */
    private Board myBoard;
    /** The possible moves for the player */
    private ArrayList<? extends Move> moves;

    /** Legal Directions for the horizontal player */
    private static Move.Direction[] HORIZONTAL_DIRECTIONS = {
            Move.Direction.UP,
            Move.Direction.DOWN,
            Move.Direction.RIGHT
    };
    /** Legal Directions for the vertical player */
    private static Move.Direction[] VERTICAL_DIRECTIONS = {
            Move.Direction.UP,
            Move.Direction.LEFT,
            Move.Direction.RIGHT
    };

    /**
     * Getters and setters.
     */
    public char getPlayer() {
        return player;
    }

    public void setPlayer(char player) {
        this.player = player;
    }

    public Board getMyBoard() {
        return myBoard;
    }


    public ArrayList<? extends Move> getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<Move> moves) {
        this.moves = moves;
    }

    public HashMap<Vector2, Cell> getMyCells() {
        return myCells;
    }

    /**
     * Constructor
     */
    public Agent() {
        this.myBoard = null;
        this.player = Board.CELL_UNKNOWN;
        this.dimension = -1;
        this.myCells = new HashMap<>();
        this.legalDirections = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.otherPlayer = Board.CELL_UNKNOWN;
    }

    /**
     * Chooses which set of directions is accessible to the
     * agent and then calls setAllDirections with the appropriate
     * arguments to set the directions.
     */
    public void setLegalDirections() {

        if(player == Board.CELL_HORIZONTAL) {

            setAllDirections(HORIZONTAL_DIRECTIONS);
        }
        else {

            setAllDirections(VERTICAL_DIRECTIONS);
        }
    }

    /**
     * Sets all the the directions for the agent after getting
     * the appropriate directions from setLegalDirections
     *
     * @param directions Array of accessible directions for the
     *                   agent
     */
    public void setAllDirections(Move.Direction[] directions) {

        for(Move.Direction direction : directions) {

            this.legalDirections.add(direction);
        }
    }

    /**
     * The function that initialises the agent, and makes sure
     * we are implementing SliderPlayer right.
     *
     * @param dimension the row, column length of the board
     * @param board the board as a String
     * @param player the type of player the agent is going to be
     */
    public void init(int dimension, String board, char player) {

        this.player = player;
        if(player == Board.CELL_HORIZONTAL) {
            otherPlayer = Board.CELL_VERTICAL;
        }
        else {
            otherPlayer = Board.CELL_HORIZONTAL;
        }
        this.dimension = dimension;
        /*
           Creates the board and also adds the appropriate cells to the player's
           hashmap of cells.
         */
        this.createBoard(dimension, board);
        this.setLegalDirections();
        this.setMyCells();
        this.moves = myBoard.getLegalMoves(this.player);
        
    }

    /**
     * Creates the board for the player to have a representation of the board
     *
     * @param dimension the row, column length of the board
     * @param board the representation of the board as a string
     */
    public void createBoard(int dimension, String board) {

        ArrayList<String> boardMapping = new ArrayList<>();
        BufferedReader buffer = new BufferedReader(new StringReader(board));
        // Code duplication from PartA driver, won't have PartA driver in the submission.
        for (int row = 0; row < dimension; row++) {
            String line = "";
            try {
                line = buffer.readLine();
            }
            catch(IOException e) {
                System.err.println(e);
            }
            // remove spaces and add to the head of the list
            line = line.replaceAll("\\s", "");
            boardMapping.add(0, line);
        }

        this.myBoard = new Board(boardMapping);
    }

    /**
     * Gets all the cells of the type player for the player to use.
     */
    public void setMyCells() {

        myCells = myBoard.getCellsOfType(this.player);

    }

    /**
     * Uppdate from the other player.
     * @param move A Move object representing the previous move made by the
     *             opponent, which may be null (indicating a pass).
     */
    public void update(Move move) {
        if(move != null) {
            this.myBoard.makeMove(move, otherPlayer);
        }

        this.myCells = this.myBoard.getCellsOfType(player);
        return;
    }

    /**
     * Update from our move function
     * @param move A move object representing the previous move made by the
     *             opponent, which may be null (indicating a pass).
     * @param type The player type to be updated.
     */
    public void update(Move move, char type) {
        if(move != null) {
            this.myBoard.makeMove(move, type);
        }

        this.myCells = this.myBoard.getCellsOfType(player);
        return;
    }

}
