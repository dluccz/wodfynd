package word.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import word.game.WordConnectGame;

import word.game.managers.AdManager;
import word.game.net.WordMeaningProvider;
import word.game.ui.calendar.Date;
import word.game.ui.calendar.DateUtil;
import word.game.util.AppExit;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 366;
		config.height = 650;
		config.y = 100;


		Map<String, WordMeaningProvider> provider = new HashMap<>();
		provider.put("en", new WordMeaningProviderDesktop());

		DateUtil dateUtil = new DateUtilImpl();

		WordConnectGame game = new WordConnectGame(new NetworkDesktop(), provider);

		game.dateUtil = dateUtil;
		game.appExit = new AppExit() {
			@Override
			public void exitApp() {
				Gdx.app.exit();
			}
		};


		new LwjglApplication(game, config);
	}

}
