package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import main.Game;
import main.Game.GameState;
import main.Game.Player;

public class Bot {
    /**
     * TODO: speed things up (iterative deepening and candidates' sorting)
     */

    /**
     * A transposition table to store static evaluations of already
     * seen positions in. This way we prevent evaluating the same
     * position (at which one can arrive with different sequences
     * of moves) more than once. 
     */
    private Map<Long, Integer> transpositionTable;

    // Maximum search depth for minimax
    private int DEPTH = 2;

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
     * Minimax with alpha-beta pruning to cut off unreachable branches.
     * 
     * @param game
     * @param candidates The list of candidates for bestMove. To be initialized as game.candidates()
     * @param depth It is a depth-bounded algorithm
     * @param alpha To be initialized as negative infinity
     * @param beta To be initialized as positive infinity
     * @param player Whether it is a minimizing or a maximizing branch. To be initialized as game.toplay()
     * @return
     */
    private evaluatedMove minimaxAB(Game game, int depth, Integer alpha, Integer beta, Player player) {
        // If @player is the maximizer, the starting maxEval is negative "infinity",
        // and if @player is the minimizer, the starting maxEval is "negative infinity". 
        Integer maxEval = (game.player() == player) ? - Integer.MAX_VALUE : Integer.MAX_VALUE; 
        // Search only moves that are not too far from stones that are already on the board
        Set<Integer> candidates = game.candidates();
        int bestMove = candidates.iterator().next(); // Retrieve *any* candidate
        for (int move : candidates) {
            // Copy the game
            Game gameCopy = new Game(game);
            // Apply move
            gameCopy.play(move);
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
                eval = minimaxAB(gameCopy, depth - 1, alpha, beta, player).eval();
            }
            // Maximizer
            if (game.player() == player) {
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                    alpha = Math.max(alpha, maxEval);
                }
            }
            // Minimizer
            else {
                if (eval < maxEval) {
                    maxEval = eval;
                    bestMove = move;
                    beta = Math.min(beta, maxEval);
                }
            }
            // If position is unreachable, terminate loop and return
            if (alpha >= beta)
                return new evaluatedMove(bestMove, maxEval);

        }
        return new evaluatedMove(bestMove, maxEval);
    }

    /**
     * Chooses a move it can find in a given position.
     * 
     * @param game
     * @param depth As alpha-beta is a depth-bounded algorithm,
     * we pass the argument specifying maximum search depth.
     * @return
     */
    public int chooseMoveAB(Game game) {
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
        // Else, apply the minimax algorithm the the game.
        else {
            return minimaxAB(game, DEPTH, - Integer.MAX_VALUE, Integer.MAX_VALUE, game.player()).move();
        }
    }
    
    /**
     * A simple interface for testing purpouses.
     * User input is two separate(!) integers. First is x-coordinate (couted left-right),
     * second is y-coordinate (counted top-down). It does not check whether the move is legal.
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
                    int move = bot.chooseMoveAB(game); // If depth were three, move-selection would take far longer than 5 seconds
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