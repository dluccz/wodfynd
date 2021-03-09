package word.game;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class WordConnectFirebaseMessagingService extends FirebaseMessagingService {


    public static final String INTENT_RECEIVED_PUSH_MESSAGE = "INTENT_RECEIVED_PUSH_MESSAGE";
    public static final String KEY_COIN_AMOUNT = "coins";
    public static final String KEY_PUSH_MESSAGE_TITLE = "KEY_PUSH_MESSAGE_TITLE";
    public static final String KEY_PUSH_MESSAGE_TEXT = "KEY_PUSH_MESSAGE_TEXT";


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("fcm", "token: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        int coins = 0;


        if (remoteMessage.getData().size() > 0) {
            if(remoteMessage.getData().containsKey(KEY_COIN_AMOUNT)){

                String coinStr = remoteMessage.getData().get(KEY_COIN_AMOUNT);

                if(coinStr != null) {
                    coins = Integer.parseInt(coinStr);
                }
            }
        }

        String text = "";
        String title = "";

        if (remoteMessage.getNotification() != null) {
            text = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();
        }
        Log.d("fcm", "received notification, coins:" + coins+", title:" + title + ", text:" + text);

        Intent intent = new Intent(INTENT_RECEIVED_PUSH_MESSAGE);
        intent.putExtra(KEY_COIN_AMOUNT, coins);
        intent.putExtra(KEY_PUSH_MESSAGE_TITLE, title);
        intent.putExtra(KEY_PUSH_MESSAGE_TEXT, text);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);



    }
}
