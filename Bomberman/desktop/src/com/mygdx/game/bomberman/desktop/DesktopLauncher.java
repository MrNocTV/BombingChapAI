
package com.mygdx.game.bomberman.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.bomberman.Bomberman;

public class DesktopLauncher {
	public static void
	main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//set screen width and height
		config.width  = Bomberman.SCREEN_WIDTH;
		
		config.height = Bomberman.SCREEN_HEIGHT;
		//set screen title
		config.title = "Bomberman";
		new LwjglApplication(new Bomberman(), config);
	}
}
