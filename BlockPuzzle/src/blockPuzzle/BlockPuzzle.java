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

  private int[][] result; // 0 : empty, 1~N(n) : filled by item "n"
  private int result_height = 0, result_width = 0;

  public class Block {
    public int width;
    public int height;
    public int index;
    public int[] edge = new int[2];
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
    public void resetStatus() {
      isUsed = UnUsed;
      edge[0] = 0;
      edge[1] = 0;
    }
  };

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
    if (bp1.runPuzzle()) {
      System.out.println("success");
    } else {
      System.out.println("fail");
    }
    if (debug) {
      bp1.printResult();
      bp1.outputAsHtml();
    }
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
  }

  BlockPuzzle(int h, int w, Queue<Block> b) {
    result_height = h;
    result_width = w;
    blocks = b;
    result = new int[result_width][result_height];
  }

  BlockPuzzle(int h, int w) {
    result_height = h;
    result_width = w;
    result = new int[result_width][result_height];
  }
  
  private void addBlock(Block b) {
    blocks.add(b);
  }

  private Block removeBlock() {
    if (blocks.isEmpty()) {
      return null;
    }
    Block block = blocks.remove();
    return block;
  }

  private Block peekBlock() {
    return blocks.peek();
  }

  private int countBlock() {
    return blocks.size();
  }

  public void initializeResultArea() {
    for (int y = 0; y < result_height; y++) {
      for (int x = 0; x < result_width; x++) {
        result[x][y] = -1;
      }
    }
  }
  
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
        if (allocateItem(block, pos)) {
          block.isUsed = Block.Used;
          if(runPuzzle()) {
            addBlock(block);
            return true;
          } else {
            removeItem(block, pos);
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
  
  private void printResult() {
    for (int y = 0; y < result_height; y++) {
      for (int x = 0; x < result_width; x++) {
        System.out.print(result[x][y]);
      }
      System.out.println();
    }
  }
  
  private void outputAsHtml() {
    StringBuilder builder = new StringBuilder();
    int cell_size = 100;
    builder.append("<!DOCTYPE html>");
    builder.append("<html lang=\"en\">");
    builder.append("<head><title>blockPuzzle</title></head>");
    builder.append("<body><h1>The result of test block</h1></body>");
    builder.append("<canvas id=\"result\" width=\" " + (result_width * 100 + 20) + "\" height=\" " + (result_height * 100 + 20) + "\">");
    builder.append("<p>This html uses canvas style</p>");
    builder.append("</canvas>");
    builder.append("<script>");
    builder.append("var start = 10;");
    builder.append("var size = " + cell_size + ";");
    builder.append("var canvas = document.getElementById('result');");
    builder.append("var context = canvas.getContext('2d');");
    while (true) {
      Block block = removeBlock();
      if (block == null) {
        break;
      }
      builder.append("context.beginPath();");
      int x_pos = cell_size * block.edge[0];
      int y_pos = cell_size * block.edge[1];
      int b_width = cell_size * block.width;
      int b_height = cell_size * block.height;
      builder.append("context.strokeRect(start + " + x_pos + ", start + " + y_pos + ", " + b_width + ", " + b_height + ");");
      builder.append("context.font = \"20px 'Arial'\";");
      builder.append("context.strokeText('" + block.width + "x" + block.height + "', " + (x_pos + b_width / 10) + ", " + (y_pos + b_height) + ");");
      builder.append("context.closePath();");
    }
    builder.append("</script>");
    builder.append("</html>");
    String html = builder.toString();
    File file = new File("./src/blockPuzzle/result.html");
    try {
      FileWriter filewriter = new FileWriter(file);
      filewriter.write(html);
      filewriter.close();
    } catch (IOException e) {
      System.out.println("Something went wrong while writing.");
    }
  }

  private boolean allocateItem(Block block, int[] position) {
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
  
  private boolean removeItem(Block block, int[] position) {
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
  
  /*
   * == result ==
   *     211
   *     554
   *     554
   *     553
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
    }
  }
}
