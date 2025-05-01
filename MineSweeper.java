import java.util.*;

public class MineSweeper {
    private static final int SIZE = 10;
    private static final int MINE = -1;
    private static final int EMPTY = 0;
    private static final int HIDDEN = -2;
    private static final int FLAGGED = -3;

    private int[][] fieldVisible = new int[SIZE][SIZE];
    private int[][] fieldHidden = new int[SIZE][SIZE];
    private Random random = new Random();
    private Scanner sc = new Scanner(System.in);
    private int minesLeft = 10;
    private boolean gameOver = false;

    public static void main(String[] args) {
        MineSweeper game = new MineSweeper();
        game.startGame();
    }

    public void startGame() {
        System.out.println("\nWelcome to Minesweeper!");
        System.out.println("Commands: ");
        System.out.println("- Enter coordinates (row column) to reveal a cell");
        System.out.println("- Enter 'f row column' to flag or unflag a cell");
        setupField();

        while (!gameOver) {
            displayVisible();
            if (checkWin()) {
                System.out.println("You won!");
                displayHidden();
                break;
            }
            playMove();
        }
    }

    public void setupField() {
        // Initialize fields
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(fieldVisible[i], HIDDEN);
            Arrays.fill(fieldHidden[i], EMPTY);
        }

        // Place mines
        int minesPlaced = 0;
        while (minesPlaced < 10) {
            int i = random.nextInt(SIZE);
            int j = random.nextInt(SIZE);
            if (fieldHidden[i][j] != MINE) {
                fieldHidden[i][j] = MINE;
                minesPlaced++;
            }
        }
        buildHidden();
    }

    public void buildHidden() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (fieldHidden[i][j] == MINE) continue;

                int count = 0;
                for (int r = i - 1; r <= i + 1; r++) {
                    for (int c = j - 1; c <= j + 1; c++) {
                        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE && fieldHidden[r][c] == MINE) {
                            count++;
                        }
                    }
                }
                fieldHidden[i][j] = count;
            }
        }
    }

    public void displayVisible() {
        System.out.println("\nMines left: " + minesLeft);
        System.out.print("\t ");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(" " + i + "  ");
        }
        System.out.println();

        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + "\t| ");
            for (int j = 0; j < SIZE; j++) {
                switch (fieldVisible[i][j]) {
                    case HIDDEN:
                        System.out.print("■");
                        break;
                    case FLAGGED:
                        System.out.print("⚑");
                        break;
                    case EMPTY:
                        System.out.print(" ");
                        break;
                    case MINE:
                        System.out.print("X");
                        break;
                    default:
                        System.out.print(fieldVisible[i][j]);
                        break;
                }
                System.out.print(" | ");
            }
            System.out.println();
        }
    }

    public void playMove() {
        System.out.print("\nEnter your move (row column or 'f row column'): ");
        String input = sc.nextLine().trim();

        try {
            if (input.startsWith("f")) {
                // Flagging a cell
                String[] parts = input.split(" ");
                if (parts.length != 3) throw new Exception();

                int i = Integer.parseInt(parts[1]);
                int j = Integer.parseInt(parts[2]);

                if (!isValidPosition(i, j)) throw new Exception();

                if (fieldVisible[i][j] == HIDDEN) {
                    fieldVisible[i][j] = FLAGGED;
                    minesLeft--;
                } else if (fieldVisible[i][j] == FLAGGED) {
                    fieldVisible[i][j] = HIDDEN;
                    minesLeft++;
                } else {
                    System.out.println("Cannot flag a revealed cell.");
                }
            } else {
                // Revealing a cell
                String[] parts = input.split(" ");
                if (parts.length != 2) throw new Exception();

                int i = Integer.parseInt(parts[0]);
                int j = Integer.parseInt(parts[1]);

                if (!isValidPosition(i, j)) throw new Exception();

                if (fieldVisible[i][j] == FLAGGED) {
                    System.out.println("Cell is flagged. Unflag it first.");
                    return;
                }

                if (fieldHidden[i][j] == MINE) {
                    revealAllMines();
                    displayVisible();
                    System.out.println("You hit a mine!");
                    System.out.println("============ GAME OVER ============");
                    gameOver = true;
                } else {
                    revealCell(i, j);
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input! Please use format: row column OR f row column");
        }
    }

    private boolean isValidPosition(int i, int j) {
        return i >= 0 && i < SIZE && j >= 0 && j < SIZE;
    }

    public void revealCell(int i, int j) {
        if (!isValidPosition(i, j)) return;
        if (fieldVisible[i][j] != HIDDEN) return;

        fieldVisible[i][j] = fieldHidden[i][j];

        if (fieldHidden[i][j] == EMPTY) {
            // Reveal all adjacent empty cells recursively
            for (int r = i - 1; r <= i + 1; r++) {
                for (int c = j - 1; c <= j + 1; c++) {
                    if (!(r == i && c == j)) {
                        revealCell(r, c);
                    }
                }
            }
        }
    }

    public void revealAllMines() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (fieldHidden[i][j] == MINE) {
                    fieldVisible[i][j] = MINE;
                }
            }
        }
    }

    public boolean checkWin() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (fieldHidden[i][j] != MINE && fieldVisible[i][j] == HIDDEN) {
                    return false;
                }
            }
        }
        return true;
    }

    public void displayHidden() {
        System.out.println("\nFinal Board:");
        System.out.print("\t ");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(" " + i + "  ");
        }
        System.out.println();

        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + "\t| ");
            for (int j = 0; j < SIZE; j++) {
                if (fieldHidden[i][j] == MINE) {
                    System.out.print("X");
                } else if (fieldHidden[i][j] == EMPTY) {
                    System.out.print(" ");
                } else {
                    System.out.print(fieldHidden[i][j]);
                }
                System.out.print(" | ");
            }
            System.out.println();
        }
    }
}
