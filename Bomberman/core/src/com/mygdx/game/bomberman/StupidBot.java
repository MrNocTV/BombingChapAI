package com.mygdx.game.bomberman;

import java.awt.Point;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class StupidBot {
	protected Animation backAnim;
	protected Animation frontAnim;
	protected Animation rightAnim;
	protected Animation leftAnim;
	protected float backAnimTime;
	protected float frontAnimTime;
	protected float rightAnimTime;
	protected float leftAnimTime;
	protected Vector2 position;
	protected int direction;
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	protected GameScreen game;
	protected Rectangle botRect;
	protected Rectangle obstacleRect;
	protected boolean isAlive;
	protected boolean isMoving;
	protected String name;
	protected int speed;
	
	public StupidBot(GameScreen game) {
		position = new Vector2();
		
		backAnim = new Animation(0.08f, game.atlas.findRegion("Creep_B_f01"),
				game.atlas.findRegion("Creep_B_f02"),
				
				game.atlas.findRegion("Creep_B_f04"),
				game.atlas.findRegion("Creep_B_f05"),
				game.atlas.findRegion("Creep_B_f01"));
		backAnim.setPlayMode(PlayMode.LOOP);
		frontAnim = new Animation(0.08f, game.atlas.findRegion("Creep_F_f00"),
				game.atlas.findRegion("Creep_F_f01"),
				game.atlas.findRegion("Creep_F_f02"),
				game.atlas.findRegion("Creep_F_f03"),
				game.atlas.findRegion("Creep_F_f04"),
				game.atlas.findRegion("Creep_F_f05"),
				game.atlas.findRegion("Creep_F_f00"));
		frontAnim.setPlayMode(PlayMode.LOOP);
		rightAnim = new Animation(0.08f, game.atlas.findRegion("Creep_S_f00"),
				game.atlas.findRegion("Creep_S_f01"),
				game.atlas.findRegion("Creep_S_f02"),
				game.atlas.findRegion("Creep_S_f03"),
				game.atlas.findRegion("Creep_S_f04"),
				game.atlas.findRegion("Creep_S_f05"),
				game.atlas.findRegion("Creep_S_f06"),
				game.atlas.findRegion("Creep_S_f00"));
		rightAnim.setPlayMode(PlayMode.LOOP);
		LinkedList<TextureRegion> leftTextures = new LinkedList<TextureRegion>();
		for(int count = 0; count <= 6; ++count) {
			leftTextures.add(new TextureRegion(game.atlas.findRegion("Creep_S_f0"+count)));
		}
		for(TextureRegion t : leftTextures) t.flip(true, false);
		leftAnim = new Animation(0.08f, leftTextures.get(0),
				leftTextures.get(1), leftTextures.get(2),
				leftTextures.get(3), leftTextures.get(4),
				leftTextures.get(5), leftTextures.get(6),
				leftTextures.get(0));
		leftAnim.setPlayMode(PlayMode.LOOP);
		botRect = new Rectangle();
		obstacleRect = new Rectangle();
		isAlive = true;
		this.game = game;
		direction = DOWN;
		isMoving = true;
		name = "SP";
	}
	
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
		//check collision with bombs
		for(int count = 0; count < game.bombs.size; ++count) {
			obstacleRect.set(game.bombs.get(count).x, game.bombs.get(count).y,64,64);
			if(botRect.overlaps(obstacleRect)) {
				checkCollisionWithWallsAndBricksAndBombs();
			}
		}
		if(!isMoving) {
			checkCollisionWithWallsAndBricksAndBombs();
		}
		if(isMoving) {
			if(isThereABombOrFlame()) {
				int x = (int)((position.x+32)/64)*64;
				int y = (int)((position.y+32)/64)*64;
				switch(direction) {
				case UP:
					if(!game.isAWall(x, y-64) && !game.isABrick(x, y-64)) direction = DOWN;
					break;
				case DOWN:
					if(!game.isAWall(x, y+64) && !game.isABrick(x, y+64)) direction = UP;
					break;
				case LEFT:
					if(!game.isAWall(x+64, y) && !game.isABrick(x+64, y)) direction = RIGHT;
					break;
				case RIGHT:
					if(!game.isAWall(x-64, y) && !game.isABrick(x-64, y)) direction = LEFT;
					break;
				}
			}
		}
	
		if(isMoving)
			switch(direction) {
			case UP:
				position.y += 1+(float)speed/2;
				backAnimTime+=deltaTime;
				break;
			case DOWN:
				position.y -= (1+(float)speed/2);
				frontAnimTime += deltaTime;
				break;
			case LEFT:
				position.x -= (1+(float)speed/2);
				leftAnimTime += deltaTime;
				break;
			case RIGHT: 
				position.x += 1+(float)speed/2;
				rightAnimTime+=deltaTime;
				break;
			}
	}

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
		if(!game.isAWall(x, y+64) && !game.isABrick(x, y+64) && !game.isABomb(x, y+64) && !game.isAFlame(x, y+64)) {
			possibleMoves.add(new Vector3(x+64,y, UP));
			//System.out.print("UP ");
		}
		//left
		if(!game.isAWall(x, y-64) && !game.isABrick(x, y-64) && !game.isABomb(x, y-64) && !game.isAFlame(x, y-64)) {
			possibleMoves.add(new Vector3(x, y-64,DOWN));
			//System.out.print("DOWN ");
		}
		//left
		if(!game.isAWall(x-64, y) && !game.isABrick(x-64, y) && !game.isABomb(x-64, y) && !game.isAFlame(x-64, y)) {
			possibleMoves.add(new Vector3(x-64, y,LEFT));
			//System.out.print("LEFT ");
		}
		
		//right
		if(!game.isAWall(x+64, y) && !game.isABrick(x+64, y) && !game.isABomb(x+64, y) && !game.isAFlame(x+64, y)) {
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
	
	protected boolean isThereABombOrFlame() {
		int x = (int)((position.x+32)/64)*64;
		int y = (int)((position.y+32)/64)*64;
		
		
		if(direction == UP) {
			while(!game.isAWall(x, y) && !game.isABrick(x, y)) {
				if(game.isABomb(x, y) || game.isAFlame(x, y))
					return true;
				y+=64;
			}
		} else if (direction == DOWN) {
			while(!game.isAWall(x, y) && !game.isABrick(x, y)) {
				if(game.isABomb(x, y) || game.isAFlame(x, y))
					return true;
				y-=64;
			}
		} else if(direction == LEFT) {
			while(!game.isAWall(x, y) && !game.isABrick(x, y)) {
				if(game.isABomb(x, y) || game.isAFlame(x, y))
					return true;
				x-=64;
			}
		} else if(direction == RIGHT ) {
			while(!game.isAWall(x, y) && !game.isABrick(x, y)) {
				if(game.isABomb(x, y) || game.isAFlame(x, y))
					return true;
				x+=64;
			}
		}
		
		return false;
	}
	
}
