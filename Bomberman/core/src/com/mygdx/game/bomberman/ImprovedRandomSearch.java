package com.mygdx.game.bomberman;

import java.util.LinkedList;

import com.badlogic.gdx.math.MathUtils;

public class ImprovedRandomSearch {
	protected class Cell {
		int i;
		int j;
		ImprovedRandomSearch.Cell parent;
		public Cell(int i, int j) {
			this.i = i;
			this.j = j;
			parent = null;
		}
		
		public String toString() {
			return "["+this.i+","+this.j+"]";
		}
	}
	
	protected ImprovedRandomSearch.Cell[][] grid;
	protected boolean[][] closed;
	protected LinkedList<ImprovedRandomSearch.Cell> backtrackNodes;
	private LinkedList<ImprovedRandomSearch.Cell> neighbors;
	protected int startX;
	protected int startY;
	protected int targetX;
	protected int targetY;
	
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
	
	public void findPath(int x, int y, int[][] blockedMap) {
		startX = 0;
		startY = 0;
		targetX = 5;
		targetY = 5;
		grid = new ImprovedRandomSearch.Cell[x][y];
		closed = new boolean[x][y];
		backtrackNodes = new LinkedList<ImprovedRandomSearch.Cell>();
		neighbors = new LinkedList<ImprovedRandomSearch.Cell>();
		for(int i = 0; i < x; ++i) {
			for(int j = 0; j < y; ++j) 
				grid[i][j] = new ImprovedRandomSearch.Cell(i, j);
		}
		for(int i = 0; i < blockedMap.length; ++i)
			grid[blockedMap[i][0]][blockedMap[i][1]] = null;
		
		try {
			improvedRandomWalk();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		 try {
			 System.out.println(closed[targetX][targetY]);
			 if(closed[targetX][targetY]){
				 System.out.println(grid[startX][startY].parent);
				//Trace back the path 
	                System.out.print("Path: ");
	                ImprovedRandomSearch.Cell current = grid[targetX][targetY];
	                System.out.print(current);
	                while(current.parent!=null){
	                    
	                    current = current.parent;
	                    System.out.print("->"+current);
	                } 
	                
		         
		       }else System.out.println("No possible path");
		       
		       }catch(Exception ex) {
		    	   System.out.println(ex.getMessage()+ " after");
		       }
	}
	
	private void updateParent(ImprovedRandomSearch.Cell current, ImprovedRandomSearch.Cell t) {
		t.parent = current;
		neighbors.add(t);
	}
	
	private void resetNeighbors() {
		while (neighbors.isEmpty()) {
			neighbors.remove();
		}
	}
	
	private void improvedRandomWalk() {
		ImprovedRandomSearch.Cell current = grid[startX][startY];
		closed[current.i][current.j] = true;
		while(true) {
			if(current.equals(grid[targetX][targetY])) {
				return;
			}
			
			ImprovedRandomSearch.Cell t;
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
	
	public static void main(String[] args) {
		ImprovedRandomSearch f = new ImprovedRandomSearch();
		f.findPath(6, 6, new int[][]{{0,2},{1,2},{4,2},{5,2}});
	}
}
