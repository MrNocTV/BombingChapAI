package com.mygdx.game.bomberman;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;


public class WeirdBot extends SmartBot{
	public WeirdBot(GameScreen game) {
		super(game);
		name = "LS";
	}
	
	protected class Cell {
		int i;
		int j;
		int val;
		int possibleDirections;
		WeirdBot.Cell parent;
		public Cell(int i, int j) {
			this.i = i;
			this.j = j;
			parent = null;
		}
		
		@Override
		public String toString() {
			return "["+this.i+","+this.j+"]";
		}
	}
	protected WeirdBot.Cell[][] grid;
	protected boolean[][] closed;
	protected Stack<WeirdBot.Cell> backtrackNodes;
	protected PriorityQueue<WeirdBot.Cell> neighbors;

	
	private int manhattanDistance(int x1, int y1, int x2, int y2) {
		int dx = (int)Math.abs(x1-x2);
		int dy = (int)Math.abs(y1-y2);
		return dx+dy;
	}
	
	private int calculatePossibleMovesAtNode(int x, int y) {
		int countMove = 0;
		//if valid, not block, not visited
		if(x-1 >= 0 && grid[x-1][y] != null && !closed[x-1][y]) { //up
			++countMove;
		}
		if(x+1 < grid.length && grid[x+1][y] != null && !closed[x+1][y]) {//down
			++countMove;
		}
		if(y-1 >= 0 && grid[x][y-1] != null && !closed[x][y-1]) {//left
			++countMove;
		}
		if(y+1 < grid[0].length && grid[x][y+1] != null && !closed[x][y+1]) { //right
			++countMove;
		}
		return countMove;
	}
	
	@Override
	public void findPath(int x, int y, int[][] blockedMap) {
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
	
		grid = new WeirdBot.Cell[x][y];
		closed = new boolean[x][y];
		backtrackNodes = new Stack<WeirdBot.Cell>();
		neighbors = new PriorityQueue<WeirdBot.Cell>(new Comparator<WeirdBot.Cell>() {
	     	   public int compare(WeirdBot.Cell c1, WeirdBot.Cell c2) {
	    		   return c1.val<c2.val ? -1 : c1.val > c2.val ? 1 : 0;
	    	   }
	       });
		
		for(int i = 0; i < x; ++i)
			for(int j = 0; j < grid[i].length; ++j){
				grid[i][j] = new WeirdBot.Cell(i,j);
				//calculate the manhattan value at each node
				grid[i][j].val = manhattanDistance(i, j, targetX,targetY) + manhattanDistance(startX, startY, i, j);  //g+h
			}
		
		for(int i=0;i<blockedMap.length;++i){
			grid[blockedMap[i][0]][blockedMap[i][1]] = null;
        }
		try {
			locSearch();
		}catch(Exception ex) {
			//System.out.println(ex.getMessage());
		}
		
		 try {
			// System.out.println(closed[targetX][targetY]);
			 if(closed[targetX][targetY]){
				 System.out.println(grid[startX][startY].parent);
				//Trace back the path 
	                //System.out.print("Path: ");
	                Cell current = grid[targetX][targetY];
	              //  System.out.print(current);
	                while(current.parent!=null){
	                    if(current.parent.parent == null) {
	                    	huntX = 9-current.i;
	                    	huntY = current.j;
	                    	
	                    	//System.out.println("h:"+huntY + " , " +huntX + " - " + startY + ","+(9-startX) +" = " + (huntY-startY) + ","+(huntX-(9-startX)));
	                    }
	                    current = current.parent;
	                    
	                } 
	                foundPath = true;
		         
		       }//else System.out.println("No possible path");
		       
		       }catch(Exception ex) {
		    	   //System.out.println(ex.getMessage()+ " after");
		       }
		
	}
	
	private void resetNeighbors() { 
		while(!neighbors.isEmpty())
			neighbors.poll();
	}
	
	private void checkAndUpdateParent(WeirdBot.Cell current, WeirdBot.Cell t){
            t.parent = current;
            neighbors.add(t);
        
    }
	
	private void locSearch() {
        
		WeirdBot.Cell current = grid[startX][startY];
        closed[current.i][current.j]=true; 
        while(true){ 

            if(current.equals(grid[targetX][targetY])){
                return; 
            } 
         
            WeirdBot.Cell t;  
            if(current.i-1>=0 && grid[current.i-1][current.j] != null && !closed[current.i-1][current.j]){ //up
                t = grid[current.i-1][current.j];
                checkAndUpdateParent(current, t); 

            } 

            if(current.j-1>=0 && grid[current.i][current.j-1] != null && !closed[current.i][current.j-1]){ //left
                t = grid[current.i][current.j-1];
                checkAndUpdateParent(current, t); 
            }

            if(current.j+1<grid[0].length && grid[current.i][current.j+1]!=null&&!closed[current.i][current.j+1]){ //right
                t = grid[current.i][current.j+1];
                checkAndUpdateParent(current, t);  
            }

            if(current.i+1<grid.length&&grid[current.i+1][current.j]!=null&&!closed[current.i+1][current.j]){ //down
                t = grid[current.i+1][current.j];
                checkAndUpdateParent(current, t); 
            }
            
            if(neighbors.isEmpty()) {
            	if(backtrackNodes.isEmpty()) {
            		return;
            	} else {
            		current = backtrackNodes.pop();
            	}
            } else {
            	current = neighbors.poll();
            	closed[current.i][current.j] = true;
            	if(calculatePossibleMovesAtNode(current.parent.i, current.parent.j) >= 1) 
            		backtrackNodes.add(current.parent);
            }
            resetNeighbors();
        } 
	}
	
}
