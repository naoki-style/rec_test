package sudoku;

public class Sudoku {
  private int[][] board;

  public Sudoku(int[][] board) { // コンストラクタ
    this.board = board;
  }

  public void print() { // 9x9のボードを表示
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        System.out.print(board[i][j] + " ");
      }
      System.out.println();
    }
  }

  private int[] getNum(int x, int y) { // (x,y)位置の候補の計算
    int[] res = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for (int i = 0; i < 9; i++) {
      res[board[i][y]]++; // 横
      res[board[x][i]]++; // 縦
      res[board[x / 3 * 3 + i / 3][y / 3 * 3 + i % 3]]++; // 3x3ボックス
    }
    return res; // res[i] : iが縦、横、3x3ボックスで出現した個数
  }

  public boolean setSolution(int n) { // ボードの場所n以下に解を設定。OKかどうかを返す。
    int x = n / 9, y = n % 9; // nの位置のx,yを求める
    if (n >= 81)
      return true; // nがボードの枠を超えれば無条件でＯＫ
    if (board[x][y] != 0)
      return setSolution(n + 1); // x,yに解が入っていれば次に行く。
    int[] nums = getNum(x, y); // x,y位置の候補を求める
    for (int i = 1; i <= 9; i++) {
      if (nums[i] == 0) { // iが候補なら
        board[x][y] = i; // x,yにiを解として設定し
        if (setSolution(n + 1))
          return true; // 次に行く。ダメなら次の候補を調べる。
      }
    }
    board[x][y] = 0;
    return false; // 全ての候補でダメならfalseを返す。
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    int[][] board = test_1;
    Sudoku sdk = new Sudoku(board);
    if (sdk.setSolution(0)) {
      sdk.print(); // 結果の表示
    } else {
      System.out.println("解は見つかりません");
    }
  }
  
  private static final int[][] test_1 =
    { {0, 0, 7, 0, 0, 0, 4, 0, 0}, {3, 0, 6, 0, 2, 0, 1, 0, 8}, {0, 2, 0, 5, 0, 9, 0, 3, 0},
      {5, 0, 0, 0, 4, 0, 0, 0, 9}, {0, 9, 0, 0, 8, 0, 0, 5, 0}, {6, 0, 0, 0, 7, 0, 0, 0, 3},
      {0, 8, 0, 7, 0, 2, 0, 4, 0}, {4, 0, 9, 0, 6, 0, 5, 0, 2}, {0, 0, 3, 0, 0, 0, 9, 0, 0}};
  private static final int[][] test_2 =
    { {0, 0, 8, 0, 0, 5, 9, 0, 0}, {2, 0, 0, 0, 3, 0, 0, 4, 0}, {1, 9, 0, 0, 0, 7, 0, 0, 5},
      {0, 0, 0, 8, 0, 0, 5, 0, 0}, {0, 4, 0, 3, 9, 6, 0, 7, 0}, {0, 0, 3, 0, 0, 2, 0, 0, 0},
      {4, 0, 0, 5, 0, 0, 0, 1, 9}, {0, 8, 0, 0, 1, 0, 0, 0, 6}, {0, 0, 5, 7, 0, 0, 2, 0, 0}};
  
}
