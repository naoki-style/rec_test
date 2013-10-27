package cyclicList;

import java.util.ArrayList;
import java.util.List;

/*
 * Assumption
 *  1. Not serial numbers
 *  2. n(L) is max(N(X))
 *  3. Result shall be pairs of "combination"(printList(0))
 *      if full lists, then printList(1)
 */
public class CyclicList {
  private int L = 0;
  private int nL = 0;
  private List<int[]> lists;

  /**
   * @param args
   */
  public static void main(String[] args) {
    CyclicList list = new CyclicList(4);
    boolean res = list.findList();
    if (res) {
      list.printList(1);
    } else {
      System.out.println("Not found.");
    }
  }
  
  CyclicList(int count) {
    L = count;
    nL = L * (L - 1) + 1;
    lists = new ArrayList<int[]>();
  }
  
  /*
   * Entry point of this question
   */
  private boolean findList() {
    boolean res = true;
    lists = getNumberCombination(nL, L, nL);
    if (lists.isEmpty()) {
      res = false;
    }
    if (lists.size() < 3) {
      // Impossible to be "2nd < last". 
      return false;
    }
    return res;
  }
  
  /*
   * Check and list up numbers from larger one recursively
   */
  private ArrayList<int[]> getNumberCombination(int limit, int count, int sum) {
    ArrayList<int[]> combi = new ArrayList<int[]>();
    if (count == 1 && sum == 1 && limit > 1) {
      int [] res = new int[1];
      res[0] = sum;
      combi.add(res);
      return combi;
    } else if (count == 1 && sum > 1) {
      return null;
    }
    for (int i = limit - 1; i > 1; i--) {
      if (i <= sum && i < limit) {
        ArrayList<int[]> candidate = getNumberCombination(i, count - 1, sum - i);
        if (candidate != null) {
          addNumber(i, candidate, combi);
        }
      }
    }
    return combi;
  }
  
  /*
   * Add picked number to the result list
   */
  private void addNumber(int number, List<int[]> src, List<int[]> dst) {
    for (int i = 0; i < src.size(); i++) {
      int[] temp = src.get(i);
      int[] newlist = new int[temp.length + 1];
      newlist[0] = number;
      for (int j = 1; j < temp.length + 1; j++) {
        newlist[j] = temp[j -1];
      }
      dst.add(newlist);
    }
  }
  
  /*
   * Print list
   *  type 0 : show the combination of numbers
   *  type 1 : show all the sequences of numbers
   */
  private void printList(int type) {
    if (type == 0) { // list all the combinations
      for (int i = 0; i < lists.size(); i++) {
        int[] data = lists.get(i);
        for (int j = data.length - 1; j >= 0; j--) {
          System.out.print(data[j] + "->");
        }
        System.out.println();
      }
    } else if (type == 1) { // list all the sequences
      for (int i = 0; i < lists.size(); i++) {
        int[] data = lists.get(i);
        boolean[] used = new boolean[data.length + 1];
        for (int j = 0; j < lists.size(); j++) {
          used[j] = false; 
        }
        // 1st item must be "1"
        int[] temp = new int[data.length];
        temp[0] = data[data.length - 1];
        used[data.length - 1] = true;
        makeAllThePermutationAndPrint(1, data, temp, used);
      }
    }
  }
  
  /*
   * Generate all the sequences for a combination
   */
  private void makeAllThePermutationAndPrint(int length, int[] original, int[] create, boolean[] used) {
    if (length == create.length) {
      //  The last item must be larger than the 2nd item
      if (create[1] >= create[create.length - 1]) {
        return;
      }
      for (int i : create) {
        System.out.print(i + "->");
      }
      System.out.println();
    } else {
      for (int i = 0; i < create.length - 1; i++) {
        if (!used[i]) {
          create[length] = original[i];
          used[i] = true;
          makeAllThePermutationAndPrint(length + 1, original, create, used);
          used[i] = false;
        }
      }
    }
  }

}
