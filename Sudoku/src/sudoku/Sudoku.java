package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Input  : File(9x9). If not put, then "input.txt" is used.
 * Output : "output.txt" if it can be solved.
 *          Otherwise, print "This cannot be solved." to standard out.
 *  * Precondition : 9 x 9
 */
public class Sudoku {
  private int[][] cell;
  private static boolean test = false;
  private static final int SUDOKU_SIZE = 9;
  private static int[][] original = new int[SUDOKU_SIZE][SUDOKU_SIZE];
  private static final String INPUT_FILENAME = new File("").getAbsolutePath() + "/input.txt";
  private static final String OUTPUT_FILENAME = new File("").getAbsolutePath() + "/output.txt";
  private boolean isComplete = false;

  /**
   * @param args
   */
  public static void main(String[] args) {
    String file = null;
    if (args.length == 0 || args[0] == null || args[0].isEmpty() ) {
      file = INPUT_FILENAME;
    } else {
      file = args[0];
    }
    int[][] input = readFileAndConvert(file);
    if (input == null) {
      System.out.println("Cannot start the Sudoku");
      return;
    }
    Sudoku sudoku = new Sudoku(input);
    if (sudoku.executeSudokuFrom(0, 0)) {
      sudoku.writeResultToFile(OUTPUT_FILENAME);
    }
    sudoku.displayBoardStatus();

    // >> test data
    if (test) {
      Sudoku sudoku1 = new Sudoku(test_1);
      if (sudoku1.executeSudokuFrom(0, 0)) {}
      sudoku1.displayBoardStatus();

      Sudoku sudoku2 = new Sudoku(test_2);
      if (sudoku2.executeSudokuFrom(0, 0)) {}
      sudoku2.displayBoardStatus();

      Sudoku sudoku3 = new Sudoku(test_3);
      if (sudoku3.executeSudokuFrom(0, 0)) {}
      sudoku3.displayBoardStatus();

      Sudoku sudoku4 = new Sudoku(test_4);
      if (sudoku4.executeSudokuFrom(0, 0)) {}
      sudoku4.displayBoardStatus();
    }
    // << test data
  }

  /*
   *  Constructor
   */
  public Sudoku(int[][] board) {
    this.isComplete = false;
    this.cell = board;
    this.copyData(original, cell);
  }

  /*
   * List the possible numbers for the cell.
   *  If [1, 2, 7] are available, return {(T), F, F, T, T, T, T, F, T, T}.
   *  If row or column is out of range, return null.
   */
  private boolean[] getCandidates(int row, int column) {
    boolean[] numberUsed = {true, false, false, false, false, false, false, false, false, false};
    if (row > 9 || column > 9) {
      return null;
    }
    for (int i = 0; i < SUDOKU_SIZE; i++) {
      numberUsed[cell[i][column]] = true;
      numberUsed[cell[row][i]] = true;
      int block_left = (row / 3) * 3;
      int block_up = (column / 3) * 3;
      numberUsed[cell[block_left + i / 3][block_up + i % 3]] = true;
    }
    return numberUsed;
  }

  /*
   * Start to fill cells from specified cell.
   *  Traverse from [0, 0] -> [0, 1] -> ... -> [1, 0] -> ... -> [8, 8]
   */
  public boolean executeSudokuFrom(int row, int column) {
    int next_position = row * SUDOKU_SIZE + column + 1;
    if (next_position > 81) {
      // Reached the final position without error
      isComplete = true;
      return true;
    }
    if (cell[row][column] != 0) {
      // A value has already been assigned, so go to the next cell
      return executeSudokuFrom(next_position / SUDOKU_SIZE, next_position % SUDOKU_SIZE);
    }
    boolean[] numberUsed = getCandidates(row, column);
    // Try to put and check if continue till the end
    for (int i = 1; i <= SUDOKU_SIZE; i++) {
      if (!numberUsed[i]) {
        cell[row][column] = i;
        if (executeSudokuFrom(next_position / SUDOKU_SIZE, next_position % SUDOKU_SIZE)) {
          return true;
        }
      }
    }
    cell[row][column] = 0;
    return false;
  }

  // Copy each value from src to dst
  private void copyData(int[][] dst, final int[][] src) {
    for (int row = 0; row < SUDOKU_SIZE; row++) {
      for (int column = 0; column < SUDOKU_SIZE; column++) {
        dst[row][column] = src[row][column];
      }
    }
  }

  // Read file and convert each number from String to int
  private static int[][] readFileAndConvert(String filename) {
    int[][] result = new int[SUDOKU_SIZE][SUDOKU_SIZE];
    File file = new File(filename);
    try {
      FileReader filereader = new FileReader(file);
      for (int row = 0; row < SUDOKU_SIZE; row++) {
        for (int column = 0; column < SUDOKU_SIZE;) {
          int temp = filereader.read();
          if (0x30 <= temp && temp <= 0x39) {
            result[row][column++] = Character.getNumericValue(temp);
          } else if (temp == -1) {
            System.out.println("EOF before 9x9");
            filereader.close();
            return null;
          }
        }
      }
      filereader.close();
    } catch (FileNotFoundException e) {
      System.out.println("file not found.");
      return null;
    } catch (IOException e) {
      System.out.println("file I/O error.");
      return null;
    }
    return result;
  }

  // Output the result to file
  private void writeResultToFile(String filename) {
    File file = new File(filename);
    try {
      FileWriter filewriter = new FileWriter(file);
      for (int row = 0; row < SUDOKU_SIZE; row++) {
        for (int column = 0; column < SUDOKU_SIZE; column++) {
          filewriter.write('0' + cell[row][column]);
        }
        filewriter.write('\n'); // Line Feed
      }
      filewriter.close();
    } catch (IOException e) {
      System.out.println("Something went wrong while writing.");
    }
  }

  // Show the board status
  private void displayBoardStatus() {
    System.out.print("=== start ===");
    if (isComplete == false) {
      System.out.println("This cannot be solved.");
    } else {
      for (int row = 0; row < SUDOKU_SIZE; row++) {
        if ((row % 3) == 0) {
          System.out.println("");
        }
        for (int column = 0; column < SUDOKU_SIZE; column++) {
          if ((column % 3) == 0) {
            System.out.print(" [" + cell[row][column] + "] ");
          } else {
            System.out.print("[" + cell[row][column] + "] ");
          }
        }
        System.out.println("");
      }
    }
    System.out.println("=== end ===");
  }

  // One assumption is "9x9".
  private static final int[][] test_1 = { {0, 0, 7, 0, 0, 0, 4, 0, 0}, {3, 0, 6, 0, 2, 0, 1, 0, 8},
      {0, 2, 0, 5, 0, 9, 0, 3, 0}, {5, 0, 0, 0, 4, 0, 0, 0, 9}, {0, 9, 0, 0, 8, 0, 0, 5, 0},
      {6, 0, 0, 0, 7, 0, 0, 0, 3}, {0, 8, 0, 7, 0, 2, 0, 4, 0}, {4, 0, 9, 0, 6, 0, 5, 0, 2},
      {0, 0, 3, 0, 0, 0, 9, 0, 0}};
  private static final int[][] test_2 = { {0, 0, 8, 0, 0, 5, 9, 0, 0}, {2, 0, 0, 0, 3, 0, 0, 4, 0},
      {1, 9, 0, 0, 0, 7, 0, 0, 5}, {0, 0, 0, 8, 0, 0, 5, 0, 0}, {0, 4, 0, 3, 9, 6, 0, 7, 0},
      {0, 0, 3, 0, 0, 2, 0, 0, 0}, {4, 0, 0, 5, 0, 0, 0, 1, 9}, {0, 8, 0, 0, 1, 0, 0, 0, 6},
      {0, 0, 5, 7, 0, 0, 2, 0, 0}};
  private static final int[][] test_3 = { {1, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0}};
  private static final int[][] test_4 = { {1, 0, 8, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 4, 1},
      {0, 0, 0, 0, 1, 0, 0, 0, 5}, {0, 1, 0, 8, 0, 0, 5, 0, 0}, {0, 0, 0, 0, 0, 1, 0, 7, 0},
      {2, 3, 4, 0, 0, 0, 0, 0, 0}, {4, 0, 0, 0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0, 1, 0, 0},
      {0, 0, 5, 1, 0, 0, 0, 0, 0}};

}
