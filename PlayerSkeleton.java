import java.lang.*;

public class PlayerSkeleton {

  public static final int COLS = 10;
  public static final int ROWS = 21;

  //indices for legalMoves
  public static final int ORIENT = 0;
  public static final int SLOT = 1;

  protected static int[] pOrients;
  protected static int[][] pWidth;
  private static int[][] pHeight;
  private static int[][][] pBottom;
  private static int[][][] pTop;
  

  //implement this function to have a working system
  public int pickMove(State s, int[][] legalMoves) {
    int bestMove = 0;
    float maxHeuristic = -9999;
    int turn = s.getTurnNumber();
    int nextPiece = s.getNextPiece();
    
    for (int i = 0; i < legalMoves.length; i++){
      int[][] field = new int[ROWS][COLS];
      int[] top = new int[COLS];
      copyField(field, s.getField());
      copyTop(top, s.getTop());
    
      float heuristicMove = -9999;
      
      if (makeMove(field, top, turn, nextPiece, legalMoves[i][ORIENT], legalMoves[i][SLOT])) {
        heuristicMove = getHeuristic(field, top);
      }
      
    
      // it would be great if we could make a copy of state
      // int[][] newState = s.testMove(legalMoves[i][ORIENT], legalMoves[i][SLOT]);
      // float heuristicMove = getHeuristic(newState);
        
      if (heuristicMove > maxHeuristic){
        //System.out.println("Success");
        bestMove = i;
        maxHeuristic = heuristicMove;  
      }       
      // System.out.println(i + ", " + heuristicMove);
    }
    
    // System.out.println(bestMove);
    return bestMove;
  }
                                 
  public float getHeuristic(int[][] field, int[] top){
      float heuristic = 0;
      int maxHeight = 0;
      int totalHeight = 0;
      int bumpiness = getBumpiness(top);
      int holes = getHoles(field, top);
      
      for (int i = 0; i < top.length; i++) {
         if (top[i] > maxHeight){
           maxHeight = top[i];  
         }
         totalHeight += top[i]; 
      }
      heuristic -= (float)totalHeight;
      heuristic -= (float)maxHeight;
      heuristic -= (float)bumpiness;
      heuristic -= (float)holes;
      
      // System.out.println(holes);
      return heuristic;  
  }
  
  // also counts blocks above holes
  public int getHoles(int[][] field, int[] top){
    int holes = 0;
    int holeMultiplier = 1; // holes on top of holes are really bad
    int holeDepth = 1; // total number of blocks above holes
    
    for (int j = 0; j < field[0].length; j++){
      for (int i = top[j]-2; i >= 0; i--){
        if (field[i][j] == 0){
          holes += holeMultiplier;
          holeMultiplier++;  
          continue;
        }
        holeDepth++;  
      }
      holeMultiplier = 1;  
    } 
    // System.out.println(holes); 
    return holes + holeDepth;
  }
  
  public int getBumpiness(int[] top){
    int total = 0;
    for (int i = 0; i < top.length - 1; i++){
      total += Math.abs(top[i] - top[i+1]);
    }  
    return total;
  }
  
  public boolean makeMove(int[][] field, int[] top, int turn, int nextPiece, int orient, int slot){
    //height if the first column makes contact
    int height = top[slot]-pBottom[nextPiece][orient][0];
    //for each column beyond the first in the piece
    for(int c = 1; c < pWidth[nextPiece][orient];c++) {
      height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
    }
    
    //check if game ended
    if(height+pHeight[nextPiece][orient] >= ROWS) {
      return false;
    }

    
    //for each column in the piece - fill in the appropriate blocks
    for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
      
      //from bottom to top of brick
      for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
        field[h][i+slot] = turn;
      }
    }
    
    //adjust top
    for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
      top[slot+c]=height+pTop[nextPiece][orient][c];
    }
    
    int rowsCleared = 0;
    
    //check for full rows - starting at the top
    for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
      //check all columns in the row
      boolean full = true;
      for(int c = 0; c < COLS; c++) {
        if(field[r][c] == 0) {
          full = false;
          break;
        }
      }
      //if the row was full - remove it and slide above stuff down
      if(full) {
        rowsCleared++;
        //for each column
        for(int c = 0; c < COLS; c++) {

          //slide down all bricks
          for(int i = r; i < top[c]; i++) {
            field[i][c] = field[i+1][c];
          }
          //lower the top
          top[c]--;
          while(top[c]>=1 && field[top[c]-1][c]==0) top[c]--;
        }
      }
    }
    
    return true;  
  }
  
  public void copyField(int[][] copy, int[][] field){
    for (int i = 0; i < field.length; i++){
      for (int j = 0; j < field[0].length; j++){
        copy[i][j] = field[i][j];  
      }  
    }
  }
  
  public void copyTop(int[] copy, int[] top){
    for (int i = 0; i < top.length; i++){
      copy[i] = top[i];  
    }  
  }
  
  public static void main(String[] args) {
    State s = new State();
    pOrients = s.getpOrients();
    pWidth = s.getpWidth();
    pBottom = s.getpBottom();
    pHeight = s.getpHeight();
    pTop = s.getpTop();
    
    new TFrame(s);
    PlayerSkeleton p = new PlayerSkeleton();
    while(!s.hasLost()) {
      s.makeMove(p.pickMove(s,s.legalMoves()));
      s.draw();
      s.drawNext(0,0);
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("You have completed "+s.getRowsCleared()+" rows.");
  }
  
}
