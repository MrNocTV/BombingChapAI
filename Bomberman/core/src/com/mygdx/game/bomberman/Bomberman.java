package com.mygdx.game.bomberman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Bomberman extends Game {
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected Viewport viewport;
	protected AssetManager assetManager;
	public static final int SCREEN_WIDTH = 1160;
	public static final int SCREEN_HEIGHT = 640;
	protected boolean mute;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.position.set(SCREEN_WIDTH/2, SCREEN_HEIGHT/2, 0); //middle of screen
		viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera); //attach camera to viewport
		assetManager = new AssetManager();
		assetManager.load("bombermanassets.txt", TextureAtlas.class);
		assetManager.load("SolidBlock.png", Texture.class);
		assetManager.load("BackgroundTile.png", Texture.class);
		assetManager.load("default.fnt", BitmapFont.class);
		assetManager.load("gameover.png", Texture.class);
		assetManager.load("bomberman.mp3", Music.class);
		assetManager.load("explosion.wav", Sound.class);
		
		
		assetManager.finishLoading();
		setScreen(new MenuScreen(this)); //go into game screen
	}
	
	//called when the screen is resized
	@Override
	public void resize(int w, int h) { viewport.update(w, h); }
	
	//called when exit game
	@Override
	public void dispose() {
		batch.dispose();
		assetManager.dispose();
	}
}
