package com.mygdx.game.bomberman;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.mygdx.game.bomberman.SmartBot.Cell;

public class PrettySmartBot extends SmartBot{
	public PrettySmartBot(GameScreen game) {
		super(game);
		name = "FF";
	}
	
	protected class Cell{  
        int i, j;
        Cell parent; 
        int val;
        
        Cell(int i, int j){
            this.i = i;
            this.j = j; 
        }
        
        @Override
        public String toString(){
            //return "["+this.j+", "+(9-this.i)+"]";
        	return "["+this.i+", "+this.j+"]";
        }
    }
	protected Cell [][] grid = new Cell[10][15];
	private PriorityQueue<Cell> open;
	private PriorityQueue<Cell> neighbors;
	@Override
	public void setBlocked(int i, int j){
        grid[i][j] = null;
    }
    
	@Override
    public void setStartCell(int i, int j){
        startX = i;
        startY = j;
    }
    
	@Override
    public void setEndCell(int i, int j){
        targetX = i;
        targetY = j; 
    }
    
	private void floodFill() {
		open.add(grid[startX][startY]);
        closed[startX][startY] = true;
        Cell current;
        foundPath = false;
        while(true) {
        	
        	do {
        		current = open.poll();
        		
        		if(current.equals(grid[targetX][targetY]))  {
        			foundPath = true;
        			return;
        		}

        		  Cell t;  
                  if(current.i-1>=0  && grid[current.i-1][current.j] != null){ //up
                	 
                      t = grid[current.i-1][current.j];
                      if(!closed[t.i][t.j])
                    	  checkAndUpdateValue(current, t, current.val);
                  } 

                  if(current.j-1>=0  && grid[current.i][current.j-1] != null){ //left
                	
                      t = grid[current.i][current.j-1];
                      if(!closed[t.i][t.j])
                    	  checkAndUpdateValue(current, t, current.val);
                  }

                  if(current.j+1<grid[0].length  && grid[current.i][current.j+1] != null){ //right
                	 
                      t = grid[current.i][current.j+1];
               
                      if(!closed[t.i][t.j])
                    	  checkAndUpdateValue(current, t, current.val);
                      
                  }

                  if(current.i+1<grid.length && grid[current.i+1][current.j] != null){ //down
           
                      t = grid[current.i+1][current.j];
                      if(!closed[t.i][t.j])
                    	  checkAndUpdateValue(current, t, current.val);
                  }
        	}while(!open.isEmpty());
        	
        	
        	if(neighbors.isEmpty()){
       
        		return;
        	}
        	while(!neighbors.isEmpty()) 
        		open.add(neighbors.poll());
        }
	}
	
	private void checkAndUpdateValue(Cell current, Cell t, int val){
		
        t.parent = current;
        t.val = val+1;
        closed[t.i][t.j] = true;
        neighbors.add(t);
    }
	
	@Override
	protected void findPath( int x, int y, int[][] blocked){
		foundPath = false;
		startX = (int)((position.y+32)/64);
		startY = (int)((position.x+32)/64);
		startX = startX == 0 ? 1 : startX;
		startY = startY == 0 ? 1 : startY;
		startX = 9 - startX;
		targetX = (int)((game.manPosition.y+32)/64);
		targetY = (int)((game.manPosition.x+32)/64);
		targetX = targetX == 0 ? 1 : targetX;
		targetY = targetY == 0 ? 1 : targetY;
		targetX = 9 - targetX;
	
		
        //Reset
       grid = new Cell[x][y];
       closed = new boolean[x][y];
       open = new PriorityQueue<Cell>(new Comparator<Cell>() {
    	   public int compare(Cell c1, Cell c2) {
    		   return c1.val<c2.val ? -1 : c1.val > c2.val ? 1 : 0;
    	   }
       });
       neighbors = new PriorityQueue<PrettySmartBot.Cell>(new Comparator<Cell>() {
    	   public int compare(Cell c1, Cell c2) {
    		   return c1.val<c2.val ? -1 : c1.val > c2.val ? 1 : 0;
    	   }
       });
       //Set start position
       setStartCell(startX, startY);  //Setting to 0,0 by default. Will be useful for the UI part
       
       //Set End Location
       setEndCell(targetX, targetY); 
       
       for(int i=0;i<x;++i){
          for(int j=0;j<y;++j){
              grid[i][j] = new Cell(i, j);
              grid[i][j].val = 0;
//              System.out.print(grid[i][j].heuristicCost+" ");
          }
//          System.out.println();
       }
       grid[startX][startY].val = 0;
       
       /*
         Set blocked cells. Simply set the cell values to null
         for blocked cells.
       */
       for(int i=0;i<blocked.length;++i){
           setBlocked(blocked[i][0], blocked[i][1]);
       }
       try {
    	   floodFill(); 
       }catch(Exception ex) {
    	   
    	   return;
       }
       
       try {
       if(closed[targetX][targetY]){
           //Trace back the path 
            //System.out.print("Path: ");
            Cell current = grid[targetX][targetY];
            //System.out.print(current);
            while(current.parent!=null){
                current = current.parent;
                if(current.parent.parent == null) {
                	huntX = 9-current.i;
                	huntY = current.j;
                	
                	//System.out.println("h:"+huntY + " , " +huntX + " - " + startY + ","+(9-startX) +" = " + (huntY-startY) + ","+(huntX-(9-startX)));
                }
            } 
            foundPath = true;
            //System.out.println();
       }//else System.out.println("No possible path");
       }catch(Exception ex) {
    	   
       }
	}
	
}
