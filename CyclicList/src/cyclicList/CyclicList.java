package cyclicList;

import java.util.ArrayList;
import java.util.List;

public class CyclicList {
  private int L = 0;
  private int nL = 0;
  private List<int[]> lists;

  /**
   * @param args
   */
  public static void main(String[] args) {
    CyclicList list = new CyclicList(5);
    List<int[]> result = list.getNumberCombination(list.nL, list.L, list.nL);
    for (int i = 0; i < result.size(); i++) {
      list.printList(result.get(i));
    }
  }
  
  CyclicList(int count) {
    L = count;
    nL = L * (L - 1) + 1;
    lists = new ArrayList<int[]>();
  }
  
  private ArrayList<int[]> getNumberCombination(int limit, int count, int sum) {
    ArrayList<int[]> combi = new ArrayList<int[]>();
    if (count == 1 && sum == 1 && limit > 1) {
      int [] res = new int[1];
      res[0] = sum;
      combi.add(res);
      return combi;
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
  
  private void printList(int[] data) {
    for (int i = data.length - 1; i >= 0; i--) {
      System.out.print(data[i] + "->");
    }
    System.out.println();
  }

}
