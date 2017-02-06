package com.mygdx.game.bomberman;

import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class SmartBot extends StupidBot {
	protected boolean foundPath;
	public static final int V_H_COST = 10;
	public SmartBot(GameScreen game) {
		super(game);

		name = "A*";
		//printMap(map);
		//floodFillFinding();
	}

	protected class Cell{  
        int heuristicCost = 0; //Heuristic cost
        int g;
        int finalCost = 0; //G+H
        int i, j;
        Cell parent; 
        
        Cell(int i, int j){
            this.i = i;
            this.j = j; 
        }
        
        @Override
        public String toString(){
            return "["+this.j+", "+(9-this.i)+"]";
        }
    }
    
    //Blocked cells are just null Cell values in grid
    protected Cell [][] grid = new Cell[10][15];
    
    private PriorityQueue<Cell> open;
     
    protected boolean closed[][];
    protected int startX, startY;
    protected int targetX, targetY;
            
    public void setBlocked(int i, int j){
        grid[i][j] = null;
    }
    
    public void setStartCell(int i, int j){
        startX = i;
        startY = j;
    }
    
    public void setEndCell(int i, int j){
        targetX = i;
        targetY = j; 
    }
    
    private void checkAndUpdateCost(Cell current, Cell t, int cost){
        if(t == null || closed[t.i][t.j])return;
        
        int t_final_cost = t.g+cost;
        System.out.println(current);
       
        if(closed[t.i][t.j]) {
        	if(t_final_cost + current.heuristicCost < t.g + t.heuristicCost) {
        		closed[t.i][t.j] = false;
        		t.g = t_final_cost; 
        		t.finalCost = t_final_cost + t.heuristicCost;
        		t.parent = current;
        		if(!open.contains(t))
        			open.add(t);
        		return;
        	}
        }
        
        boolean inOpen = open.contains(t);
        if(!inOpen || t_final_cost<t.finalCost){
        	t.g += cost;
            t.finalCost = t.heuristicCost + t.g;
            t.parent = current;
            if(!inOpen)open.add(t);
        }
    }
	
    private void aStar(){ 
        
        //add the start location to open list.
        open.add(grid[startX][startY]);
        
        Cell current;
        
        while(true){ 
            current = open.poll();
            if(current==null)break;
            closed[current.i][current.j]=true; 

            if(current.equals(grid[targetX][targetY])){
                return; 
            } 

            Cell t;  
            if(current.i-1>=0){ //up
                t = grid[current.i-1][current.j];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 

            } 

            if(current.j-1>=0){ //left
                t = grid[current.i][current.j-1];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 
            }

            if(current.j+1<grid[0].length){ //right
                t = grid[current.i][current.j+1];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 
            }

            if(current.i+1<grid.length){ //down
                t = grid[current.i+1][current.j];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 

          
            }
        } 
    }
    
    /*
    x, y = Board's dimensions
    si, sj = start location's x and y coordinates
    ei, ej = end location's x and y coordinates
    int[][] blocked = array containing inaccessible cell coordinates
    */
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
        		   return c1.finalCost<c2.finalCost ? -1 : c1.finalCost > c2.finalCost ? 1 : 0;
        	   }
           });
           //Set start position
           setStartCell(startX, startY);  //Setting to 0,0 by default. Will be useful for the UI part
           
           //Set End Location
           setEndCell(targetX, targetY); 
           
           for(int i=0;i<x;++i){
              for(int j=0;j<y;++j){
                  grid[i][j] = new Cell(i, j);
                  grid[i][j].heuristicCost = Math.abs(i-targetX)+Math.abs(j-targetY);
//                  System.out.print(grid[i][j].heuristicCost+" ");
              }
//              System.out.println();
           }
           grid[startX][startY].finalCost = 0;
           
           /*
             Set blocked cells. Simply set the cell values to null
             for blocked cells.
           */
           for(int i=0;i<blocked.length;++i){
               setBlocked(blocked[i][0], blocked[i][1]);
           }
           try {
        	   aStar(); 
           }catch(Exception ex) {
        
        	   return;
           }
           
            
           if(closed[targetX][targetY]){
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
              //  System.out.println();
           }//else System.out.println("No possible path");
    }
    
    protected int huntX;
    protected int huntY;
    protected boolean movingToNext = false;
    protected int nextX;
    protected int nextY;
 
    protected void hunt() {
    	int dx = huntY - startY;
    	int dy = huntX - (9-startX);
    	
    	nextX = startY;
    	nextY = 9-startX;

    	if(dx == -1 && dy == 0) { //left
    		direction = LEFT;
    		--nextX;
    	} else if(dx == 1 && dy == 0) { //right
    		direction = RIGHT;
    		++nextX;

    	} else if(dx == 0 && dy == 1) { //up
    		direction = UP;
    		++nextY;
    	
    	} else if(dx == 0 && dy == -1) { //down
    		direction = DOWN;
    		--nextY;
    		
    	}
    	
    	nextX*=64;
    	nextY*=64;
    //	System.out.println(nextX+","+nextY);
    	movingToNext = true;
    	
    }
    
    protected boolean isThereAFlame() {
		int x = (int)((position.x+32)/64)*64;
		int y = (int)((position.y+32)/64)*64;
		
		if(direction == UP) {
			if(game.isAFlame(x, y+64))
				return true;
			
		} else if (direction == DOWN) {
			if(game.isAFlame(x, y-64))
				return true;
			
		} else if(direction == LEFT) {
			if(game.isAFlame(x-64, y))
				return true;
			
		} else if(direction == RIGHT ) {
			if(game.isAFlame(x+64, y))
				return true;
			
		}
		
		return false;
	}
    
    protected void moveNext(float deltaTime) {
    	if(isThereAFlame()) movingToNext = false;
    	if(movingToNext) {
    	
    		switch(direction) {
    		case LEFT:
    			position.x -= (1+(float)speed/2);
    			if(position.x <= nextX) {
    				movingToNext = false;
    				position.x = nextX;
    			}
    			leftAnimTime += deltaTime;
    			break;
    		case RIGHT:
    			position.x += 1+(float)speed/2;
    			if(position.x >= nextX) {
    				movingToNext = false;
    				position.x = nextX;
    			}
    			
    			rightAnimTime += deltaTime;
    			break;
    		case UP:
    			position.y += 1+(float)speed/2;
    			if(position.y >= nextY) {
    				movingToNext = false;
    				position.y = nextY;
    			}
    			backAnimTime += deltaTime;
    			break;
    		case DOWN:
    			position.y -= (1+(float)speed/2);
    			if(position.y <= nextY) {
    				movingToNext = false;
    				position.y = nextY;
    				
    			}
    			frontAnimTime += deltaTime;
    			break;
    		}
    
    		botRect.set(position.x+6, position.y+6, 51, 51);
    	}
    }
    
    @Override
    protected void move(float deltaTime) {
		//check collision with walls
		botRect.set(position.x+6, position.y+6, 51, 51);
		for(Point wall : game.walls) {
			obstacleRect.set(wall.x, wall.y,64,64);
			if(botRect.overlaps(obstacleRect)) {
				
				checkCollisionWithWallsAndBricksAndBombs();
			}
		}
		//check collision with bricks
		for(int count = 0; count < game.bricks.size; ++count) {
			obstacleRect.set(game.bricks.get(count).x, game.bricks.get(count).y, 64,64);
			if(botRect.overlaps(obstacleRect)) {
				checkCollisionWithWallsAndBricksAndBombs();
			}
		}
		if(!isMoving) {
			checkCollisionWithWallsAndBricksAndBombs();
		}
		
	
		if(isMoving)
			switch(direction) {
			case UP:
				position.y += 1.5+(float)speed/2;
				backAnimTime+=deltaTime;
				break;
			case DOWN:
				position.y -= (1.5+(float)speed/2);
				frontAnimTime += deltaTime;
				break;
			case LEFT:
				position.x -= (1.5+(float)speed/2);
				leftAnimTime += deltaTime;
				break;
			case RIGHT: 
				position.x += 1.5+(float)speed/2;
				rightAnimTime+=deltaTime;
				break;
			}
	}
    
    @Override
    protected void checkCollisionWithWallsAndBricksAndBombs() {
		LinkedList<Vector3> possibleMoves = new LinkedList<Vector3>();
		int x = (int)((position.x+32)/64);
		int y = (int)((position.y+32)/64);
		//adjust the position of the bot
		//so that it always at the center of a block
		
		x*=64;
		y*=64;
		position.x = x;
		position.y = y;
		//down
		if(!game.isAWall(x, y+64) && !game.isABrick(x, y+64) && !game.isAFlame(x, y+64)) {
			possibleMoves.add(new Vector3(x+64,y, UP));
			//System.out.print("UP ");
		}
		//left
		if(!game.isAWall(x, y-64) && !game.isABrick(x, y-64) && !game.isAFlame(x, y-64)) {
			possibleMoves.add(new Vector3(x, y-64,DOWN));
			//System.out.print("DOWN ");
		}
		//left
		if(!game.isAWall(x-64, y) && !game.isABrick(x-64, y) && !game.isAFlame(x-64, y)) {
			possibleMoves.add(new Vector3(x-64, y,LEFT));
			//System.out.print("LEFT ");
		}
		
		//right
		if(!game.isAWall(x+64, y) && !game.isABrick(x+64, y) && !game.isAFlame(x+64, y)) {
			possibleMoves.add(new Vector3(x+64,y,RIGHT));
			//System.out.println("RIGHT");
		}
		int index = (int)(MathUtils.random()*possibleMoves.size());
		
		if(possibleMoves.size() > 0) {
			direction = (int)possibleMoves.get(index).z;
			isMoving = true;
		}
		else {
			isMoving = false;
		}
		//System.out.println(direction);
	}
}