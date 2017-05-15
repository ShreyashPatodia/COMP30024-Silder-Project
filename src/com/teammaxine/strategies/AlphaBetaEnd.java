package com.teammaxine.strategies;

import aiproj.slider.Move;
import com.teammaxine.board.elements.Board;
import com.teammaxine.board.elements.CompressedBoard;
import com.teammaxine.board.helpers.BoardCompressor;
import com.teammaxine.board.helpers.Compresser;
import com.teammaxine.board.scorers.BlockingScorer;
import com.teammaxine.board.scorers.EndGameScorer;

import java.util.*;

/**
 * Created by shreyashpatodia on 12/05/17.
 */
public class AlphaBetaEnd implements Strategy {
    private char myPlayer;
    private char otherPlayer;
    private EndGameScorer scorer;
    private int maxDepth;
    private int sensibleLateralMoves;
    private int badLateralMoves;

    public AlphaBetaEnd(char player, EndGameScorer scorer, int maxDepth) {
        this.myPlayer = player;
        this.otherPlayer = this.myPlayer == Board.CELL_HORIZONTAL ?
                Board.CELL_VERTICAL : Board.CELL_HORIZONTAL;
        this.scorer = scorer;
        this.maxDepth = maxDepth;
        this.sensibleLateralMoves = 0;
        this.badLateralMoves = 0;
    }

    public Move findMove(Board board, int depth) {
        Move bestMove = alphaBetaSearch(depth, board,
                Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove;
    }

    /**
     * gb
     * Performs alpha beta search recursively
     *
     * @param depth
     * @return
     */
    private Move alphaBetaSearch(int depth, Board board, double alpha, double beta) {

        Move bestMove = null;
        double bestVal = Integer.MIN_VALUE;


//       Board bestBoard = null;
        ArrayList<? extends Move> legalMoves = board.getLegalMoves(myPlayer);
        for (Move move : legalMoves) {
            board.makeMove(move, myPlayer);
            if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.LEFT) {
                sensibleLateralMoves++;
            } else if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.RIGHT) {
                badLateralMoves++;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.UP) {
                badLateralMoves++;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.DOWN) {
                sensibleLateralMoves++;
            }
            //bestVal = Math.max(bestVal, val);
            double val = minValue(board, alpha, beta, depth - 1);

            //System.out.println("--------------------");
            //System.out.println("With move :" + move);
            //System.out.println("New board\n" + newBoard);
            // Check if depth - 1 should be here or not
            //System.out.println("Score for this move would be:" + val);
            if (val > bestVal) {
                //System.out.println("Making this the best move");
                bestVal = val;
                //System.out.println("With move :" + move);
                //System.out.println(val);
                bestMove = move;
            }
            //cache.add(compressor.compress(b))
            board.undoMove(move, myPlayer);
            if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.LEFT) {
                sensibleLateralMoves--;
            } else if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.RIGHT) {
                badLateralMoves--;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.UP) {
                badLateralMoves--;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.DOWN) {
                sensibleLateralMoves--;
            }
            alpha = Math.max(alpha, bestVal);
            if (alpha >= beta) {
                return bestMove;
            }
            //System.out.println("--------------------");
        }
        //System.out.println("Nodes visited " + nodes);
        return bestMove;
    }

    private double maxValue(Board board, double alpha, double beta, int depth) {

        if (board.verticalWon()) {
            if (this.myPlayer == 'V')
                return Integer.MAX_VALUE - maxDepth + depth;
            else
                return Integer.MIN_VALUE + maxDepth - depth;
        }
        if (board.horizontalWon()) {
            if (this.myPlayer == 'H')
                return Integer.MAX_VALUE - maxDepth + depth;
            else
                return Integer.MIN_VALUE + maxDepth - depth;
        }
        if (depth == 0) {
            //System.out.println("--------------------");
            //System.out.println("Terminal state :");
            //System.out.println(board);
            double score = this.scorer.scoreBoard(board, myPlayer,
                    sensibleLateralMoves, badLateralMoves);
            //System.out.println("Score is: " + score);
            //System.out.println("--------------------");
            return score;
        }
        ArrayList<? extends Move> legalMoves = board.getLegalMoves(myPlayer);
        double bestVal = Integer.MIN_VALUE;
        // We are out of moves.
        if (legalMoves.size() == 0)
            // Maybe -1 maybe not?
            return minValue(board, alpha, beta, depth);

        for (Move move : legalMoves) {

            board.makeMove(move, myPlayer);
            if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.LEFT) {
                sensibleLateralMoves++;
            } else if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.RIGHT) {
                badLateralMoves++;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.DOWN) {
                sensibleLateralMoves++;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.UP) {
                badLateralMoves++;
            }
            bestVal = Math.max(bestVal, minValue(board, alpha, beta, depth - 1));
            alpha = Math.max(bestVal, alpha);
            board.undoMove(move, myPlayer);
            if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.LEFT) {
                sensibleLateralMoves--;
            } else if (myPlayer == Board.CELL_VERTICAL && move.d == Move.Direction.RIGHT) {
                badLateralMoves--;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.DOWN) {
                sensibleLateralMoves--;
            } else if (myPlayer == Board.CELL_HORIZONTAL && move.d == Move.Direction.UP) {
                badLateralMoves--;
            }

            if (alpha >= beta)
                break;
        }
        return bestVal;
    }

    private double minValue(Board board, double alpha, double beta, int depth) {
        //System.out.println("Min called");
        //System.out.println(board);
        if (board.verticalWon()) {
            if (this.myPlayer == Board.CELL_VERTICAL)
                return Integer.MAX_VALUE - maxDepth + depth;
            else
                return Integer.MIN_VALUE + maxDepth - depth;
        }
        if (board.horizontalWon()) {
            if (this.myPlayer == Board.CELL_HORIZONTAL)
                return Integer.MAX_VALUE - maxDepth + depth;
            else
                return Integer.MIN_VALUE + maxDepth - depth;
        }
        if (depth == 0) {
            //System.out.println("--------------------");
            //System.out.println("Terminal state :");
            //System.out.println(board);
            double score = this.scorer.scoreBoard(board, otherPlayer,
                    sensibleLateralMoves, badLateralMoves);
            //System.out.println("Score is: " + score);
            //System.out.println("--------------------");
            return score;
        }
        ArrayList<? extends Move> legalMoves = board.getLegalMoves(otherPlayer);
        // The other player is out of moves.
        if (legalMoves.size() == 0)
            // Maybe -1 maybe not?
            return maxValue(board, alpha, beta, depth);

        double bestVal = Integer.MAX_VALUE;
        //
        if (legalMoves.size() == 0)
            return Integer.MAX_VALUE;
        //Board newBoard = new Board(board);
        for (Move move : legalMoves) {

            board.makeMove(move, otherPlayer);
            bestVal = Math.min(bestVal, maxValue(board, alpha, beta, depth - 1));
            beta = Math.min(bestVal, beta);
            board.undoMove(move, otherPlayer);
            if (alpha >= beta)
                break;
        }
        return bestVal;
    }
}