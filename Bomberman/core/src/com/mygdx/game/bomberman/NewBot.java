package com.mygdx.game.bomberman;

import java.util.Comparator;
import java.util.PriorityQueue;


public class NewBot extends SmartBot{
	public NewBot(GameScreen smartBot) {
		super(smartBot);
		name = "GB";
		position.x = 64*9;
		position.y = 64*2;
	}
	
	private int manhattanDistance(int x1, int y1, int x2, int y2) {
		int dx = (int)Math.abs(x1-x2);
		int dy = (int)Math.abs(y1-y2);
		return dx+dy;
	}
	
	protected class Cell {
		int i;
		int j;
		int val;
		NewBot.Cell parent;
		public Cell(int i, int j) {
			this.i = i;
			this.j = j;
			val = 0;
			parent = null;
		}
		
		@Override
		public String toString() {
			return "["+this.i+","+this.j+"]";
		}
	}
	
	protected NewBot.Cell[][] grid;
	protected PriorityQueue<NewBot.Cell> open;
	
	public void findPath(int x,int y, int[][] blockedMap) {
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
		grid = new Cell[x][y];
		closed = new boolean[x][y];
		open = new PriorityQueue<NewBot.Cell>(new Comparator<NewBot.Cell>() {
			@Override
			public int compare(NewBot.Cell c1, NewBot.Cell c2) {
				return c1.val<c2.val ? -1 : c1.val > c2.val ? 1 : 0;
			}
		});
		for(int i = 0; i < x; ++i) 
			for(int j = 0; j < y; ++j) {
				grid[i][j] = new Cell(i,j);
				grid[i][j].val = manhattanDistance(i, j, targetX, targetY);
			}
		for(int i = 0; i < blockedMap.length; ++i) {
			grid[blockedMap[i][0]][blockedMap[i][1]] = null;
		}
		try {
			greedyBFS();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		try {
			System.out.println(closed[targetX][targetY]);
			if(closed[targetX][targetY]) {
				Cell current = grid[targetX][targetY];
				System.out.print("Path:"+current);
				
				while(current.parent != null) { 
					if(current.parent.parent == null) {
	                	huntX = 9-current.i;
	                	huntY = current.j;
                	}
					current = current.parent;
				}
				foundPath = true;
		}
		}catch(Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private void checkAndUpdate(Cell current, Cell t) {
		if(!closed[t.i][t.j]) {
      	  t.parent = current;
      	  open.add(t);
        }
	}
	
	private void greedyBFS() {
		Cell current;
		open.add(grid[startX][startY]);
		closed[startX][startY] = true;
		int countLoop = 0;
		while(true) {
			++countLoop;
			current = open.poll();
			closed[current.i][current.j] = true;
			if(current.equals(grid[targetX][targetY])) {
				foundPath = true;
				return;
			}
			if(current == null) return;
				
			  Cell t;  
              if(current.i-1>=0  && grid[current.i-1][current.j] != null){ //up
                  t = grid[current.i-1][current.j];
                  checkAndUpdate(current, t);
              } 

              if(current.j-1>=0  && grid[current.i][current.j-1] != null){ //left
                  t = grid[current.i][current.j-1];
                  checkAndUpdate(current, t);
              }

              if(current.j+1<grid[0].length  && grid[current.i][current.j+1] != null){ //right
                  t = grid[current.i][current.j+1];
                  checkAndUpdate(current, t);
                  
              }

              if(current.i+1<grid.length && grid[current.i+1][current.j] != null){ //down
                  t = grid[current.i+1][current.j];
                  checkAndUpdate(current, t);
              }
              if(countLoop >= 200) return; //avoid infinite loop
		}
	}
	
}
