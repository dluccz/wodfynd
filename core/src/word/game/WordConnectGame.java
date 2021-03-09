package word.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.Map;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


import word.game.graphics.AtlasRegions;
import word.game.managers.AdManager;
import word.game.managers.ConnectionManager;
import word.game.managers.HintManager;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.net.Network;
import word.game.net.WordMeaningProvider;

import word.game.screens.BaseScreen;
import word.game.screens.SplashScreen;
import word.game.ui.calendar.DateUtil;
import word.game.ui.dialogs.iap.ShoppingProcessor;
import word.game.util.AppExit;
import word.game.util.RateUsLauncher;
import word.game.util.SupportRequest;

public class WordConnectGame extends Game {

	public ShoppingProcessor shoppingProcessor;
	private BaseScreen currentScreen;
	public ResourceManager resourceManager = new ResourceManager();
	public DateUtil dateUtil;
	public String version;
	public AdManager adManager;
	public AppExit appExit;
	public RateUsLauncher rateUsLauncher;
	public SupportRequest supportRequest;



	public WordConnectGame(Network network, Map<String, WordMeaningProvider> providerMap){
		ConnectionManager.network = network;
		LanguageManager.wordMeaningProviderMap = providerMap;
	}




	public void notificationReceived(int coins, String title, String text){
		int current = HintManager.getRemainingCoins();
		int newAmount = current + coins;
		HintManager.setCoinCount(newAmount);

		if(currentScreen != null) currentScreen.notificationReceived(newAmount, title, text);
	}




	@Override
	public void create() {
		setScreen(new SplashScreen(this));
	}




	@Override
	public void setScreen(Screen screen){
		if(currentScreen != null)
			currentScreen.dispose();

		currentScreen = (BaseScreen)screen;
		super.setScreen(screen);
	}


	@Override
	public void render() {
		super.render();
	}


	@Override
	public void dispose() {
		super.dispose();
		currentScreen.dispose();
		resourceManager.clear();
		resourceManager.dispose();
	}
















}
