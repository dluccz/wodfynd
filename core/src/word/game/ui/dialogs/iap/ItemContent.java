package word.game.ui.dialogs.iap;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemContent {

    public int coins;
    public int singleRandomReveal;
    public int multiRandomReveal;
    public int fingerReveal;
    public int rocketReveal;
    public boolean removeAds;
    TextureAtlas.AtlasRegion textureRegion;


    public ItemContent(){

    }


    public ItemContent(boolean removeAds, TextureAtlas.AtlasRegion textureRegion) {
        this.removeAds = removeAds;
        this.textureRegion = textureRegion;
    }



    public ItemContent(int coins, TextureAtlas.AtlasRegion textureRegion) {
        this.coins = coins;
        this.textureRegion = textureRegion;
    }



    public ItemContent(int coins, int singleRandomReveal, int multiRandomReveal, int fingerReveal, int rocketReveal, TextureAtlas.AtlasRegion textureRegion) {
        this.coins = coins;
        this.singleRandomReveal = singleRandomReveal;
        this.multiRandomReveal = multiRandomReveal;
        this.fingerReveal = fingerReveal;
        this.rocketReveal = rocketReveal;
        this.textureRegion = textureRegion;
    }




    @Override
    public String toString() {
        return "ItemContent{" +
                "coins=" + coins +
                ", singleRandomReveal=" + singleRandomReveal +
                ", multiRandomReveal=" + multiRandomReveal +
                ", fingerReveal=" + fingerReveal +
                ", rocketReveal=" + rocketReveal +
                ", removeAds=" + removeAds +
                '}';
    }
}
