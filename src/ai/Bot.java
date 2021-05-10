package ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import main.Game;
import main.Game.GameState;
import main.Game.Player;

public class Bot {
    /**
     * TODO: speed things up (iterative deepening, alpha-beta, sorting of candidates)
     * TODO: modify the weights, because it seems that it doesn't really want to form fours or broken threes that much :)
     */


    /**
     * Search depth. A hard-coded value. If iterative deepening will be implementet,
     * we can use time-restrictions instead of depth-restrictions.
     */
    private static final int DEPTH = 2; 

    /**
     * A transposition table to store static evaluations of already
     * seen positions in. This way we prevent evaluating the same
     * position (at which one can arrive with different sequences
     * of moves) more than once. 
     */
    private Map<Long, Integer> transpositionTable;

    // MARK: - constructor

    public Bot() {
        this.transpositionTable = new HashMap<Long, Integer>();
    }

    /**
     * A custom class describing evaluated moves
     */
    class evaluatedMove {

        private int move;
        private int eval;

        public evaluatedMove(int move, int eval) {
            this.move = move;
            this.eval = eval;
        }

        public int move() {
            return this.move;
        }

        public int eval() {
            return this.eval;
        }
    } 

    /**
     * Implementation of the basic minimax algorithm.
     * 
     * @param game
     * @param depth
     * @param player
     * @return
     */
    private evaluatedMove minimax(Game game, int depth, Player player) {
        evaluatedMove bestMove = null;
        // Search only moves that are not too far from stones that are already on the board
        Set<Integer> candidates = game.candidates();
        for (int mv : candidates) {
            // Copy the game
            Game gameCopy = new Game(game);
            // Apply move
            gameCopy.play(mv);
            Integer eval;
            // If the node is terminal or maximum depth was reached, return static evaluation
            if (!(game.state() == GameState.IN_PROGRESS) || depth == 0) {
                // If possible, retrieve value from transposition table
                eval = this.transpositionTable.get(gameCopy.hash());
                // If not, calculate static evaluation ...
                if (eval == null) {
                    Evaluator evaluator = new Evaluator(gameCopy);
                    eval = evaluator.evaluate(player);
                    // ... and store it in the table
                    this.transpositionTable.put(gameCopy.hash(), eval);
                }
            }
            else {
                eval = minimax(gameCopy, depth - 1, player).eval();
            }
            if (
                bestMove == null ||
                (game.player() == player && eval > bestMove.eval()) ||
                (game.player() != player && eval < bestMove.eval()) 
            ) bestMove = new evaluatedMove(mv, eval);
        }
        return bestMove;
    }

    /**
     * Chooses a move in a given position.
     * @param game
     * @return
     */
    public int chooseMove(Game game) {
        // Check that the gamestate is not terminal
        if (game.state() != GameState.IN_PROGRESS) {
            throw new IllegalArgumentException("Position is terminal. I cannot choose a move!");
        }
        // If this is the first move, play at the center.
        else if (game.getEmpties().cardinality() == 225) {
            Random rand = new Random();
            int[] center = {
                6 + 6 * 15, 7 + 6 * 15, 8 + 6 * 15,
                6 + 7 * 15, 7 + 7 * 15, 8 + 7 * 15,
                6 + 8 * 15, 7 + 8 * 15, 8 + 8 * 15,                
            };
            return center[rand.nextInt(center.length)];
        }
        // Else, apply the minimax algorithm.
        else {
            return minimax(game, DEPTH, game.player()).move();
        }
    }
    
    /**
     * A simple interface for testing purpouses
     * 
     * @param args
     */
    public static void main(String[] args) {
        Game game = new Game();
        Bot bot = new Bot();
        boolean flag = false;
        System.out.println(game);
        while (true) {
            GameState state = game.state();
            if (state == GameState.WIN_Black) {
                System.out.println("X has won!");
                break;
            }
            else if (state == GameState.WIN_White) {
                System.out.println("O has won!");
                break;
            }
            else if (state == GameState.DRAW) {
                System.out.println("Draw!");
                break;
            }
            else {
                if (flag) {
                    int move = bot.chooseMove(game);
                    game.play(move);
                    System.out.println(game);
                    flag = false;
                    continue;   
                }
                else {
                    Scanner in = new Scanner(System.in);
                    int x = in.nextInt();
                    int y = in.nextInt();
                    game.play(x + 15 * y);
                    System.out.println(game);
                    flag = true;
                    continue;
                }
            }
        }
    }


}