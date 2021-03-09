package word.game.util;


import word.game.screens.BaseScreen;

public interface BackNavigator {

    void notifyNavigationController(BaseScreen screen);
    boolean navigateBack();
}
