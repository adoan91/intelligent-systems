
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Stack;

public class Malacath {
    
    private int [][] board;
    private int maxDepth;
    private int [][] humanPieces;
    private int [][] humanMoves;
    
    private int [][] malacathPieces;
    private int [][] malacathMoves;
    private long startTimer;
    private long timerMax;
    
    private int victory;
    private boolean gameEnd;
    private Stack <int []> movesTryStack;
    
    /*
    0 = -
    1 = r
    2 = b
    3 = k
    4 = n
    5 = p
    6 = R
    7 = B
    8 = K
    9 = N
    10 = P
    */
    
    public Malacath() {
    	setup();
    	gameEnd = false;
    	victory = 0;
    	timerMax = TimeUnit.SECONDS.toNanos(5);
    	maxDepth = 4; // 5 plies
    	movesTryStack = new Stack <int []> ();
    	
    }
        
    private void setup() {
    	
    	board = new int [][] {
            {0, 0, 0, 0, 3, 0},
            {4, 2, 1, 1, 2, 4},
            {0, 0, 5, 5, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 10, 10, 0, 0},
            {9, 7, 6, 6, 7, 9},
            {0, 8, 0, 0, 0, 0}
    		};

    }
        
    public void askToQuit() {
    	while (true) {
        	Scanner in = new Scanner(System.in);
            System.out.print("Do you wish to quit (yes/no) ? ");
            if ((in.next()).equals("yes")) {
            	System.exit(0);
            }
        }
    }

    public void Start() {
    	
    	String first = getGoesFirst();
    	
        printBoard();

        if (first.equals("yes")) {
        	String move = getMove();
        	
            while (checkLegalMove(move) == false) {
            	move = getMove();
            }

            makeMove(move, true);

            printBoard();

            gameEnd = checkGameOver(false);        	
        }
        
        while (gameEnd == false) {
            makeMalacathMove();

            if (gameEnd == false) {
            	String move = getMove();
            	
                while (checkLegalMove(move) == false) {
                	move = getMove();
                }

                makeMove(move, true);

                printBoard();

                gameEnd = checkGameOver(false);
            }
        }

        check4Winner();
        
        askToQuit();
        
    }
    
    private String getGoesFirst() {
    	Scanner in = new Scanner(System.in);
        System.out.print("Human, do you wish to go first (yes/no) ? ");
        return in.next();
	}


	private String getMove() {
		Scanner in = new Scanner(System.in);
        System.out.print("Enter your move in the notation (B2E5): ");
        
        
        
        return in.next();
		
	}

    private void check4Winner() {
        switch (victory) {
            case 0:
            	System.out.println("Draw!");
                break;
            case 1:
            	System.out.println("Human wins!");
                break;
            case 2:
            	System.out.println("Malacath wins!");
                break;
            default:
            	System.out.println("Error");
                break;
        }
	}

	private void makeMalacathMove() {
		
		int [] mv = getMalacathMove();
		
		char [] combine = new char [] {
                convertAxis(mv[0]),
                Character.forDigit(mv[1] + 1, 10),
                convertAxis(mv[2]),
                Character.forDigit(mv[3] + 1, 10)
                };
		
		String move = String.valueOf(combine);
		
        String convertedMove = getConvertedMalacathMove(mv);
        makeMove(move, false);

        System.out.println("COMPUTER MOVES " + move + " (" + convertedMove + ")\n");

        printBoard();

        gameEnd = checkGameOver(true);
    }

    private String getConvertedMalacathMove(int [] mv) {
    	char [] combine = new char [] {
                convertAxis(boardTranslation(mv[0])),
                Character.forDigit(translate(mv[1]) + 1, 10),
                convertAxis(boardTranslation(mv[2])),
                Character.forDigit(translate(mv[3]) + 1, 10)
                };
    			
		return String.valueOf(combine);
		
	}


	private int [] getMalacathMove() {
    	
        startTimer = System.nanoTime();
        int i = 0;
        LegalMoves mMove = null;
        while ((System.nanoTime() - startTimer) < timerMax) {
            LegalMoves m = miniMax(maxDepth + i);
            if (m.score != -77777) {
                if (mMove == null || (m.score > mMove.score)) {
                    mMove = m;
                }
            }
            i++;
        }
        
        System.out.println("Plies: " + (maxDepth + i));
        
        return mMove.mv;
		
	}
    
    public int translate(int move) {
		switch (move) {
			case 0:
				return 7;
			case 1:
				return 6;
			case 2:
				return 5;
			case 3:
				return 4;
			case 4:
				return 3;
			case 5:
				return 2;
			case 6:
				return 1;
			case 7:
				return 0;
			default:
				return -1;
		}
    }

    private LegalMoves miniMax(int maxDepth) {
        LegalMoves best = new LegalMoves();
        best.score = -9999; 
        best.mv = null;
        int bestScore = best.score;
        int moveScore;
        genMalacathMoves();
        int[][] moves = malacathMoves;
        
        int i = 0;
        while (moves[i][0] != 0 || 
        		moves[i][1] != 0 || 
        		moves[i][2] != 0 || 
        		moves[i][3] != 0
        		) {
            if (System.nanoTime() - startTimer > timerMax) {

            	best.score = bestScore;
            	best.mv = moves[i];
                return best;
            }
            tryMoveOnBoard(moves[i], false);
            moveScore = min(0, maxDepth, 9999, bestScore);
            if (moveScore != -77777) {
                if (moveScore > bestScore) {
                    bestScore = moveScore;
                    best.mv = moves[i];
                }
                
            }
            retractMove();
            i++;
        }

        best.score = bestScore;
        return best;
    }

    private int min(int depth, int maxDepth, int abMin, int abMax) {
        if (checkGameOver(true)) {
            return 9999;
        }
        else if (depth == maxDepth) {
            return eval(true);
        } else {
            LegalMoves best = new LegalMoves();
            best.score = 9999;
            best.mv = null;

            int bestScore = best.score;
            int moveScore;
            genHumanMoves();
            int [][] moves = humanMoves;

            int i = 0;
            while (moves[i][0] != 0 || 
            		moves[i][1] != 0 || 
            		moves[i][2] != 0 || 
            		moves[i][3] != 0
            		) {
                if ((System.nanoTime() - startTimer) > timerMax) {
                    return -77777;
                }
                tryMoveOnBoard(moves[i], true);
                moveScore = max(depth + 1, maxDepth, abMin, abMax);
    			if (moveScore > abMax) { // alpha beta prune
                    retractMove();
                    return moveScore;
                }
                if (moveScore != -77777) {
                    if (moveScore < bestScore) {
                        bestScore = moveScore;
                        best.mv = moves[i];
    					abMin = moveScore;
                    }
                }
                retractMove();
                i++;
            }
            return bestScore;
        }
        
    }

    private int max(int depth, int maxDepth, int abMin, int abMax) {
        if (checkGameOver(true)) {
            return -9999;
        }
        else if (depth == maxDepth) {
            return eval(false);
        } else {
            LegalMoves best = new LegalMoves();
            best.score = -9999;
            best.mv = null;
            int bestScore = best.score;
            int moveScore;
            genMalacathMoves();
            int [][] moves = malacathMoves;

            int i = 0;
            while (moves[i][0] != 0 || 
            		moves[i][1] != 0 || 
            		moves[i][2] != 0 || 
            		moves[i][3] != 0
            		) {
                if ((System.nanoTime() - startTimer) > timerMax) {
                    return -77777;
                }
                tryMoveOnBoard(moves[i], false);
                moveScore = min(depth + 1, maxDepth, abMin, abMax);
                if (moveScore < abMin) { // alpha beta prune
                    retractMove();
                    return moveScore;
                }
                if (moveScore != -77777) {
                    if (moveScore > bestScore) {
                        bestScore = moveScore;
                        best.mv = moves[i];
    					abMax = moveScore;
                    }
                }
                retractMove();
                i++;
            }
            return bestScore;
        }
        
    }

    private void retractMove() {
        int [] move = movesTryStack.pop();
        int w = move[0], x = move[1], y = move[2], z = move[3];
        int piece = move[4];
        board[z][y] = board[x][w];
        board[x][w] = piece;
    }

    private void tryMoveOnBoard(int [] move, boolean turn) {
        int w = move[0], x = move[1], y = move[2], z = move[3];
        movesTryStack.push(new int [] {
        		y, 
        		z, 
        		w, 
        		x, 
        		board[z][y]
        		});
        
        board[z][y] = board[x][w];
        board[x][w] = 0;

    }


    private int eval(boolean turn){
        int score = 0;
        int humanTotalPieces = 0;
        int malacathTotalPieces = 0;
        genMalacathMoves();
        genHumanMoves();
        
        if (malacathMoves[0][0] == 0 && 
        		malacathMoves[0][1] == 0 && 
        		malacathMoves[0][2] == 0 && 
        		malacathMoves[0][3] == 0
        		) {
        	
        	if (turn == true) {
        		return 9999;
        	} else {
        		return -9999;
        	}
        	
        }

        for (int i = 0; i < malacathPieces.length; i++) {
        	// rook
        	if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 6) {
                score += 80;
                
                malacathTotalPieces++;
            }
        	// bishop
        	if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 7) {
                score += 40;
                malacathTotalPieces++;
            }
        	// king
            if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 8) {
                score += 100;
                malacathTotalPieces++;
            }
        	// knight
            if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 9) {
                score += 60;
                malacathTotalPieces++;
            }
            // pawn
            if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 10) {
                score += 20;
                malacathTotalPieces++;
            }
        }
        for (int i = 0; i < humanPieces.length; i++) {
        	// rook
            if (board[humanPieces[i][0]][humanPieces[i][1]] == 1) { 
                score -= 80;
                humanTotalPieces++;
            }
            // bishop
            if (board[humanPieces[i][0]][humanPieces[i][1]] == 2) { 
                score -= 40;
                humanTotalPieces++;
            }
            // king
            if (board[humanPieces[i][0]][humanPieces[i][1]] == 3) {
                score -= 100;
                humanTotalPieces++;
            }
            // knight
            if (board[humanPieces[i][0]][humanPieces[i][1]] == 4) {
                score -= 60;
                humanTotalPieces++;
            }
            // pawn
            if (board[humanPieces[i][0]][humanPieces[i][1]] == 5) {
                score -= 20;
                humanTotalPieces++;
            }
        }
        
        // tune by total pieces of human and Malacath
        score += (malacathTotalPieces * 10);
        score -= (humanTotalPieces * 10);
        
        return score;
    }

    private void printMoves(int[][] moves) {
        for (int[] move1 : moves) {
            String move = "";
            if (move1[0] != 0 || 
            		move1[1] != 0 || 
            		move1[2] != 0 || 
            		move1[3] != 0
            		) {
                for (int j = 0; j < move1.length; j++) {
                    if (j == 0 || j == 2) {
                        move += convertAxis(move1[j]);
                    } else {
                        move += move1[j] + 1 + " ";
                    }
                }
                System.out.println(move);
            }
        }
    }

    private void genMalacathMoves() {
    	
        malacathPieces = new int [18][2];
        malacathMoves = new int [125][4];
        int pieceIndex = 0, moveIndex = 0;

        /*
        0 = -
        1 = r
        2 = b
        3 = k
        4 = n
        5 = p
        6 = R
        7 = B
        8 = K
        9 = N
        10 = P
        */
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                if (board[i][j] == 6 || 
                		board[i][j] == 7 ||
                		board[i][j] == 8 ||
                		board[i][j] == 9 ||
                		board[i][j] == 10
                		) {
                    malacathPieces[pieceIndex][0] = i;
                    malacathPieces[pieceIndex][1] = j;
                    pieceIndex++;
                }
            }
        }
        for (int i = 0; i < pieceIndex; i++) {
        	
            if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 6) { // rook
                // forward
                int x = malacathPieces[i][0] - 1;
                int y = malacathPieces[i][1];
                
                while (x > -1 && 
                		(board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                		) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    if(board[x][y] != 0){
                        break;
                    }
                    x--;
                }
                
                // move back
                x = malacathPieces[i][0] + 1;
                
                while (x < 8 && 
                		(board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                		) {
                	// only captures
                    if (board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5
                			) {
                    	malacathMoves[moveIndex][0] = malacathPieces[i][1];
                        malacathMoves[moveIndex][1] = malacathPieces[i][0];
                        malacathMoves[moveIndex][2] = y;
                        malacathMoves[moveIndex][3] = x;
                        moveIndex++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                }
                
                // move horizontal
                x = malacathPieces[i][0];
                y = malacathPieces[i][1] + 1;
                
                // right
                
                while (y < 6 && 
                		(board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                		) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    y++;
                }
                
                
                // left
                y = malacathPieces[i][1] - 1;
                
                while (y > -1 && 
                		(board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                		) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    y--;
                }

            }
            else if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 7) { // bishop
                // up left
                int x = malacathPieces[i][0] - 1;
                int y = malacathPieces[i][1] - 1;
                while (x > -1 && 
                		y > -1 && 
                        (board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y--;
                }

                // up right
                x = malacathPieces[i][0] - 1;
                y = malacathPieces[i][1] + 1;
                while (x > -1 && 
                		y < 6 && 
                        (board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y++;
                }

                // back left
                x = malacathPieces[i][0] + 1;
                y = malacathPieces[i][1] - 1;
                while (x < 8 && 
                		y > -1 && 
                        (board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                        ) {
                    // only if move is a capture
                    if (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5) {
                        malacathMoves[moveIndex][0] = malacathPieces[i][1];
                        malacathMoves[moveIndex][1] = malacathPieces[i][0];
                        malacathMoves[moveIndex][2] = y;
                        malacathMoves[moveIndex][3] = x;
                        moveIndex++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y--;
                }

                // back right
                x = malacathPieces[i][0] + 1;
                y = malacathPieces[i][1] + 1;
                while (x < 8 && 
                		y < 6 && 
                        (board[x][y] == 0 || 
                			board[x][y] == 1 || 
                			board[x][y] == 2 || 
                			board[x][y] == 3 || 
                			board[x][y] == 4 || 
                			board[x][y] == 5)
                        ) {
                    // only if move is a capture
                    if (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5) {
                        malacathMoves[moveIndex][0] = malacathPieces[i][1];
                        malacathMoves[moveIndex][1] = malacathPieces[i][0];
                        malacathMoves[moveIndex][2] = y;
                        malacathMoves[moveIndex][3] = x;
                        moveIndex++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y++;
                }
            } 
            else if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 8) { // king
            	// can only move to the right
                // one square at a time

                //can capture right or left one square

                // move horizontal
            	
            	// left
                int x = malacathPieces[i][0];
                int y = malacathPieces[i][1] - 1;
                if (y > -1 && 
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }

                // right
                y = malacathPieces[i][1] + 1;
                if (y < 6 && 
                        (board[x][y] == 0 || 
                          	board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }
            } 
            else if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 9) { // knight
            	// move 1 right and 2 forward
                int x = malacathPieces[i][0] - 2;
                int y = malacathPieces[i][1] + 1;
                
                if (x > -1 && 
                		y < 6 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }

                // move 1 left and 2 forward
                x = malacathPieces[i][0] - 2;
                y = malacathPieces[i][1] - 1;
                
                if (x > -1 && 
                		y > -1 &&
                        (board[x][y] == 0 || 
                            board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }

                // move 1 right backward 2
                x = malacathPieces[i][0] + 2;
                y = malacathPieces[i][1] + 1;
                
                // only capture
                if(x < 8 && 
                		y < 6 &&
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }

                // move 1 left backward 2
                x = malacathPieces[i][0] + 2;
                y = malacathPieces[i][1] - 1;
                
                // only captures
                if(x < 8 && 
                		y > -1 &&
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }
                
                // 2 left 1 forward
                x = malacathPieces[i][0] - 1;
                y = malacathPieces[i][1] - 2;
                
                if (x > -1 && 
                		y > -1 &&
                        (board[x][y] == 0 ||
                        	board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // 2 right 1 forward
                x = malacathPieces[i][0] - 1;
                y = malacathPieces[i][1] + 2;
                
                if (x > -1 && 
                		y < 6 &&
                        (board[x][y] == 0 ||
                            board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // move 2 left 1 backward
                x = malacathPieces[i][0] + 1;
                y = malacathPieces[i][1] - 2;
                
                if (x < 8 && 
                		y > -1 &&
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // move 2 right 1 backward
                x = malacathPieces[i][0] + 1;
                y = malacathPieces[i][1] + 2;
                
                if (x < 8 && 
                		y < 6 &&
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }
            } 
            else if (board[malacathPieces[i][0]][malacathPieces[i][1]] == 10) { // pawn
            	// move forward
                int x = malacathPieces[i][0] - 1;
                int y = malacathPieces[i][1];
                
                if (x > -1 && (board[x][y] == 0)) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }

                // up right
                // only if capture
                x = malacathPieces[i][0] - 1;
                y = malacathPieces[i][1] + 1;
                if (x > -1 && 
                		y < 6 && 
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }

                // up left
                // only if capture
                x = malacathPieces[i][0] - 1;
                y = malacathPieces[i][1] - 1;
                if (x > -1 && 
                		y > -1 && 
                        (board[x][y] == 1 || 
                            board[x][y] == 2 || 
                            board[x][y] == 3 || 
                            board[x][y] == 4 || 
                            board[x][y] == 5)
                        ) {
                    malacathMoves[moveIndex][0] = malacathPieces[i][1];
                    malacathMoves[moveIndex][1] = malacathPieces[i][0];
                    malacathMoves[moveIndex][2] = y;
                    malacathMoves[moveIndex][3] = x;
                    moveIndex++;
                }
            }
        }

    }

    private void genHumanMoves(){
    	humanPieces = new int [18][2];
        humanMoves = new int [125][4];
    	int pieceIndex = 0, moveIndex = 0;

        /*
        0 = -
        1 = r
        2 = b
        3 = k
        4 = n
        5 = p
        6 = R
        7 = B
        8 = K
        9 = N
        10 = P
        */
        
        // get all humans pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                if (board[i][j] == 1 || 
                		board[i][j] == 2 ||
                		board[i][j] == 3 || 
                		board[i][j] == 4 || 
                		board[i][j] == 5
                		) {
                    humanPieces[pieceIndex][0] = i;
                    humanPieces[pieceIndex][1] = j;
                    pieceIndex++;
                }
            }
        }

        for (int i = 0; i < pieceIndex; i++) {
        	
            if (board[humanPieces[i][0]][humanPieces[i][1]] == 1) { // rook
                // move vertically up
                int x = humanPieces[i][0] + 1;
                int y = humanPieces[i][1];
                
                while(x < 8 && 
                		(board[x][y] == 0 || 
                			board[x][y] == 6 || 
                			board[x][y] == 7 || 
                			board[x][y] == 8 || 
                			board[x][y] == 9 || 
                			board[x][y] == 10)
                		) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                }
                
                // move vertically down
                x = humanPieces[i][0] - 1;
                
                while (x > -1 && 
                		(board[x][y] == 0 || 
	            			board[x][y] == 6 || 
	            			board[x][y] == 7 || 
	            			board[x][y] == 8 || 
	            			board[x][y] == 9 || 
	            			board[x][y] == 10)
                		) {
                    if (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10) {
                    	humanMoves[moveIndex][0] = humanPieces[i][1];
                        humanMoves[moveIndex][1] = humanPieces[i][0];
                        humanMoves[moveIndex][2] = y;
                        humanMoves[moveIndex][3] = x;
                        moveIndex++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                }
                
                // move horizontal
                
                // right
                x = humanPieces[i][0];
                y = humanPieces[i][1] + 1;
                
                while (y < 6 && 
                		(board[x][y] == 0 || 
	            			board[x][y] == 6 || 
	            			board[x][y] == 7 || 
	            			board[x][y] == 8 || 
	            			board[x][y] == 9 || 
	            			board[x][y] == 10)
                		) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                
                    if (board[x][y] != 0) {
                        break;
                    }
                    y++;
                }
                
                // left
                y = humanPieces[i][1] - 1;
                
                while (y > -1 && 
                		(board[x][y] == 0 || 
	            			board[x][y] == 6 || 
	            			board[x][y] == 7 || 
	            			board[x][y] == 8 || 
	            			board[x][y] == 9 || 
	            			board[x][y] == 10)
                		) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    y--;
                }
            
            }
            else if (board[humanPieces[i][0]][humanPieces[i][1]] == 2) { // bishop
                // up right
                int x = humanPieces[i][0] + 1;
                int y = humanPieces[i][1] + 1;
                while (x < 8 && 
                		y < 6 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y++;
                }

                // up left
                x = humanPieces[i][0] + 1;
                y = humanPieces[i][1] - 1;
                while (x < 8 && 
                		y > -1 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    if (board[x][y] != 0) {
                        break;
                    }
                    x++;
                    y--;
                }

                // down right
                x = humanPieces[i][0] - 1;
                y = humanPieces[i][1] + 1;
                while (x > -1 && 
                		y < 6 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    // only if move is a capture
                    if (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10) {
                        humanMoves[moveIndex][0] = humanPieces[i][1];
                        humanMoves[moveIndex][1] = humanPieces[i][0];
                        humanMoves[moveIndex][2] = y;
                        humanMoves[moveIndex][3] = x;
                        moveIndex++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y++;
                }

                // down left
                x = humanPieces[i][0] - 1;
                y = humanPieces[i][1] - 1;
                while (x > -1 && 
                		y > -1 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    // only if move is a capture
                    if (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10) {
                        humanMoves[moveIndex][0] = humanPieces[i][1];
                        humanMoves[moveIndex][1] = humanPieces[i][0];
                        humanMoves[moveIndex][2] = y;
                        humanMoves[moveIndex][3] = x;
                        moveIndex++;
                    }
                    if (board[x][y] != 0) {
                        break;
                    }
                    x--;
                    y--;
                }
            } 
            else if (board[humanPieces[i][0]][humanPieces[i][1]] == 3) { // king
            	// can only move to the left
                // one square at a time

                //can capture right or left one square

                // move horizontal
            	// left
            	int x = humanPieces[i][0];
                int y = humanPieces[i][1] - 1;
                
                if (y > -1 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }

                // right
                y = humanPieces[i][1] + 1;
                
                if (y < 6 && 
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
            } 
            else if (board[humanPieces[i][0]][humanPieces[i][1]] == 4) { // knight
            	// move 1 right and 2 up
                int x = humanPieces[i][0] + 2;
                int y = humanPieces[i][1] + 1;
                
                if (x < 8 && 
                		y < 6 && 
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }

                // move 1 left and 2 up
                x = humanPieces[i][0] + 2;
                y = humanPieces[i][1] - 1;
                
                if(x < 8 && 
                		y > -1 &&
                        (board[x][y] == 0 || 
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }

                // move 1 right down 2
                x = humanPieces[i][0] - 2;
                y = humanPieces[i][1] + 1;
                
                if (x > -1 && 
                		y < 6 &&
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }

                // move 1 left down 2
                x = humanPieces[i][0] - 2;
                y = humanPieces[i][1] - 1;
                
                if (x > -1 && 
                		y > -1 &&
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // 2 left 1 forward
                x = humanPieces[i][0] + 1;
                y = humanPieces[i][1] - 2;
                
                if (x < 8 && 
                		y > -1 &&
                        (board[x][y] == 0 ||
                        	board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // 2 right 1 forward
                x = humanPieces[i][0] + 1;
                y = humanPieces[i][1] + 2;
                
                if (x < 8 && 
                		y < 6 &&
                        (board[x][y] == 0 ||
                            board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // move 2 left 1 backward
                x = humanPieces[i][0] - 1;
                y = humanPieces[i][1] - 2;
                
                if (x > -1 && 
                		y > -1 &&
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }
                
                // move 2 right 1 backward
                x = humanPieces[i][0] - 1;
                y = humanPieces[i][1] + 2;
                
                if (x > -1 && 
                		y < 6 &&
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                }
            } 
            else if (board[humanPieces[i][0]][humanPieces[i][1]] == 5) { // pawn
            	// move vertically up
                int x = humanPieces[i][0] + 1;
                int y = humanPieces[i][1];
                
                if (x < 8 && (board[x][y] == 0)) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }

                // up right
                // only if capture
                x = humanPieces[i][0] + 1;
                y = humanPieces[i][1] + 1;
                if (x < 8 && 
                		y < 6 && 
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }

                // up left
                // only if capture
                x = humanPieces[i][0] + 1;
                y = humanPieces[i][1] - 1;
                if (x < 8 && 
                		y > -1 && 
                        (board[x][y] == 6 || 
                            board[x][y] == 7 || 
                            board[x][y] == 8 || 
                            board[x][y] == 9 || 
                            board[x][y] == 10)
                        ) {
                    humanMoves[moveIndex][0] = humanPieces[i][1];
                    humanMoves[moveIndex][1] = humanPieces[i][0];
                    humanMoves[moveIndex][2] = y;
                    humanMoves[moveIndex][3] = x;
                    moveIndex++;
                    
                }
            }
        }
        
    }

    private int boardTranslation(int i){
        switch (i) {
	        case 0:
	            return 5;
	        case 1:
	            return 4;
	        case 2:
	            return 3;
	        case 3:
	            return 2;
	        case 4:
	            return 1;
	        case 5:
	            return 0;
            default: // error
            	return -1;
        }
    }

    private boolean checkNoLegalMoves(int i, int [][] moves) {
    	return (moves[i][0] == 0 && 
        		moves[i][1] == 0 && 
        		moves[i][2] == 0 && 
        		moves[i][3] == 0
        		);
    }
    
    private boolean checkGameOver(boolean turn) {
        int[][] moves;

        boolean human = false;
        boolean malacath = false;
        // check if kings are still on the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                if (board[i][j] == 8) {
                	malacath = true;
                }
                if (board[i][j] == 3) {
                	human = true;
                }
            }
        }
        
        if (malacath == false) {
        	victory = 1;
        	return true;
        }
        if (human == false) {
        	victory = 2;
        	return true;
        }
        
        // check if no possible moves
        if (turn) {
        	genHumanMoves();
            moves = humanMoves;
            
            if (checkNoLegalMoves(0, moves)) {
            	victory = 2;
                return true;
            }
        } else {
        	genMalacathMoves();
            moves = malacathMoves;
            
            if (checkNoLegalMoves(0, moves)) {
            	victory = 1;
                return true;
            }
        }
        
        return false;
    }

    private boolean checkLegalMove(String move) {
    
        
        int w = convertLetter(move.charAt(0));
        int x = Character.getNumericValue(move.charAt(1)) - 1;
        int y = convertLetter(move.charAt(2));
        int z = Character.getNumericValue(move.charAt(3)) - 1;
        
        genHumanMoves();
        int [][] moves = humanMoves;
        int i = 0;
        
        while (moves[i][0] != 0 || 
        		moves[i][1] != 0 || 
        		moves[i][2] != 0 || 
        		moves[i][3] != 0
        		) {
            if (moves[i][0] == w && 
            		moves[i][1] == x && 
            		moves[i][2] == y && 
            		moves[i][3] == z
            		) {
                return true;
            }
            i++;
        }

        System.out.println("Illegal move detected please re-enter a valid move.");
        return false;
    }

    private void makeMove(String move, boolean turn) {

        int w = convertLetter(move.charAt(0));
        int x = Character.getNumericValue(move.charAt(1)) - 1;
        int y = convertLetter(move.charAt(2));
        int z = Character.getNumericValue(move.charAt(3)) - 1;
        
        if (board[x][w] == 1 || 
        		board[x][w] == 2 || 
				board[x][w] == 4 || 
				board[x][w] == 6 || 
				board[x][w] == 7 || 
				board[x][w] == 9) {
        	board[z][y] = morph(board[x][w]);
        } else {
        	board[z][y] = board[x][w];
        }
        
        board[x][w] = 0;

    }
    
    public int morph(int piece) {
    	switch (piece) {
	        case 1:
	            return 2;
	        case 2:
	            return 4;
	        case 4:
	            return 1;
	        case 6:
	            return 7;
	        case 7:
	            return 9;
	        case 9:
	            return 6;
            default: // error
            	return -1;
    	}
    }

    private int convertLetter(char c) {
        switch (c){
            case 'A':
            	return 0;
        	case 'a':
                return 0;
            case 'B':
            	return 1;
            case 'b':
                return 1;
            case 'C':
            	return 2;
            case 'c':
                return 2;
            case 'D':
            	return 3;
            case 'd':
                return 3;
            case 'E':
            	return 4;
            case 'e':
                return 4;
            case 'F':
            	return 5;
            case 'f':
                return 5;
            default: // error
                return -1;
        }
    }

    private char convertAxis(int c) {
        switch (c){
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
            case 3:
                return 'D';
            case 4:
                return 'E';
            case 5:
                return 'F';
            default:
                return 'X';
        }
    }


    private void printBoard() {
        for (int i = 7; i >= 0; i--) {
            System.out.print(i + 1 + "  ");
            String line = "";
            for (int j = 0; j < 6; j++) {
                line += convert(board[i][j]) + " ";
            }
            System.out.println(line + "\n");
        }
        System.out.println("--------------");
        System.out.println("   A B C D E F");
    }

    private String convert(int i) {
        /*
        0 = -
        1 = r
        2 = b
        3 = k
        4 = n
        5 = p
        6 = R
        7 = B
        8 = K
        9 = N
        10 = P
        */
        
        switch (i){
            case 0:
                return "-";
            case 1:
                return "r";
            case 2:
                return "b";
            case 3:
                return "k";
            case 4:
                return "n";
            case 5:
                return "p";
            case 6:
                return "R";
            case 7:
                return "B";
            case 8:
                return "K";
            case 9:
                return "N";
            case 10:
                return "P";
            default:
                return "-";
        }
       
    }

}

class LegalMoves {
    public int score;
    public int [] mv;

    LegalMoves() {}

}

