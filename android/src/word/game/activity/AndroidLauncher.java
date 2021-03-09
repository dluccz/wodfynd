package word.game.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



import word.game.DateUtilImpl;
import word.game.NetworkAndroid;
import word.game.R;
import word.game.WordConnectFirebaseMessagingService;
import word.game.WordConnectGame;
import word.game.WordMeaningProviderAndroid;
import word.game.net.WordMeaningProvider;
import word.game.ui.calendar.Date;
import word.game.util.AppExit;
import word.game.util.RateUsLauncher;
import word.game.util.SupportRequest;

public class AndroidLauncher extends IAPActivity implements AppExit, RateUsLauncher, SupportRequest {

	private WordConnectGame game;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = getResources().getBoolean(R.bool.IMMERSIVE_MODE);

		Map<String, WordMeaningProvider> provider = new HashMap<>();
		provider.put("en", new WordMeaningProviderAndroid());



		//put you word meaning provider above

		DateUtilImpl dateUtil = new DateUtilImpl();
		dateUtil.context = this;

		game = new WordConnectGame(new NetworkAndroid(this), provider);
		game.dateUtil = dateUtil;
		game.shoppingProcessor = androidShoppingProcessor;
		game.adManager = this;
		game.appExit = this;
		game.rateUsLauncher = this;
		game.supportRequest = this;

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			game.version = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		initialize(game, config);

		LocalBroadcastManager.getInstance(this).registerReceiver(mCoinsMessageReceiver, new IntentFilter(WordConnectFirebaseMessagingService.INTENT_RECEIVED_PUSH_MESSAGE));


		fcm();

	}






	private void fcm(){
		Bundle bundle = getIntent().getExtras();

		if(bundle != null){
			int coinCount = 0;


			if(bundle.containsKey(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) || bundle.containsKey(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT)){

				if(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) instanceof String)
					coinCount = Integer.parseInt(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT).toString());
				else if(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) instanceof Object)
					coinCount = (int)bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT);
				else if(bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT) instanceof Integer)
					coinCount = ((Integer) bundle.get(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT)).intValue();


				Log.d("fcm", "Received coins from intent " + coinCount);

			}

			String text = "";
			String title = "";

			if(bundle.containsKey(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT)){
				Object rawText = bundle.get(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT);
				if(rawText != null) text = rawText.toString();

				Object rawTitle = bundle.get(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TITLE);
				if(rawTitle != null) rawTitle.toString();
			}

			notifyUserAboutIncomingPushMessage(coinCount, title, text);

		}




		FirebaseMessaging.getInstance().subscribeToTopic("coin_topic")
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						String msg = "subscribe successful";
						if (!task.isSuccessful()) {
							msg = "subscribe failed";
						}
						Log.d("fcm", msg);

					}
				});

	}






	@Override
	public void exitApp() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onBackPressed();
			}
		});

	}



	@Override
	public void launch() {
		String param = getPackageName();
		Uri uri = Uri.parse("market://details?id=" + param);
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		// To count with Play market backstack, After pressing back button,
		// to taken back to our application, we need to add following flags to intent.
		goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + param)));
		}
	}






	@Override
	public void sendSupportEmail(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request for " + getString(R.string.app_name));
		intent.putExtra(Intent.EXTRA_TEXT, getDeviceInfo());
		startActivity(Intent.createChooser(intent, "Send e-mail..."));
	}





	public String getDeviceInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n");
		sb.append("Please type your request above");
		sb.append("\n");
		sb.append("Brand: ");
		sb.append(Build.BRAND);
		sb.append("\n");
		sb.append("Model: ");
		sb.append(Build.MODEL);
		sb.append("\n");
		sb.append("SDK: ");
		sb.append(Build.VERSION.SDK_INT);
		sb.append("\n");

		Locale locale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			locale = Resources.getSystem().getConfiguration().getLocales().get(0);
		} else {
			//noinspection deprecation
			locale = Resources.getSystem().getConfiguration().locale;
		}

		sb.append("Locale: ");
		sb.append(locale.getLanguage() + "-" + locale.getCountry());
		sb.append("\n");
		sb.append("App version: ");

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			sb.append(pInfo.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}


		return sb.toString();
	}





	private BroadcastReceiver mCoinsMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			int coins = intent.getIntExtra(WordConnectFirebaseMessagingService.KEY_COIN_AMOUNT, 0);
			String title = intent.getStringExtra(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TITLE);
			String text = intent.getStringExtra(WordConnectFirebaseMessagingService.KEY_PUSH_MESSAGE_TEXT);
			notifyUserAboutIncomingPushMessage(coins, title, text);
		}
	};



	private void notifyUserAboutIncomingPushMessage(int coins, String title, String text){
		game.notificationReceived(coins, title, text);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mCoinsMessageReceiver);
	}
}
