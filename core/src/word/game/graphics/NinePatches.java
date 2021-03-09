package word.game.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import word.game.managers.ResourceManager;

public class NinePatches {


    public static NinePatch dialog_bg;
    public static NinePatch round_rect_shadow;
    public static NinePatch rect;
    public static NinePatch play_r_up;
    public static NinePatch play_r_down;
    public static NinePatch ribbon;
    public static NinePatch iap_card1;
    public static NinePatch iap_card2;
    public static NinePatch btn_dialog_up;
    public static NinePatch btn_dialog_down;
    public static NinePatch btn_dialog_disabled;
    public static NinePatch preview;
    public static NinePatch board_cell, board_cell_solved;
    public static NinePatch hint_btn_cost_bg;
    public static NinePatch rrect;
    public static NinePatch tooltip;
    public static NinePatch flat_btn;
    public static NinePatch combo_bg;
    public static NinePatch ribbon2;


    public static void init(ResourceManager resourceManager){

        TextureAtlas atlas1     = resourceManager.get(ResourceManager.ATLAS_1, TextureAtlas.class);
        //if(Gdx.app.getType() == Application.ApplicationType.Android)
        play_r_up               = atlas1.createPatch("play_r_up");
        play_r_down             = atlas1.createPatch("play_r_down");
        dialog_bg               = atlas1.createPatch("dialog_bg");
        rect                    = atlas1.createPatch("rect");
        ribbon                  = atlas1.createPatch("ribbon");
        iap_card1               = atlas1.createPatch("iap_card1");
        iap_card2               = atlas1.createPatch("iap_card2");
        btn_dialog_up           = atlas1.createPatch("btn_dialog_up");
        btn_dialog_down         = atlas1.createPatch("btn_dialog_down");
        btn_dialog_disabled     = atlas1.createPatch("btn_dialog_disabled");
        rrect                   = atlas1.createPatch("rrect");
        tooltip                 = atlas1.createPatch("tooltip");
        flat_btn                = atlas1.createPatch("flat_btn");
        round_rect_shadow       = atlas1.createPatch("round_rect_shadow");
        combo_bg                = atlas1.createPatch("combo_bg");
        ribbon2                 = atlas1.createPatch("feedback");


        TextureAtlas atlas2     = resourceManager.get(ResourceManager.ATLAS_2, TextureAtlas.class);
        preview                 = atlas2.createPatch("preview");
        board_cell              = atlas2.createPatch("board_cell");
        board_cell_solved       = atlas2.createPatch("board_cell_solved");



        TextureAtlas atlas4     = resourceManager.get(ResourceManager.ATLAS_4, TextureAtlas.class);
        hint_btn_cost_bg        = atlas4.createPatch("hint_btn_cost_bg");


    }

}
