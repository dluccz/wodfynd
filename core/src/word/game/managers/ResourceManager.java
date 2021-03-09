package word.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import word.game.config.UIConfig;

import static word.game.model.GameData.findFirstIncompleteLevel;


public class ResourceManager implements Disposable {

    //atlas files
    public static String ATLAS_1                    = "textures/atlas1.atlas";
    public static String ATLAS_2                    = "textures/atlas2.atlas";
    public static String ATLAS_3                    = "textures/atlas3.atlas";
    public static String ATLAS_4                    = "textures/atlas4.atlas";

    //background images
    public static String introBackground, gameBackground;

    //fonts
    public static String fontSemiBold               = "fonts/montserrat_semibold_ui.fnt";
    public static String fontSemiBoldShadow         = "fonts/montserrat_semibold_ui_shadow.fnt";
    public static String fontBlack                  = "fonts/montserrat_black.fnt";
    public static String fontBoardAndDialFont       = "fonts/montserrat_semibold_board.fnt";


    //sound effects
    public static final String SFX_BLAST            = "sfx/blast.mp3";
    public static final String SFX_HINT             = "sfx/hint.mp3";
    public static final String SFX_MONSTER_JUMP     = "sfx/monster_jump.mp3";
    public static final String SFX_LEVEL_END        = "sfx/level_end.mp3";
    public static final String SFX_ROCKET           = "sfx/rocket.mp3";
    public static final String SFX_SELECT_1         = "sfx/SELECT_1.mp3";
    public static final String SFX_SELECT_2         = "sfx/SELECT_2.mp3";
    public static final String SFX_SELECT_3         = "sfx/SELECT_3.mp3";
    public static final String SFX_SELECT_4         = "sfx/SELECT_4.mp3";
    public static final String SFX_SELECT_5         = "sfx/SELECT_5.mp3";
    public static final String SFX_SELECT_6         = "sfx/SELECT_6.mp3";
    public static final String SFX_SELECT_7         = "sfx/SELECT_7.mp3";
    public static final String SFX_SELECT_8         = "sfx/SELECT_8.mp3";
    public static final String SFX_SPIN_CLICK       = "sfx/spin_click.mp3";
    public static final String SFX_SUCCESS          = "sfx/success.mp3";
    public static final String SFX_WHEEL_SPIN       = "sfx/wheel_spin.mp3";
    public static final String SFX_FOUND_BEFORE     = "sfx/found_before.mp3";
    public static final String SFX_BONUS_WORD       = "sfx/bonus_word.mp3";
    public static final String SFX_WRONG            = "sfx/wrong.mp3";
    public static final String SFX_SHUFFLE          = "sfx/shuffle.mp3";
    public static final String SFX_NOTIFICATION     = "sfx/notification.mp3";
    public static final String SFX_HIT_BOOSTER      = "sfx/hit_booster.mp3";

    //shaders
    public static final String SHADER_LINE          = "shader/line.frag";
    public static final String SHADER_DIAL          = "shader/dial.fsh";
    public static final String SHADER_OVERLAY       = "shader/overlay.fsh";
    public static final String SHADER_VERTEX        = "shader/vertex.vsh";




    /***** DO NOT EDIT ANYTHING BELOW *****/

    private final AssetManager manager = new AssetManager();


    public static String LOCALE_PROPERTIES_FILE;

    public static float scaleFactor = 1.0f;
    public static String resolvedRes;




    public static void init(){
        if(resolvedRes != null) return;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float largeResolutionWidth = 1536;
        float largeResolutionHeight = 2048;

        float mediumResolutionWidth = 768;
        float mediumResolutionHeight = 1024;

        float smallResolutionWidth = 320;
        float smallResolutionHeight = 480;


        if(screenHeight > 1024){
            resolvedRes = "_hdr";
            scaleFactor = Math.min(screenHeight / largeResolutionHeight, screenWidth / largeResolutionWidth);
        }else if(screenHeight > 480){
            resolvedRes = "_hd";
            scaleFactor = Math.min(screenHeight / mediumResolutionHeight, screenWidth / mediumResolutionWidth);
        }else {
            resolvedRes = "_sd";
            scaleFactor = Math.min(screenHeight / smallResolutionHeight, screenWidth / smallResolutionWidth);
        }
        //Gdx.app.log("resolution", resolvedRes + ", " + scaleFactor);
    }





    public static String resolveResolutionAwarePath(String fileName){

        if(resolvedRes == null)
            init();

        if(fileName.toLowerCase().contains(resolvedRes + ".")) {
            return fileName;
        }

        String[] fileNameParts = fileName.split("\\.");
        return fileNameParts[0] + resolvedRes + "." + fileNameParts[1];
    }



    public void load(String fileName, Class type){
        manager.load(fileName, type);
    }

    public void load(String fileName, Class type, AssetLoaderParameters parameter){
        manager.load(fileName, type, parameter);
    }

    public synchronized boolean update(){
        return manager.update();
    }



    public boolean update (int millis){
        return manager.update(millis);
    }

    public synchronized float getProgress(){
        return manager.getProgress();
    }


    public synchronized boolean isFinished (){
        return manager.isFinished();
    }

    public synchronized <T> T get (String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }

    public <T> Array<T> getAll(java.lang.Class<T> type,  Array<T> out){
        return manager.getAll(type, out);
    }


    public void setLoader (Class type, AssetLoader loader){
        manager.setLoader(type, loader);
    }

    public void finishLoading(){
        manager.finishLoading();
    }

    public void finishLoadingAsset(String fileName){
        manager.finishLoadingAsset(fileName);
    }


    public boolean contains(String fileName){
        return manager.contains(fileName);
    }


    public void unload(String fileName){
        manager.unload(fileName);
    }


    public void clear(){
        manager.clear();
    }

    @Override
    public void dispose() {
        manager.dispose();
    }




}
