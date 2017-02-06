package com.mygdx.game.bomberman;

import java.util.LinkedList;

import com.badlogic.gdx.math.MathUtils;

public class PrettyWeirdBot extends WeirdBot {
	public PrettyWeirdBot(GameScreen game) {
		super(game);
		name="RW";
	}
	
	protected class Cell {
		int i;
		int j;
		PrettyWeirdBot.Cell parent;
		public Cell(int i, int j) {
			this.i = i;
			this.j = j;
			parent = null;
		}
		
		public String toString() {
			return "["+this.i+","+this.j+"]";
		}
	}
	
	protected PrettyWeirdBot.Cell[][] grid;
	protected boolean[][] closed;
	protected LinkedList<PrettyWeirdBot.Cell> backtrackNodes;
	private LinkedList<PrettyWeirdBot.Cell> neighbors;
	
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
		grid = new PrettyWeirdBot.Cell[x][y];
		closed = new boolean[x][y];
		backtrackNodes = new LinkedList<PrettyWeirdBot.Cell>();
		neighbors = new LinkedList<PrettyWeirdBot.Cell>();
		for(int i = 0; i < x; ++i) {
			for(int j = 0; j < y; ++j) 
				grid[i][j] = new PrettyWeirdBot.Cell(i, j);
		}
		for(int i = 0; i < blockedMap.length; ++i)
			grid[blockedMap[i][0]][blockedMap[i][1]] = null;
		
		try {
			improvedRandomWalk();
		}catch(Exception ex) {
			//System.out.println(ex.getMessage());
		}
		
		 try {
			 
			 if(closed[targetX][targetY]){
	               //Trace back the path 
	                //System.out.print("Path: ");
	                PrettyWeirdBot.Cell current = grid[targetX][targetY];
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
	              //  System.out.println();
	           }//else System.out.println("No possible path");
		       
		       }catch(Exception ex) {
		    	  // System.out.println(ex.getMessage()+ " after");
		       }
	}
	
	private void updateParent(PrettyWeirdBot.Cell current, PrettyWeirdBot.Cell t) {
		t.parent = current;
		neighbors.add(t);
	}
	
	private void resetNeighbors() {
		while (neighbors.isEmpty()) {
			neighbors.remove();
		}
	}
	
	private void improvedRandomWalk() {
		PrettyWeirdBot.Cell current = grid[startX][startY];
		closed[current.i][current.j] = true;
		int countLoop = 0;
		while(true) {
			if(current.equals(grid[targetX][targetY])) {
				return;
			}
			++countLoop;
			if(countLoop >= 200) return;
			PrettyWeirdBot.Cell t;
			 if(current.i-1>=0 && grid[current.i-1][current.j] != null && !closed[current.i-1][current.j]){ //up
	                t = grid[current.i-1][current.j];
	                updateParent(current, t); 
			 } 

	            if(current.j-1>=0 && grid[current.i][current.j-1] != null && !closed[current.i][current.j-1]){ //left
	                t = grid[current.i][current.j-1];
	                updateParent(current, t); 
	            }

	            if(current.j+1<grid[0].length && grid[current.i][current.j+1]!=null&&!closed[current.i][current.j+1]){ //right
	                t = grid[current.i][current.j+1];
	                updateParent(current, t);  
	            }

	            if(current.i+1<grid.length&&grid[current.i+1][current.j]!=null&&!closed[current.i+1][current.j]){ //down
	                t = grid[current.i+1][current.j];
	                updateParent(current, t); 
	            }
	            
	            if(neighbors.isEmpty()) {
	            	if(backtrackNodes.isEmpty()) return;
	            	else{
	            		current = backtrackNodes.getLast();
	            	}
	            } else {
	            	current = neighbors.get((int)(MathUtils.random()*neighbors.size()));
	            	closed[current.i][current.j] = true;
	            	if(calculatePossibleMovesAtNode(current.parent.i, current.parent.j) >= 1)
	            		backtrackNodes.add(current.parent);
	            }
	            resetNeighbors();
		}
	}
}
