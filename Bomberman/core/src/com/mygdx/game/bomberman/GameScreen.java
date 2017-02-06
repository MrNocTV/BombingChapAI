package com.mygdx.game.bomberman;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends ScreenAdapter{
	protected SpriteBatch batch;
	private OrthographicCamera camera;
	private FPSLogger fpsLogger;
	protected ShapeRenderer shapeRenderer;
	protected Vector2 manPosition;
	private Rectangle manRect;
	protected TextureAtlas atlas;
	private Animation manFrontAnim;
	private Animation manBackAnim;
	private Animation manRightAnim;
	private Animation manLeftAnim;
	private float manFrontAnimTime;
	private float manBackAnimTime;
	private float manRightAnimTime;
	private float manLeftAnimTime;
	private boolean moving;
	private int direction;
	private static final int UP = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	private static final int RIGHT = 4;
	private TextureRegion standingTexture;
	private TextureRegion[] leftTextureRegions;
	protected Array<Point> walls;
	private Rectangle obstacleRect;
	private Texture wallTexture;
	private Texture bgTile;
	private static final int MAX_SPEED = 3;
	private static final int MAX_NUMBER_OF_BOMBS = 6;
	private enum GameState {INIT, ACTION, GAMEOVER};
	private GameState gameState;
	private int manSpeed;
	private TextureRegion bompTexture;
	protected Array<Vector3> bombs;
	private int numberOfBoms;
	private Animation flameAnim;
	private float flameAnimTime;
	private int bombLength;
	private Array<Vector3> flames;
	private StupidBot stupidBot;
	protected Array<Point> bricks;
	private Texture brickTexture;
	private SmartBot smartBot;
	private PrettySmartBot prettySmartBot;
	private WeirdBot weirdBot;
	private PrettyWeirdBot prettyWeirdBot;
	private Array<Vector3> items;
	private TextureRegion speedUpTexture; //increase speed of bomberman
	private TextureRegion bombUpTexture; //increase number of bombs
	private static final int BOMB = 2;
	private static final int SPEED = 3;
	private float invisibleTime;
	private int lives;
	private BitmapFont bmFont;
	private Texture gameover;
	private Rectangle botRect;
	private TextureRegion botStandingTexture;
	private Music music;
	private Sound explosion;
	private NewBot newBot;
	
	public GameScreen(Bomberman game) {
		batch = game.batch; //this help drawing stuff onto the screen efficiently
		camera = game.camera; //this help us to see the world
		fpsLogger = new FPSLogger(); //log how many fps 
		shapeRenderer = new ShapeRenderer(); //for debugging
		manPosition = new Vector2();
		manRect = new Rectangle();
		botRect = new Rectangle();
		atlas = game.assetManager.get("bombermanassets.txt", TextureAtlas.class);
		botStandingTexture = atlas.findRegion("Creep_F_f00");
		manFrontAnim = new Animation(0.05f, atlas.findRegion("Bman_F_f00"),
				atlas.findRegion("Bman_F_f02"),
				atlas.findRegion("Bman_F_f03"),
				atlas.findRegion("Bman_F_f04"),
				atlas.findRegion("Bman_F_f05"),
				atlas.findRegion("Bman_F_f06"),
				atlas.findRegion("Bman_F_f07"),
				atlas.findRegion("Bman_F_f00"));
		manFrontAnim.setPlayMode(PlayMode.LOOP);
		manBackAnim = new Animation(0.05f, atlas.findRegion("Bman_B_f00"),
				atlas.findRegion("Bman_B_f01"),
				atlas.findRegion("Bman_B_f02"),
				atlas.findRegion("Bman_B_f03"),
				atlas.findRegion("Bman_B_f04"),
				atlas.findRegion("Bman_B_f05"),
				atlas.findRegion("Bman_B_f06"),
				atlas.findRegion("Bman_B_f07"),
				atlas.findRegion("Bman_B_f00"));
		manBackAnim.setPlayMode(PlayMode.LOOP);
		manRightAnim = new Animation(0.05f, atlas.findRegion("Bman_S_f00"),
				atlas.findRegion("Bman_S_f01"),
				atlas.findRegion("Bman_S_f02"),
				atlas.findRegion("Bman_S_f03"),
				atlas.findRegion("Bman_S_f04"),
				atlas.findRegion("Bman_S_f05"),
				atlas.findRegion("Bman_S_f07"),
				atlas.findRegion("Bman_S_f00"));
		manRightAnim.setPlayMode(PlayMode.LOOP);
		leftTextureRegions = new TextureRegion[8];
		bmFont = game.assetManager.get("default.fnt", BitmapFont.class);
		gameover = game.assetManager.get("gameover.png", Texture.class);
		music = game.assetManager.get("bomberman.mp3", Music.class);
		music.setLooping(true);
		if(!game.mute)
			music.play();
		explosion = game.assetManager.get("explosion.wav", Sound.class);
		for(int count = 0; count <= 7; ++count) {
			if(count != 6) {
				leftTextureRegions[count]= new TextureRegion(atlas.findRegion("Bman_S_f0"+count));
				leftTextureRegions[count].flip(true, false);
			}
		}
		
		manLeftAnim = new Animation(0.1f, leftTextureRegions[0],
				leftTextureRegions[1],
				leftTextureRegions[2],
				leftTextureRegions[3],
				leftTextureRegions[4],
				leftTextureRegions[5],
				leftTextureRegions[7],
				leftTextureRegions[0]);
		
		manLeftAnim.setPlayMode(PlayMode.LOOP);
		standingTexture = atlas.findRegion("Bman_F_f00");
		speedUpTexture = atlas.findRegion("SpeedPowerup");

		bombUpTexture = atlas.findRegion("BombPowerup");
		walls = new Array<Point>();
		//set position of walls
		for(int count = 1; count <= 15; ++count) {
			walls.add(new Point((count-1)*64, 0));
			walls.add(new Point((count-1)*64, Bomberman.SCREEN_HEIGHT-64));
		}
		for(int count = 1; count <= 9; ++count) {
			walls.add(new Point(0, (count-1)*64));
			walls.add(new Point(14*64, (count-1)*64));
		}
		//makeUITMap();
		
		for(int count = 1; count <= 3; ++count)
			for(int count1 = 1; count1 <= 6; ++count1) {
				walls.add(new Point(count1*2*64, count*2*64));
			}
			
		
		obstacleRect = new Rectangle();
		wallTexture = game.assetManager.get("SolidBlock.png", Texture.class);
		bgTile = game.assetManager.get("BackgroundTile.png", Texture.class);
		bombs = new Array<Vector3>();
		lives = 3;
		bompTexture = atlas.findRegion("bomp");
		flameAnim = new Animation(0.05f,
									atlas.findRegion("Flame_f00"),
									atlas.findRegion("Flame_f01"),
									atlas.findRegion("Flame_F02"),
									atlas.findRegion("Flame_F03"),
									atlas.findRegion("Flame_F04"),
									atlas.findRegion("Flame_f00"));
		flameAnim.setPlayMode(PlayMode.LOOP);
		flames = new Array<Vector3>();
		

		
		brickTexture = new Texture(Gdx.files.internal("ExplodableBlock.png"));
		manPosition.x = 64;
		manPosition.y = 64;
		
		
		
		resetScene();
	}
	
	//this method is where the game logic occur 
	//it also handle drawing stuff onto the screen
	@Override
	public void render(float deltaTime) {
		//clear scene
		Gdx.gl.glClearColor(0, 0, 0, 1); //black
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		updateScene(deltaTime);
		drawScene();
		drawDebug();
		
			if(Gdx.input.justTouched()) {
				resetScene();
			}
		
	}
	
	//release memory
	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
	}
	
	////////////////////////
	///////////// Private
	///////////// Methods
	///////////////////////

	private void resetScene() {
		stupidBot = new StupidBot(this);
		smartBot = new SmartBot(this);
		prettySmartBot = new PrettySmartBot(this);
		weirdBot = new WeirdBot(this);
		prettyWeirdBot = new PrettyWeirdBot(this);
		newBot = new NewBot(this);
		manPosition.set(64, 64);
		manFrontAnimTime = 0;
		manBackAnimTime = 0;
		manRightAnimTime = 0;
		moving = false;
		manSpeed = MAX_SPEED;
		bombs = new Array<Vector3>();
		numberOfBoms = MAX_NUMBER_OF_BOMBS;
		smartBot.speed = manSpeed;
		prettySmartBot.speed = manSpeed;
		weirdBot.speed = manSpeed;
		prettyWeirdBot.speed = manSpeed;
		stupidBot.speed = manSpeed;
		newBot.speed = manSpeed;
		flameAnimTime = 0;
		bombLength = 1;
		flameAnimTime = 0;
		flames = new Array<Vector3>();
		stupidBot.isAlive = true;
		smartBot.isAlive = true;
		prettySmartBot.isAlive = true;
		weirdBot.isAlive = true;
		prettyWeirdBot.isAlive = true;
		bricks = new Array<Point>();
		items = new Array<Vector3>();
		
		invisibleTime = 3f;
		stupidBot.position.set(64*9, 64);
		smartBot.position.x = 64*5;
		smartBot.position.y = 64*8;
		prettySmartBot.position.x = 64*9;
		prettySmartBot.position.y = 64*8;
		weirdBot.position.x = 64*13;
		weirdBot.position.y = 64*6;
		prettyWeirdBot.position.x = 64*3;
		prettyWeirdBot.position.y = 64*8;
		lives = 3;
		/*
		while(bricks.size < 50) {
			int x = (int)(MathUtils.random(1,14));
			int y = (int)(MathUtils.random(1,9));
		    x*=64;
			y*=64;
			if((x  != stupidBot.position.x || y != stupidBot.position.y) //not the bot position
				&& (x >= manPosition.x*4  || y != manPosition.y)
				&& (x != smartBot.position.x || y != smartBot.position.y)
				&& (x != prettySmartBot.position.x || y != prettySmartBot.position.y)
				&& (x != weirdBot.position.x || y != weirdBot.position.y)
				&& (x != prettyWeirdBot.position.x || y != prettyWeirdBot.position.y)
				&&	!isAWall(x, y)//and not a wall
				&& !isABrick(x, y)) {//and not bomberman position
				bricks.add(new Point(x,y));
				
			}
		}
		*/
		gameState = GameState.ACTION;
	}
	
	private void updateScene(float deltaTime) {
		if(gameState == gameState.INIT || gameState == gameState.GAMEOVER) {
			if(gameState == GameState.INIT) 
				gameState = GameState.ACTION;
			return;
		}
		//input handling
		checkForInput(deltaTime);
		placeBomp();
		
		if(stupidBot.isAlive)
			stupidBot.move(deltaTime);
		
		
		updateBombsTimer(deltaTime);
		flameAnimTime += deltaTime;
		if(invisibleTime > 0)
			invisibleTime -= deltaTime;
		flameTriggerBomb();//flame of bomb trigger other bombs
		updateFlamesTimer(deltaTime);
		int[][] blockedMap = new int[walls.size + bricks.size + bombs.size + flames.size][2];
		int count = 0;
		int x = 0;
		int y = 0;
		for(int i = 0; i < walls.size; ++i) {
			x = (int)(walls.get(i).x + 32)/64;
			y = (int)(walls.get(i).y + 32)/64;
			blockedMap[count][0] = 9-y;
			blockedMap[count++][1] = x;
		}
		for(int i = 0; i < bricks.size; ++i) {
			x = (int)(bricks.get(i).x + 32)/64;
			y = (int)(bricks.get(i).y + 32)/64;
			blockedMap[count][0] = 9-y;
			blockedMap[count++][1] = x;
		}
		for(int i = 0; i < bombs.size; ++i) {
			x = (int)(bombs.get(i).x + 32)/64;
			y = (int)(bombs.get(i).y + 32)/64;
			blockedMap[count][0] = 9-y;
			blockedMap[count++][1] = x;
		}
		for(int i = 0; i < flames.size; ++i) {
			x = (int)(flames.get(i).x + 32)/64;
			y = (int)(flames.get(i).y + 32)/64;
			blockedMap[count][0] = 9-y;
			blockedMap[count++][1] = x;
		}
		
		
		//if the bot is alive, it can still move
		
		if(smartBot.isAlive) {
			//if(smartBot1.huntTime > 0)
				if(smartBot.foundPath)
					if(!smartBot.movingToNext) {
							smartBot.findPath(10, 15, blockedMap);
							if(smartBot.foundPath) {
								smartBot.hunt();
								int xx = (int)(smartBot.position.x+32)/64;
								int yy = (int)(smartBot.position.y+32)/64;
								smartBot.position.x = xx*64;
								smartBot.position.y = yy*64;
							}
					} else {
							if(smartBot.foundPath)
								smartBot.moveNext(deltaTime);
						}
				else  {
					smartBot.move(deltaTime);
					smartBot.findPath(10, 15, blockedMap);
				}
	
		}
		
		
		if(prettySmartBot.isAlive) {
			//if(smartBot1.huntTime > 0)
				if(prettySmartBot.foundPath)
					if(!prettySmartBot.movingToNext) {
						prettySmartBot.findPath(10, 15, blockedMap);
							if(prettySmartBot.foundPath) {
								prettySmartBot.hunt();
								int xx = (int)(prettySmartBot.position.x+32)/64;
								int yy = (int)(prettySmartBot.position.y+32)/64;
								prettySmartBot.position.x = xx*64;
								prettySmartBot.position.y = yy*64;
							}
					} else {
							if(prettySmartBot.foundPath)
								prettySmartBot.moveNext(deltaTime);
						}
				else  {
					prettySmartBot.move(deltaTime);
					prettySmartBot.findPath(10, 15, blockedMap);
				}
	
		}
		
		
		if(weirdBot.isAlive) {
			if(weirdBot.foundPath)
				if(!weirdBot.movingToNext) {
					weirdBot.findPath(10, 15, blockedMap);
						if(weirdBot.foundPath) {
							weirdBot.hunt();
							int xx = (int)(weirdBot.position.x+32)/64;
							int yy = (int)(weirdBot.position.y+32)/64;
							weirdBot.position.x = xx*64;
							weirdBot.position.y = yy*64;
						}
				} else {
						if(weirdBot.foundPath)
							weirdBot.moveNext(deltaTime);
					}
			else  {
				weirdBot.move(deltaTime);
				weirdBot.findPath(10, 15, blockedMap);
			}
		}
		
		if(prettyWeirdBot.isAlive) {
			if(prettyWeirdBot.foundPath)
				if(!prettyWeirdBot.movingToNext) {
					prettyWeirdBot.findPath(10, 15, blockedMap);
						if(prettyWeirdBot.foundPath) {
							prettyWeirdBot.hunt();
							int xx = (int)(prettyWeirdBot.position.x+32)/64;
							int yy = (int)(prettyWeirdBot.position.y+32)/64;
							prettyWeirdBot.position.x = xx*64;
							prettyWeirdBot.position.y = yy*64;
						}
				} else {
						if(prettyWeirdBot.foundPath)
							prettyWeirdBot.moveNext(deltaTime);
					}
			else  {
				prettyWeirdBot.move(deltaTime);
				prettyWeirdBot.findPath(10, 15, blockedMap);
			}
		}
		
		if(newBot.isAlive) {
			if(newBot.foundPath)
				if(!newBot.movingToNext) {
					newBot.findPath(10, 15, blockedMap);
						if(newBot.foundPath) {
							newBot.hunt();
							int xx = (int)(newBot.position.x+32)/64;
							int yy = (int)(newBot.position.y+32)/64;
							newBot.position.x = xx*64;
							newBot.position.y = yy*64;
						}
				} else {
						if(newBot.foundPath)
							newBot.moveNext(deltaTime);
					}
			else  {
				newBot.move(deltaTime);
				newBot.findPath(10, 15, blockedMap);
			}
		}
		
	
		flameKillBots();
		if(invisibleTime <= 0)
			flameKillBomberman();
		if(invisibleTime <= 0)
			botsKillBomberman();
		checkCollisionWithItems();
		checkForEndGame();
	}
	
	private void drawScene() {
		batch.begin(); //start drawing
			//draw stuff
		//draw background tiles
		for(int count1 = 1; count1 < 14; ++count1) {
			for(int count2 = 1; count2 <9; ++count2 ) {
				int x = count1*64;
				int y = count2*64;
				if(!walls.contains(new Point(x,y), false)) 
					batch.draw(bgTile, x, y);
			}
		}
		//draw walls (solid block)
		for(Point point : walls)
			batch.draw(wallTexture, point.x, point.y);
		//draw bricks (explodable blocks)
		for(Point brick : bricks)
			batch.draw(brickTexture, brick.x, brick.y);
		//draw bombs
		for(Vector3 bomb : bombs)
			batch.draw(bompTexture, bomb.x+10, bomb.y+10);
		
		//draw flames
		for(Vector3 flame : flames) 
			batch.draw(flameAnim.getKeyFrame(flameAnimTime), flame.x+10, flame.y+10);
		//draw bomberman animation
		if(invisibleTime>0)
			batch.setColor(Color.BLUE);
		else
			batch.setColor(Color.WHITE);
		if(moving) {
			switch(direction) {
			case UP: 
				batch.draw(manBackAnim.getKeyFrame(manBackAnimTime), manPosition.x, manPosition.y,64,64);
				break;
			case DOWN:
				batch.draw(manFrontAnim.getKeyFrame(manFrontAnimTime), manPosition.x, manPosition.y,64,64);
				break;
			case LEFT:
				batch.draw(manLeftAnim.getKeyFrame(manLeftAnimTime), manPosition.x, manPosition.y,64,64);
				break;
			case RIGHT:
				batch.draw(manRightAnim.getKeyFrame(manRightAnimTime), manPosition.x, manPosition.y,64,64);
				break;
			}
		} else {
			//draw bomberman when it's not moving
			batch.draw(standingTexture, manPosition.x, manPosition.y,64,64);
		}
		batch.setColor(Color.WHITE);
		batch.draw(standingTexture, 980,550);
		bmFont.draw(batch, "x"+lives, 1040, 570);
		batch.draw(botStandingTexture, 980,470);
		bmFont.draw(batch, "x", 1040, 490);
		batch.draw(speedUpTexture, 990, 400);
		bmFont.draw(batch, "x"+manSpeed, 1040, 425);
		batch.draw(bombUpTexture, 990, 330);
		bmFont.draw(batch, "x"+numberOfBoms, 1040, 355);
		
		
		//draw stupidBot
		if(stupidBot.isAlive) {
			switch(stupidBot.direction) {
			case StupidBot.UP:
				batch.draw(stupidBot.backAnim.getKeyFrame(stupidBot.backAnimTime), stupidBot.position.x+6, stupidBot.position.y+6);
				break;
			case StupidBot.DOWN:
				batch.draw(stupidBot.frontAnim.getKeyFrame(stupidBot.frontAnimTime), stupidBot.position.x+6, stupidBot.position.y+6);
				break;
			case StupidBot.LEFT:
				batch.draw(stupidBot.leftAnim.getKeyFrame(stupidBot.leftAnimTime), stupidBot.position.x+6, stupidBot.position.y+6);
				break;
			case StupidBot.RIGHT:
				batch.draw(stupidBot.rightAnim.getKeyFrame(stupidBot.rightAnimTime), stupidBot.position.x+6, stupidBot.position.y+6);
				break;
			}
			bmFont.draw(batch, stupidBot.name, stupidBot.position.x+25,stupidBot.position.y);
		}
		if(smartBot.isAlive) {
			switch(smartBot.direction) {
			case StupidBot.UP:
				batch.draw(smartBot.backAnim.getKeyFrame(smartBot.backAnimTime), smartBot.position.x+6, smartBot.position.y+6);
				break;
			case StupidBot.DOWN:
				batch.draw(smartBot.frontAnim.getKeyFrame(smartBot.frontAnimTime), smartBot.position.x+6, smartBot.position.y+6);
				break;
			case StupidBot.LEFT:
				batch.draw(smartBot.leftAnim.getKeyFrame(smartBot.leftAnimTime), smartBot.position.x+6, smartBot.position.y+6);
				break;
			case StupidBot.RIGHT:
				batch.draw(smartBot.rightAnim.getKeyFrame(smartBot.rightAnimTime), smartBot.position.x+6, smartBot.position.y+6);
				break;
			}
			bmFont.draw(batch, smartBot.name, smartBot.position.x+25,smartBot.position.y);
		}
		if(prettySmartBot.isAlive) {
			switch(prettySmartBot.direction) {
			case StupidBot.UP:
				batch.draw(prettySmartBot.backAnim.getKeyFrame(prettySmartBot.backAnimTime), prettySmartBot.position.x+6, prettySmartBot.position.y+6);
				break;
			case StupidBot.DOWN:
				batch.draw(prettySmartBot.frontAnim.getKeyFrame(prettySmartBot.frontAnimTime), prettySmartBot.position.x+6, prettySmartBot.position.y+6);
				break;
			case StupidBot.LEFT:
				batch.draw(prettySmartBot.leftAnim.getKeyFrame(prettySmartBot.leftAnimTime), prettySmartBot.position.x+6, prettySmartBot.position.y+6);
				break;
			case StupidBot.RIGHT:
				batch.draw(prettySmartBot.rightAnim.getKeyFrame(prettySmartBot.rightAnimTime), prettySmartBot.position.x+6, prettySmartBot.position.y+6);
				break;
			}
			bmFont.draw(batch, prettySmartBot.name, prettySmartBot.position.x+20,prettySmartBot.position.y);
		}
	
		if(weirdBot.isAlive) {
			switch(weirdBot.direction) {
			case StupidBot.UP:
				batch.draw(weirdBot.backAnim.getKeyFrame(weirdBot.backAnimTime), weirdBot.position.x+6, weirdBot.position.y+6);
				break;
			case StupidBot.DOWN:
				batch.draw(weirdBot.frontAnim.getKeyFrame(weirdBot.frontAnimTime), weirdBot.position.x+6, weirdBot.position.y+6);
				break;
			case StupidBot.LEFT:
				batch.draw(weirdBot.leftAnim.getKeyFrame(weirdBot.leftAnimTime), weirdBot.position.x+6, weirdBot.position.y+6);
				break;
			case StupidBot.RIGHT:
				batch.draw(weirdBot.rightAnim.getKeyFrame(weirdBot.rightAnimTime), weirdBot.position.x+6, weirdBot.position.y+6);
				break;
			}
			bmFont.draw(batch, weirdBot.name, weirdBot.position.x+20,weirdBot.position.y);
		}
		if(prettyWeirdBot.isAlive) {
			switch(prettyWeirdBot.direction) {
			case StupidBot.UP:
				batch.draw(prettyWeirdBot.backAnim.getKeyFrame(prettyWeirdBot.backAnimTime), prettyWeirdBot.position.x+6, prettyWeirdBot.position.y+6);
				break;
			case StupidBot.DOWN:
				batch.draw(prettyWeirdBot.frontAnim.getKeyFrame(prettyWeirdBot.frontAnimTime), prettyWeirdBot.position.x+6, prettyWeirdBot.position.y+6);
				break;
			case StupidBot.LEFT:
				batch.draw(prettyWeirdBot.leftAnim.getKeyFrame(prettyWeirdBot.leftAnimTime), prettyWeirdBot.position.x+6, prettyWeirdBot.position.y+6);
				break;
			case StupidBot.RIGHT:
				batch.draw(prettyWeirdBot.rightAnim.getKeyFrame(prettyWeirdBot.rightAnimTime), prettyWeirdBot.position.x+6, prettyWeirdBot.position.y+6);
				break;
			}
			bmFont.draw(batch, prettyWeirdBot.name, prettyWeirdBot.position.x+15,prettyWeirdBot.position.y);
		}
		if(newBot.isAlive) {
			switch(newBot.direction) {
			case StupidBot.UP:
				batch.draw(newBot.backAnim.getKeyFrame(newBot.backAnimTime), newBot.position.x+6, newBot.position.y+6);
				break;
			case StupidBot.DOWN:
				batch.draw(newBot.frontAnim.getKeyFrame(newBot.frontAnimTime), newBot.position.x+6, newBot.position.y+6);
				break;
			case StupidBot.LEFT:
				batch.draw(newBot.leftAnim.getKeyFrame(newBot.leftAnimTime), newBot.position.x+6, newBot.position.y+6);
				break;
			case StupidBot.RIGHT:
				batch.draw(newBot.rightAnim.getKeyFrame(newBot.rightAnimTime), newBot.position.x+6, newBot.position.y+6);
				break;
			}
			bmFont.draw(batch, newBot.name, newBot.position.x+15,newBot.position.y);
		}
		
		for(Vector3 item : items) {
			int type = (int)item.z;
			switch(type){
			case BOMB:
				batch.draw(bombUpTexture, item.x+16,item.y+16,32,32);
				break;
			case SPEED:
				batch.draw(speedUpTexture, item.x+16,item.y+16,32,32);
				break;
			}
		}
		
		if(gameState == GameState.GAMEOVER) {
			batch.draw(gameover, 300,350);
			bmFont.draw(batch, "Click anywhere to continue!", 400, 300);
		}
		//draw HUD (Head Up Display)
		
		
		batch.end(); //finish drawing
	}
	
	private void checkForInput(float deltaTime) {
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			//calculate position (coordinate of the left block)
			int x = (int)(((manPosition.x+32)/64) -1 )*64;
			int y = (int)((manPosition.y+32)/64)*64;
			//check if the left block is a wall, and check for the collision with it
			if(!checkCollisionWithWalls(x,y) && !checkCollisionWithBricks(x, y) && !checkCollisionWithBombs(x, y)) {
				manPosition.x -= (2+manSpeed);
				moving = true;
				direction = LEFT;
				manLeftAnimTime+=deltaTime;
			}
			
		} else if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			//calculate the position(coordinate of the right block)
			int x = (int)(((manPosition.x+32)/64) +1 )*64;
			int y = (int)((manPosition.y+32)/64)*64;
			//check if the right block is a wall, and check for the collision with it
			if(!checkCollisionWithWalls(x,y) && !checkCollisionWithBricks(x, y) && !checkCollisionWithBombs(x, y)) {
				manPosition.x += 2+manSpeed;
				moving = true;
				manRightAnimTime+=deltaTime;
				direction = RIGHT;
			}
		} else if(Gdx.input.isKeyPressed(Keys.UP)) {
			//calculate the position (coordinate of the "above" block)
			int x = (int)((manPosition.x+32)/64)*64;
			int y = (int)(((manPosition.y+32)/64) +1)*64;
			//check if it is a wall and check for collision with it
			if(!checkCollisionWithWalls(x,y) && !checkCollisionWithBricks(x, y) && !checkCollisionWithBombs(x, y)) {
				manPosition.y += 2+manSpeed;
				manBackAnimTime+=deltaTime;
				moving = true;
				direction = UP;
			}
			
		} else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			//calculate the position (coordinate of the "below" block)
			int x = (int)((manPosition.x+32)/64)*64;
			int y = (int)(((manPosition.y+32)/64)-1 )*64;
			//check if it is a wall and check for collision with it
			if(!checkCollisionWithWalls(x,y) && !checkCollisionWithBricks(x, y) && !checkCollisionWithBombs(x, y)){
				manPosition.y -= (2+manSpeed);
				manFrontAnimTime+=deltaTime;
				moving = true;
				direction = DOWN;
			}
			
		} else {
			//key released
			//bomberman stand still
			moving = false;
			
		}
		//print for debugging
		//System.out.println("("+(int)((manPosition.x+32)/64)+","+(int)((manPosition.y+32)/64)+")" );
	}
	
	
	//for debugging
	private void drawDebug() {
		shapeRenderer.begin(ShapeType.Line);
		
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(manPosition.x+19, manPosition.y+19, 26, 26);
		if(stupidBot.isAlive)
			shapeRenderer.rect(stupidBot.position.x+6, stupidBot.position.y+6,51,51);
		
		if(smartBot.isAlive) {
			  if(smartBot.foundPath){
	               //Trace back the path 
	               // System.out.print("Path: ");
	                SmartBot.Cell current = smartBot.grid[smartBot.targetX][smartBot.targetY];
	                //System.out.print(current);
	                while(current.parent!=null){
	                	//y, 9-x
	                    shapeRenderer.rect(current.j*64, 9*64-current.i*64, 64,64);
	                    current = current.parent;
	                } 
	                //System.out.println();
	           }//else System.out.println("No possible path");
		}
		if(prettySmartBot.isAlive) {
			  if(prettySmartBot.foundPath){
	               //Trace back the path 
	               // System.out.print("Path: ");
	                PrettySmartBot.Cell current = prettySmartBot.grid[prettySmartBot.targetX][prettySmartBot.targetY];
	                //System.out.print(current);
	                while(current.parent!=null){
	                	//y, 9-x
	                    shapeRenderer.rect(current.j*64, 9*64-current.i*64, 64,64);
	                    current = current.parent;
	                } 
	                //System.out.println();
	           }//else System.out.println("No possible path");
		}
		
		if(weirdBot.isAlive) {
			  if(weirdBot.foundPath){
	               //Trace back the path 
	               // System.out.print("Path: ");
	                WeirdBot.Cell current = weirdBot.grid[weirdBot.targetX][weirdBot.targetY];
	                //System.out.print(current);
	                while(current.parent!=null){
	                	//y, 9-x
	                    shapeRenderer.rect(current.j*64, 9*64-current.i*64, 64,64);
	                    current = current.parent;
	                } 
	                //System.out.println();
	           }//else System.out.println("No possible path");
		}
		shapeRenderer.setColor(Color.BLUE);
		
		if(prettyWeirdBot.isAlive) {
			  if(prettyWeirdBot.foundPath){
	               //Trace back the path 
	               // System.out.print("Path: ");
	                PrettyWeirdBot.Cell current = prettyWeirdBot.grid[prettyWeirdBot.targetX][prettyWeirdBot.targetY];
	                //System.out.print(current);
	                while(current.parent!=null){
	                	//y, 9-x
	                    shapeRenderer.rect(current.j*64, 9*64-current.i*64, 64,64);
	                    current = current.parent;
	                } 
	                //System.out.println();
	           }//else System.out.println("No possible path");
		}
		shapeRenderer.setColor(Color.GREEN);
		if(newBot.isAlive) {
			  if(newBot.foundPath){
	               //Trace back the path 
	               // System.out.print("Path: ");
	                NewBot.Cell current = newBot.grid[newBot.targetX][newBot.targetY];
	                //System.out.print(current);
	                while(current.parent!=null){
	                	//y, 9-x
	                    shapeRenderer.rect(current.j*64, 9*64-current.i*64, 64,64);
	                    current = current.parent;
	                } 
	                //System.out.println();
	           }//else System.out.println("No possible path");
		}
		shapeRenderer.setColor(Color.WHITE);
		
		shapeRenderer.end();
	}
	
	//bomb at position (x,y) exploses
	private void bombExplose(int x, int y) {
		int copyX = x;
		int copyY = y;
		int vX = copyX/64;
		int vY = copyY/64;
		//smartBot1.map[9-vY][vX] = 0;
	    //smartBot2.map[9-vY][vX] = 0;
		//explore center
		flames.add(new Vector3(x,y,1));
		explosion.play(0.7f);
		//explore left
		for(int count = 1; count <= bombLength; ++count) {
			copyX-=64;
			//check if current block is a wall
			if(isAnItem(copyX, copyY)) 
				removeItem(copyX,copyY);
			if(isAWall(copyX, copyY)) break;
			if(isABrick(copyX, copyY)) {
				addItem(copyX,copyY);
				//remove the brick
				bricks.removeValue(new Point(copyX, copyY), false);
				int indexX = copyX/64;
				int indexY = copyY/64;
				//smartBot1.map[9-indexY][indexX] = 0;
				//smartBot1.printMap(smartBot1.map);
				break;
			}
			flames.add(new Vector3(copyX,copyY,1));
			
		}
		//explore right
		copyX = x;
		copyY = y;
		for(int count = 1; count <= bombLength; ++count) {
			copyX+=64;
			//check if current block is a wall
			if(isAnItem(copyX, copyY)) 
				removeItem(copyX,copyY);
			if(isAWall(copyX, copyY)) break;
			if(isABrick(copyX, copyY)) {
				addItem(copyX,copyY);
				//remove the brick
				bricks.removeValue(new Point(copyX, copyY), false);
				int indexX = copyX/64;
				int indexY = copyY/64;
				//smartBot1.map[9-indexY][indexX] = 0;
			//	smartBot1.printMap(smartBot1.map);
				break;
			}
			flames.add(new Vector3(copyX,copyY,1));
			
		}
		//explore up
		copyX = x;
		copyY = y;
		for(int count = 1; count <= bombLength; ++count) {
			copyY += 64;
			//check if current block is a wall
			if(isAnItem(copyX, copyY)) 
				removeItem(copyX,copyY);
			if(isAWall(copyX, copyY)) break;
			if(isABrick(copyX, copyY)) {
				//remove the brick
				addItem(copyX,copyY);
				bricks.removeValue(new Point(copyX, copyY), false);
				
				int indexX = copyX/64;
				int indexY = copyY/64;
				//smartBot1.map[9-indexY][indexX] = 0;
				//smartBot1.printMap(smartBot1.map);
				break;
			}
			flames.add(new Vector3(copyX,copyY,1));
			
		}
		//explore down
		copyX = x;
		copyY = y;
		for(int count = 1; count <= bombLength; ++count) {
			copyY -= 64;
			//check if current block is a wall
			if(isAnItem(copyX, copyY)) 
				removeItem(copyX,copyY);
			if(isAWall(copyX, copyY)) break;
			if(isABrick(copyX, copyY)) {
				addItem(copyX,copyY);
				//remove the brick
				bricks.removeValue(new Point(copyX, copyY), false);
				int indexX = copyX/64;
				int indexY = copyY/64;
				//smartBot1.map[9-indexY][indexX] = 0;
				//smartBot1.printMap(smartBot1.map);
				break;
			}
			flames.add(new Vector3(copyX,copyY,1));
		}
		
	}
	
	private void removeItem(int x, int y) {
		for(Vector3 item : items) {
			if(item.x == x && item.y == y) {
				items.removeValue(item, false);
			}
		}
	}
	
	protected boolean isAWall(int x, int y) {
		for(Point wall : walls) {
			if(wall.x == x && wall.y == y)
				return true;
		}
		return false;
	}
	
	protected boolean isABrick(int x, int y) {
		for(Point brick : bricks)
			if(brick.x == x && brick.y == y)
				return true;
		return false;	
	}
	
	protected boolean isABomb(int x, int y) {
		for(Vector3 bomb: bombs) {
			if(bomb.x == x && bomb.y == y)
				return true;
		}
		return false;
	}
	
	protected boolean isAFlame(int x, int y) {
		for(Vector3 flame : flames) {
			if(flame.x == x && flame.y == y)
				return true;
		}
		return false;
	}
	
	protected boolean isHuman(int x, int y) {
		return manPosition.x == x && manPosition.y == y;
	}
	
	private boolean isAnItem(int x, int y) {
		for(Vector3 item : items) {
			if(item.x == x && item.y == y)
				return true;
		}
		return false;
	}
	
	protected void placeBomp() {
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println("Space pressed");
			//calculate the position of bomberman
			int x = (int)((manPosition.x+32)/64);
			int y = (int)((manPosition.y+32)/64);
		    //smartBot1.map[9-y][x] = -1;
		    //smartBot2.map[9-y][x] = -1;
			System.out.println("(" + x + "," +y+")");
			x*=64;
			y*=64;
			x = x == 0 ? 64 : x;
			y = y == 0 ? 64 : y;
			
			//check if there is a bomb at this position
			for(Vector3 bomb : bombs) {
				if(bomb.x == x && bomb.y == y) return;
			}
			
			if(bombs.size < numberOfBoms) {
				//x,y is the position of the bomb
				//z is the time, after z time, the bomb is "booooooommmmm"
				bombs.add(new Vector3(x, y, 3));
			}
		}
	}
	
	
	//bomb goes off after 3 seconds
	private void updateBombsTimer(float deltaTime) {
		for(Vector3 bomb : bombs) {
			bomb.z -= deltaTime; //update timer
			if(bomb.z <= 0) { //out of time
				int x = (int)bomb.x/64;
				int y = (int)bomb.y/64;
				//smartBot1.map[9-y][x] = 0;
				bombs.removeValue(bomb, false); //remove the bomb
				bombExplose((int)bomb.x, (int)bomb.y); //explose
			}
		}
	}
	
	//bomb goes off and trigger other bombs around it 
	private void flameTriggerBomb() {
		for(Vector3 flame : flames) {
			if(isABomb((int)flame.x, (int)flame.y)) {
				//remove bomb
				for(Vector3 bomb: bombs) {
					if(bomb.x == flame.x && bomb.y == flame.y) {
						int x = (int)bomb.x / 64;
						int y = (int)bomb.y / 64;
						//smartBot1.map[9-y][x] = 0;
						bombs.removeValue(bomb, false);
						//trigger explosion
						bombExplose((int)bomb.x, (int)bomb.y);
						break;
					}
				}
				
				
			}
		}
	}
	
	private void flameKillBots() {
		for(Vector3 flame : flames) {
			obstacleRect.set(flame.x+8, flame.y+8, 48,48);
			if(stupidBot.isAlive) {
				botRect.set(stupidBot.position.x+19, stupidBot.position.y+19,26,26);
				if(obstacleRect.overlaps(botRect))
					stupidBot.isAlive = false; //kill the bot	
			}
			if(smartBot.isAlive) {
				botRect.set(smartBot.position.x+19, smartBot.position.y+19,26,26);
				if(obstacleRect.overlaps(botRect))
					smartBot.isAlive = false; //kill the bot	
			}
			if( prettySmartBot.isAlive) {
				botRect.set(prettySmartBot.position.x+19, prettySmartBot.position.y+19,26,26);
				if(obstacleRect.overlaps(botRect))
					prettySmartBot.isAlive = false; //kill the bot	
			}
			if(weirdBot.isAlive) {
				botRect.set(weirdBot.position.x+19, weirdBot.position.y+19,26,26);
				if(obstacleRect.overlaps(botRect))
					weirdBot.isAlive = false; //kill the bot	
			}
			if(prettyWeirdBot.isAlive){
				botRect.set(prettyWeirdBot.position.x+19, prettyWeirdBot.position.y+19,26,26);
				if(obstacleRect.overlaps(botRect))
					prettyWeirdBot.isAlive = false; //kill the bot	
			}
			if(newBot.isAlive){
				botRect.set(newBot.position.x+19, newBot.position.y+19,26,26);
				if(obstacleRect.overlaps(botRect))
					newBot.isAlive = false; //kill the bot	
			}
			
		}
	}
	
	private void flameKillBomberman() {
		manRect.set(manPosition.x+19, manPosition.y+19, 26,26);
		for(Vector3 flame : flames) {
			obstacleRect.set(flame.x+8,flame.y+8,48,48);
			if(manRect.overlaps(obstacleRect)) {
				--lives;
				System.out.println(lives);
				if(lives == 0) {
					gameState = GameState.GAMEOVER;
					return;
				}
				invisibleTime = 3f;
				manPosition.x = 64;
				manPosition.y = 64;
				break;
			}
		}
	}
	
	
	//each flame exists 1 second in screen
	private void updateFlamesTimer(float deltaTime) {
		for(Vector3 flame : flames) {
			flame.z -= deltaTime; //update timer
			if(flame.z <= 0) { 
				flames.removeValue(flame, false);//remove flame
			}
		}
	}
	
	//check collision between bomberman and walls
	private boolean checkCollisionWithWalls(int x, int y) {
		//rectangle wraps bomberman for detecting collision with wall
		manRect.set(manPosition.x+19, manPosition.y+19, 26,26);
		//go through all walls (solid block)
		for(Point point : walls) {
			//check collision with specific wall having position x,y
			if(point.x == x && point.y == y) {
				//rectangle wraps wall for detecting collision with bomberman
				obstacleRect.set(point.x, point.y, 64, 64);
				if(manRect.overlaps(obstacleRect)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkCollisionWithBricks(int x, int y) {
		manRect.set(manPosition.x+19, manPosition.y+19, 26,26);
		for(Point point : bricks) {
			if(point.x == x && point.y == y) {
				obstacleRect.set(point.x, point.y, 64,64);
				if(manRect.overlaps(obstacleRect)) 
					return true;
			}
		}
		return false;
	}
	
	private boolean checkCollisionWithBombs(int x, int y) {
		manRect.set(manPosition.x+19, manPosition.y+19, 26,26);
		for(Vector3 bomb : bombs) {
			if(bomb.x == x && bomb.y == y) {
				obstacleRect.set(bomb.x+8, bomb.y+8, 48, 48);
				if(manRect.overlaps(obstacleRect))
					return true;
			}
		}
		return false;
		
	}
	
	private void botsKillBomberman() {
		if(checkCollisionWithBots()) {
			--lives;
			if(lives == 0) {
				gameState = GameState.GAMEOVER;
			}
			invisibleTime = 3f;
			manPosition.x = 64;
			manPosition.y = 64;
		}
	}
	
	private boolean checkCollisionWithBots() {
		manRect.set(manPosition.x+19, manPosition.y+19, 26,26);
		if(stupidBot.isAlive && manRect.overlaps(stupidBot.botRect)) return true;
		if(smartBot.isAlive && manRect.overlaps(smartBot.botRect)) return true;
		if(prettySmartBot.isAlive && manRect.overlaps(prettySmartBot.botRect)) return true;
		if(weirdBot.isAlive && manRect.overlaps(weirdBot.botRect)) return true;
		if(prettyWeirdBot.isAlive && manRect.overlaps(prettyWeirdBot.botRect)) return true;
		if(newBot.isAlive && manRect.overlaps(prettyWeirdBot.botRect)) return true;
		return false;
	}
	
	//ratio is 1/3
	//item is added randomly (could be flame power up, bomb power up, speed power up)
	private void addItem(int x, int y) {
		int randomVal = MathUtils.random(1, 4);
		if(randomVal == 1) {
			int kindOfItem = MathUtils.random(2, 3);
			switch(kindOfItem) {
			case BOMB: //bomb
				items.add(new Vector3(x,y,2.2f));
				break;
			case SPEED: //speed
				items.add(new Vector3(x,y,3.3f));
				break;
			}
		} else
			return;
	}
	
	private void checkCollisionWithItems() {
		manRect.set(manPosition.x+19, manPosition.y+19, 26,26);
		for(Vector3 item : items) {
			obstacleRect.set(item.x+16,item.y+16,32,32);
			if(manRect.overlaps(obstacleRect)) {
				int type = (int)item.z;
				switch(type) {
				case BOMB:
					if(numberOfBoms < MAX_NUMBER_OF_BOMBS)
						++numberOfBoms;
					break;
				case SPEED:
					if(manSpeed < MAX_SPEED) {
						++manSpeed;
						smartBot.speed = manSpeed-1;
						prettySmartBot.speed = manSpeed-1;
						weirdBot.speed = manSpeed-1;
						prettyWeirdBot.speed = manSpeed-1;
						stupidBot.speed = manSpeed-1;
						System.out.println(prettySmartBot.speed);
					}
					break;
				}
				items.removeValue(item, false);
			}
		}
	}
	
	private void checkForEndGame() {
		if(!stupidBot.isAlive && !smartBot.isAlive && !prettySmartBot.isAlive && !weirdBot.isAlive && !prettyWeirdBot.isAlive && !newBot.isAlive) {
			resetScene();
		}
	}
}
