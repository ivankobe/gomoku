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
     * Since evaluation of non-terminal and terminal positions should be independent
     * of each other, the latter are stored seperately.
     */
    private static final Map<String, Integer> scores;

    /**
     * The scores for win and lose are symmetric. The score for draw is 0.
     */
    static {
        scores = new HashMap<String, Integer>();
        scores.put("win", 100000);
        scores.put("lose", - 100000);
        scores.put("draw", 0); 
    }


    /**
     * A transposition table to store static evaluations of already
     * seen positions in. This way we prevent evaluating the same
     * position (at which one can arrive with different sequences
     * of moves) more than once. We need two transposition tables,
     * once for when computer is playing black and one for when it is
     * playing white. In a single game, a single table would suffice,
     * since scores are always calculated for the same player, however,
     * the same Bot object is meant to be used in several games (in
     * order to accumulate as big transposition tables as possible).
     */
    private Map<Long, Integer> transpositionTableBlack;
    private Map<Long, Integer> transpositionTableWhite;


    /**
     * Retrieve the appropriate transposition table
     * 
     * @param player
     * @return
     */
    private Map<Long, Integer> getTranspositionTable(Player player) {
        return (player == Player.Black) ? this.transpositionTableBlack : this.transpositionTableWhite;
    }

    /**
     * Since minimax is a depth-bounded algorithm, maximum search depth has to be specifyied
     */
    private int DEPTH = 2;

    // MARK: - constructor

    public Bot() {
        this.transpositionTableBlack = new HashMap<Long, Integer>();
        this.transpositionTableWhite = new HashMap<Long, Integer>();
    }

    /**
     * A custom class describing evaluated moves, basically a pair (move, evaluation)
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
     * Minimax with alpha-beta pruning to cut off unreachable branches. At each
     * step, candidates for next move are first evaluated and sorted. This should
     * speed up the algorithm significantly.
     * 
     * @param game
     * @param depth It is a depth-bounded algorithm
     * @param alpha To be initialized as negative infinity
     * @param beta To be initialized as positive infinity
     * @param player Whether it is a minimizing or a maximizing branch. To be initialized as game.toplay()
     * @return
     */
    private evaluatedMove minimaxAB(Game game, int depth, Integer alpha, Integer beta, Player player) {
        // Retrieve the appropriate transposition table
        Map<Long, Integer> transpositionTable = this.getTranspositionTable(player);
        // If @player is the maximizer, the starting maxEval is negative "infinity",
        // and if @player is the minimizer, the starting maxEval is "negative infinity". 
        Integer maxEval = (game.player() == player) ? - Integer.MAX_VALUE : Integer.MAX_VALUE;
        // Search only moves that are not too far from stones that are already on the board 
        Set<Integer> candidates = game.candidates();
        // A map for storing the evaluations of child nodes
        Map<Integer, Integer> evaluations = new HashMap<Integer, Integer>();
        // Since, for evaluation of a child node, the game must be copied,
        // the copied games are stored in a map so as to avoid having to
        // copy each game twice
        Map<Integer, Game> clonedGames = new HashMap<Integer, Game>();
        // Evaluate each candidate
        for (int move : candidates) {
            // Copy the game
            Game gameCopy = new Game(game);
            // Play the candidate
            gameCopy.play(move);
            // If this board was already evaluated, retrieve evaluation from table
            Integer staticEvaluation = transpositionTable.get(gameCopy.hash());
            // If not, calculate static evaluation and store it in the table for future reference 
            if (staticEvaluation == null) {
                GameState state = gameCopy.state();
                if ((state == GameState.WIN_Black && player == Player.Black) ||
                    (state == GameState.WIN_White && player == Player.White)) {
                    staticEvaluation = scores.get("win");
                }
                else if ((state == GameState.WIN_Black && player == Player.White) ||
                    (state == GameState.WIN_White && player == Player.Black)) {
                    staticEvaluation = - scores.get("win");
                }
                else if (state == GameState.DRAW) {
                    staticEvaluation = scores.get("draw");
                }
                else {
                    Evaluator evaluator = new Evaluator(gameCopy);
                    staticEvaluation = evaluator.evaluate(player);
                }
                transpositionTable.put(gameCopy.hash(), staticEvaluation);
            }
            // Store the cloned game
            clonedGames.put(move, gameCopy);
            // Store the evaluation of child node
            evaluations.put(move, staticEvaluation);
        }
        // Transform the set of candidates into a list (for sorting)
        List<Integer> sorted = new ArrayList<Integer>(candidates);
        // Sort the list
        sorted.sort((x,y) -> evaluations.get(y).compareTo(evaluations.get(x))); // Yay, lambda expressions!
        int bestMove = sorted.get(0);  // Start with the best candidate   
        for (int move : sorted) {
            // Retrieve the cloned game
            Game clone = clonedGames.get(move);
            Integer eval;
            // If position is terminal or maximum depth was reached, retrieve evaluation
            // from table. We can be sure that eval != null.
            if (!(clone.state() == GameState.IN_PROGRESS) || depth == 0) {
                eval = transpositionTable.get(clone.hash());
            }
            // Else, make a recursive call
            else {
                eval = minimaxAB(clone, depth - 1, alpha, beta, player).eval();
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