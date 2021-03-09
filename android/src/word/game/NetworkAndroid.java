package word.game;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import word.game.net.Network;


public class NetworkAndroid implements Network {

    private Context context;

    public NetworkAndroid(Context context){
        this.context = context;
    }


    @Override
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
