package blockPuzzle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Input  : [N, M], [(n1, m1), (n2, m2), ..., (ni, mi)]
 * Output : figure with html format
 */
public class BlockPuzzle {
  private static boolean debug = true;

  // Variable for the whole result board
  private int[][] result; // -1 : empty, 1~N(n) : filled by item "n"
  private int result_height = 0, result_width = 0;
  private static final String HTML_FILE = new File("").getAbsolutePath() + "/result.html";

  // Class for each block
  public class Block {
    // Size
    public int width;
    public int height;
    // ID
    public int index;
    // Allocated place
    public int[] edge = new int[2];
    // Status
    public static final int UnUsed = 0, Using = 1, Used = 2;
    public int isUsed;

    Block(int y, int x, int i) {
      width = x;
      height = y;
      index = i;
      isUsed = UnUsed;
      edge[0] = 0;
      edge[1] = 0;
    }

    // Status moved to not-allocated
    public void resetStatus() {
      isUsed = UnUsed;
      edge[0] = 0;
      edge[1] = 0;
    }
  };

  // Block Group
  private Queue<Block> blocks = new LinkedList<Block>();
  private int blockNumber = 0;

  /**
   * @param args
   */
  public static void main(String[] args) {
    int N1 = 4, M1 = 3;
    BlockPuzzle bp1 = new BlockPuzzle(N1, M1);
    bp1.setTestData(1);
    bp1.initializeResultArea();
    if (debug) {
      bp1.printResult();
    }
    boolean res = bp1.runPuzzle();
    if (res) {
      System.out.println("success");
    } else {
      System.out.println("fail");
    }
    if (debug) {
      bp1.printResult();
    }
    bp1.outputAsHtml(res);
    /*
    int N2 = 6, M2 = 4;
    BlockPuzzle bp2 = new BlockPuzzle(N2, M2);
    bp2.setTestData(2);
    bp2.initializeResultArea();
    if (debug) {
      bp2.printResult();
    }
    if (bp2.runPuzzle()) {
      System.out.println("success");
    } else {
      System.out.println("fail");
    }
    if (debug) {
      bp2.printResult();
      bp2.outputAsHtml();
    }
    int N3 = 6, M3 = 4;
    BlockPuzzle bp3 = new BlockPuzzle(N3, M3);
    bp3.setTestData(3);
    bp3.initializeResultArea();
    if (debug) {
      bp3.printResult();
    }
    res = bp3.runPuzzle();
    if (res) {
      System.out.println("success");
    } else {
      System.out.println("fail");
    }
    if (debug) {
      bp3.printResult();
      bp3.outputAsHtml(res);
    }
    */
  }

  // Constructor
  BlockPuzzle(int h, int w, Queue<Block> b) {
    result_height = h;
    result_width = w;
    blocks = b;
    result = new int[result_width][result_height];
  }

  // Constructor
  BlockPuzzle(int h, int w) {
    result_height = h;
    result_width = w;
    result = new int[result_width][result_height];
  }

  // Add a block to Queue
  private void addBlock(Block b) {
    blocks.add(b);
  }

  // Remove a block from Queue
  private Block removeBlock() {
    if (blocks.isEmpty()) {
      return null;
    }
    Block block = blocks.remove();
    return block;
  }

  // Peek
  private Block peekBlock() {
    return blocks.peek();
  }

  // Size of Queue
  private int countBlock() {
    return blocks.size();
  }

  /*
   * Initialize the result area (by setting result[][] as "-1")
   */
  public void initializeResultArea() {
    for (int y = 0; y < result_height; y++) {
      for (int x = 0; x < result_width; x++) {
        result[x][y] = -1;
      }
    }
  }

  /*
   * Main part of the Puzzle
   *  Search starts from [0,0] to [N,M], try to allocate one by one.
   */
  public boolean runPuzzle() {
    for (int i = 0; i < blockNumber; i++) {
      Block block = removeBlock();
      if (block == null) {
        return true;
      }
      if (block.isUsed != Block.Used) {
        int[] pos = getClosestEmpty();
        if (pos == null) {
          return false;
        }
        block.isUsed = Block.Using;
        block.edge = pos;
        if (allocateBlock(block, pos)) {
          block.isUsed = Block.Used;
          if (runPuzzle()) {
            addBlock(block);
            return true;
          } else {
            removeBlock(block, pos);
            block.resetStatus();
          }
        } else {
          block.resetStatus();
        }
      }
      addBlock(block);
    }
    return false;
  }

  // Print the result status to standard output
  private void printResult() {
    for (int y = 0; y < result_height; y++) {
      for (int x = 0; x < result_width; x++) {
        System.out.print(result[x][y]);
      }
      System.out.println();
    }
  }

  // Generate a HTML to show the result
  private void outputAsHtml(boolean success) {
    StringBuilder builder = new StringBuilder();
    builder.append("<!DOCTYPE html>\n");
    builder.append("<html lang=\"en\">\n");
    builder.append("<head><title>blockPuzzle</title></head>\n");
    builder.append("<body><h1>The result of test block (" + result_height + "x" + result_width
        + " )</h1></body>\n");
    if (success) {
      int cell_size = 100;
      builder.append("<canvas id=\"result\" width=\" " + (result_width * 100 + 20)
          + "\" height=\" " + (result_height * 100 + 20) + "\">\n");
      builder.append("<p>This html uses canvas style</p>\n");
      builder.append("</canvas>\n");
      builder.append("<script>\n");
      builder.append("var start = 10;\n");
      builder.append("var size = " + cell_size + ";\n");
      builder.append("var canvas = document.getElementById('result');\n");
      builder.append("var context = canvas.getContext('2d');\n");
      while (true) {
        Block block = removeBlock();
        if (block == null) {
          break;
        }
        builder.append("context.beginPath();\n");
        int x_pos = cell_size * block.edge[0];
        int y_pos = cell_size * block.edge[1];
        int b_width = cell_size * block.width;
        int b_height = cell_size * block.height;
        builder.append("context.lineWidth = 5;\n");
        builder.append("context.strokeRect(start + " + x_pos + ", start + " + y_pos + ", "
            + b_width + ", " + b_height + ");\n");
        builder.append("context.fillStyle = '#aaaaff';\n");
        builder.append("context.fillRect(start + " + x_pos + ", start + " + y_pos + ", " + b_width
            + ", " + b_height + ");\n");
        builder.append("context.font = \"20px 'Arial'\";\n");
        builder.append("context.lineWidth = 1;\n");
        builder.append("context.strokeText('" + block.height + "x" + block.width + "', "
            + (x_pos + b_width / 10) + ", " + (y_pos + b_height) + ");\n");
        builder.append("context.closePath();\n");
      }
      builder.append("</script>\n");
    } else {
      builder.append("<p>No way to solve this problem.</p>\n");
    }
    builder.append("</html>\n");
    String html = builder.toString();
    System.out.println(HTML_FILE);
    File file = new File(HTML_FILE);
    try {
      FileWriter filewriter = new FileWriter(file);
      filewriter.write(html);
      filewriter.close();
    } catch (IOException e) {
      System.out.println("Something went wrong while writing.");
    }
  }

  // Allocate one block to the specified position if possible
  private boolean allocateBlock(Block block, int[] position) {
    if (!checkAllocatable(block, position)) {
      return false;
    }
    for (int x = 0; x < block.width; x++) {
      for (int y = 0; y < block.height; y++) {
        int x_pos = position[0] + x;
        int y_pos = position[1] + y;
        if (x_pos > result_width || y_pos > result_height) {
          return false;
        } else {
          result[x_pos][y_pos] = block.index;
          block.isUsed = Block.Used;
        }
      }
    }
    return true;
  }

  // Remove one allocated block from the result board
  private boolean removeBlock(Block block, int[] position) {
    if (!isExist(block, position)) {
      return false;
    }
    for (int x = 0; x < block.width; x++) {
      for (int y = 0; y < block.height; y++) {
        int x_pos = position[0] + x;
        int y_pos = position[1] + y;
        if (x_pos > result_width || y_pos > result_height) {
          return false;
        } else {
          result[x_pos][y_pos] = -1;
        }
      }
    }
    return true;
  }

  // Check if a block is existed on the result board
  private boolean isExist(Block block, int[] position) {
    if ((block.width + position[0] > result_width) || (block.height + position[1] > result_height)) {
      // Out Of Range
      return false;
    }
    for (int x = 0; x < block.width; x++) {
      for (int y = 0; y < block.height; y++) {
        int x_pos = position[0] + x;
        int y_pos = position[1] + y;
        if (result[x_pos][y_pos] != block.index) {
          // One of cell has already been filled
          return false;
        }
      }
    }
    return true;
  }

  // Check if good place to allocate is there?
  private boolean checkAllocatable(Block block, int[] position) {
    if ((block.width + position[0] > result_width) || (block.height + position[1] > result_height)) {
      // Out Of Range
      return false;
    }
    for (int x = 0; x < block.width; x++) {
      for (int y = 0; y < block.height; y++) {
        int x_pos = position[0] + x;
        int y_pos = position[1] + y;
        if (result[x_pos][y_pos] != -1) {
          // One of cell has already been filled
          return false;
        }
      }
    }
    return true;
  }

  // Get the closest empty position
  private int[] getClosestEmpty() {
    for (int x = 0; x < result_width; x++) {
      for (int y = 0; y < result_height; y++) {
        if (result[x][y] == -1) {
          int[] res = {x, y};
          return res;
        }
      }
    }
    return null;
  }

  /**********************************************
   * == result == 211 554 554 553
   */
  private void setTestData(int type) {
    if (type == 1) {
      blocks.add(new Block(1, 2, blockNumber++));
      blocks.add(new Block(1, 1, blockNumber++));
      blocks.add(new Block(1, 1, blockNumber++));
      blocks.add(new Block(2, 1, blockNumber++));
      blocks.add(new Block(3, 2, blockNumber++));
    } else if (type == 2) {
      blocks.add(new Block(2, 1, blockNumber++));
      blocks.add(new Block(3, 2, blockNumber++));
      blocks.add(new Block(3, 1, blockNumber++));
      blocks.add(new Block(4, 1, blockNumber++));
      blocks.add(new Block(1, 2, blockNumber++));
      blocks.add(new Block(2, 1, blockNumber++));
      blocks.add(new Block(2, 2, blockNumber++));
      blocks.add(new Block(1, 1, blockNumber++));
    } else if (type == 3) {
      blocks.add(new Block(5, 5, blockNumber++));
      blocks.add(new Block(8, 8, blockNumber++));
    }
  }
}
