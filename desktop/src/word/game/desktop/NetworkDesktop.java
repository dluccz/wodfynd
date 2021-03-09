package word.game.desktop;


import word.game.net.Network;

public class NetworkDesktop implements Network {


    @Override
    public boolean isConnected() {
        return true;
    }
}
