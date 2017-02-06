package com.mygdx.game.bomberman;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;


public class LocSearch {
	protected class Cell {
		int i;
		int j;
		int val;
		int possibleDirections;
		LocSearch.Cell parent;
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
	protected LocSearch.Cell[][] grid;
	protected boolean[][] closed;
	protected Stack<LocSearch.Cell> backtrackNodes;
	protected PriorityQueue<LocSearch.Cell> neighbors;
	protected int startX;
	protected int startY;
	protected int targetX;
	protected int targetY;
	
	private int manhattanDistance(int x, int y) {
		int dx = (int)Math.abs(x-targetX);
		int dy = (int)Math.abs(y-targetY);
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
	
	
	public void findPath(int x, int y, int[][] blockedMap) {
		startX = 0;
		startY = 0;
		targetX = 6;
		targetY = 6;
		grid = new LocSearch.Cell[x][y];
		closed = new boolean[x][y];
		backtrackNodes = new Stack<LocSearch.Cell>();
		neighbors = new PriorityQueue<LocSearch.Cell>(new Comparator<LocSearch.Cell>() {
			@Override
			public int compare(LocSearch.Cell c1, LocSearch.Cell c2) {
				return c1.val<c2.val ? -1 : c1.val > c2.val ? 1 : 0;
			}
		});
	
		
		for(int i = 0; i < x; ++i)
			for(int j = 0; j < grid[i].length; ++j){
				grid[i][j] = new LocSearch.Cell(i,j);
				//calculate the manhattan value at each node
				grid[i][j].val = manhattanDistance(i, j);
			}
		
		for(int i=0;i<blockedMap.length;++i){
			grid[blockedMap[i][0]][blockedMap[i][1]] = null;
        }
		try {
			locSearch();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		 try {
			 System.out.println(closed[targetX][targetY]);
			 if(closed[targetX][targetY]){
		           //Trace back the path 
		            System.out.print("Path: ");
		            Cell current = grid[targetX][targetY];
		            System.out.print(current);
		            while(current.parent!=null){
		                current = current.parent;
		                System.out.print("->" + current);
		            } 
		            //foundPath = true;
		            System.out.println();
		       }else System.out.println("No possible path");
		       
		       }catch(Exception ex) {
		    	   System.out.println(ex.getMessage()+ " after");
		       }
		
	}
	
	private void resetNeighbors() { 
		while(!neighbors.isEmpty())
			neighbors.poll();
	}
	
	private void checkAndUpdateParent(Cell current, Cell t){
            t.parent = current;
            neighbors.add(t);
        
    }
	
	private void locSearch() {
        
        Cell current = grid[startX][startY];
        closed[current.i][current.j]=true; 
        while(true){ 

            if(current.equals(grid[targetX][targetY])){
                return; 
            } 
         
            Cell t;  
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
            		current = backtrackNodes.pop(); //backtracking
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
	
	public static void main(String[] args) {
		LocSearch loc = new LocSearch();
		loc.findPath(7, 7, new int[][]{{2,1},{2,2},{2,3},{3,3},{4,3},{5,3},{5,4},{5,5},{3,6}});
	}
}
