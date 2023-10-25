package word.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import word.game.WordConnectGame;
import word.game.actions.AngledBezierToAction;
import word.game.actions.BezierToAction;
import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.config.SoundConfig;
import word.game.config.UIConfig;
import word.game.controllers.GameController;
import word.game.events.ShowDictionaryEvent;
import word.game.graphics.AtlasRegions;
import word.game.graphics.NinePatches;
import word.game.pool.Pools;

import word.game.ui.Smoke;
import word.game.ui.dial.DialButton;
import word.game.ui.dialogs.AlertDialog;
import word.game.ui.dialogs.BaseDialog;
import word.game.ui.dialogs.RemoveAdsDialog;
import word.game.ui.dialogs.bonus_words.BonusWordsCompleteDialog;
import word.game.ui.dialogs.bonus_words.BonusWordsIncompleteDialog;
import word.game.ui.hint.HintComet;
import word.game.ui.hint.IdleTimer;
import word.game.ui.hint.RewardedAdAnimation;
import word.game.ui.hint.RewardedVideoButton;
import word.game.ui.hint.Rocket;
import word.game.managers.ConnectionManager;
import word.game.managers.HintManager;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.BoardModel;
import word.game.model.Constants;
import word.game.model.GameData;
import word.game.model.Level;
import word.game.model.Word;
import word.game.ui.Feedback;
import word.game.ui.hint.HintButton;
import word.game.ui.LevelEndView;
import word.game.ui.board.CoinRotateAnim;
import word.game.ui.SideComboDisplay;
import word.game.ui.board.Ufo;
import word.game.ui.board.BoardOverlay;
import word.game.ui.board.BoardView;
import word.game.ui.board.CellView;
import word.game.ui.board.Monster;
import word.game.ui.dial.Dial;
import word.game.ui.dial.DialAnimationContainer;
import word.game.ui.dialogs.BombDialog;
import word.game.ui.dialogs.ConfirmDialog;
import word.game.ui.dialogs.DictionaryDialog;
import word.game.ui.dialogs.iap.ShoppingDialog;
import word.game.ui.dialogs.wheel.RewardRevealType;
import word.game.ui.ExtraWordsButton;
import word.game.ui.preview.Preview;
import word.game.ui.top_panel.CoinView;
import word.game.ui.tutorial.Tutorial;
import word.game.ui.tutorial.TutorialBooster;
import word.game.ui.tutorial.TutorialDial;
import word.game.ui.tutorial.TutorialHintButton;

import word.game.util.BackNavigator;
import word.game.util.CameraShaker;
import word.game.util.RewardedVideoCloseCallback;
import word.game.util.UiUtil;


public class GameScreen extends BaseScreen implements ShowDictionaryEvent {



    private Preview preview;
    public GameController gameController;
    public Dial dial;
    private BoardView boardView;
    public DialAnimationContainer dialAnimationContainer;
    private LevelEndView levelEndView;

    private Rocket rocket;
    private BombDialog bombDialog;
    public AlertDialog bombBlastDialog;


    public ExtraWordsButton extraWordsButton;

    private ImageButton shuffleButton;
    private BoardOverlay boardOverlay;
    private Feedback feedback;

    private DictionaryDialog dictionaryDialog;
    private BonusWordsIncompleteDialog bonusWordsIncompleteDialog;
    private BonusWordsCompleteDialog bonusWordsCompleteDialog;
    public SideComboDisplay sideComboDisplay;

    private Smoke smoke;
    private Ufo ufo;
    public boolean offeredBoosterInThisLevel;
    public int nextBoosterType;
    private Label goldPackBubbleLabel;


    private RemoveAdsDialog removeAdsDialog;
    public Image comboLight;;
    public CameraShaker cameraShaker;

    private DelayAction comboLightDelay;
    private SequenceAction comboLightSequence;
    private RunnableAction comboLightRunnableAction;
    public int tempComboReward;



    public GameScreen(WordConnectGame wordConnectGame) {
        super(wordConnectGame);
    }




    @Override
    public void show() {

        super.show();

        setTopPanel();
        topPanel.setY(stage.getHeight());

        topPanel.topComboDisplay.setGameScreen(this);

        dialAnimationContainer = new DialAnimationContainer(wordConnectGame.resourceManager);
        dialAnimationContainer.setVisible(false);
        stage.addActor(dialAnimationContainer);

        cameraShaker = new CameraShaker();

        gameController = new GameController();
        gameController.setGameScreen(this);

    }



    public void setBackgroundImage(int level){
        setBackground(UIConfig.GAME_SCREEN_BACKGROUND_COLOR, UIConfig.getGameScreenBackgroundImage(level));
    }





    private void setComboView(){
        if(sideComboDisplay == null) {
            sideComboDisplay = new SideComboDisplay(this);
            sideComboDisplay.setVisible(false);
            sideComboDisplay.setX(-sideComboDisplay.getWidth());
            sideComboDisplay.setY(preview.getY());
            stage.addActor(sideComboDisplay);
            if(bombDialog != null) bombDialog.setZIndex(sideComboDisplay.getZIndex() + 1);
        }
    }



    private void resumeCombo(){
        setComboView();

        int comboCount = GameData.getComboCount();
        if(comboCount > 0) {

            gameController.level.comboCount = comboCount;
            gameController.setTempComboCount(comboCount);
            comboCount--;//display combo amount is actually one less

            if(comboCount > 0) {
                if(sideComboDisplay != null) {
                    if(bombBlastDialog != null) bombBlastDialog.setZIndex(sideComboDisplay.getZIndex() + 1);
                    sideComboDisplay.setComboStateSilent(comboCount);
                }
                if(dialAnimationContainer != null) dialAnimationContainer.setAutoIncrease(comboCount);
            }
        }

        gameController.setComboAnimatedWordCount(GameData.getSolvedWords());

        tempComboReward = GameData.getComboReward();
        if(tempComboReward > 0) topPanel.topComboDisplay.setComboCountWithoutAnim(tempComboReward);
    }



    public void showComboFeedback(){
        if(feedback != null && feedback.getParent() != null) return;
        if(feedback == null) createFeedback();
        feedback.setY(preview.getY() + preview.getHeight() * 0.5f);
        feedback.setZIndex(Math.max(dial.getZIndex(), boardView.getZIndex()) + 1);
        if(feedback.getParent() == null) stage.addActor(feedback);
        feedback.show(UIConfig.FEEDBACK_RIBBON_COLOR);
    }







    public void runSmoke(int count){
        if(count > 0) {
            if(smoke == null) createSmoke();
            smoke.blast(ConfigProcessor.getLevelColor(gameController.level.index));
        }
    }





    public void comboShaderAnim(final Runnable callback, final int rewardToIncrement){

        if(comboLightDelay == null) comboLightDelay = new DelayAction();
        else comboLightDelay.reset();
        comboLightDelay.setDuration(0.3f);

        if(comboLightRunnableAction == null) comboLightRunnableAction = new RunnableAction();
        else comboLightRunnableAction.reset();

        if(comboLightSequence == null) comboLightSequence = new SequenceAction();
        else comboLightSequence.reset();

        comboLightRunnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                animateComboShader(callback, rewardToIncrement);
            }
        });


        comboLightSequence.addAction(comboLightDelay);
        comboLightSequence.addAction(comboLightRunnableAction);
        stage.addAction(comboLightSequence);



    }







    public void resetCombo(Runnable callback){
        if(dialAnimationContainer != null) dialAnimationContainer.setStage(0);
        gameController.level.comboCount = 0;
        GameData.saveComboCount(0);
        //tempComboReward = GameData.getComboReward();
        //topPanel.topComboDisplay.setComboCountWithoutAnim(tempComboReward);

        if(sideComboDisplay != null) sideComboDisplay.jump(callback);
        else callback.run();
    }



    private void createFeedback(){
        feedback = new Feedback(this);
        feedback.setX((stage.getWidth() - feedback.getWidth()) * 0.5f);
        feedback.setZIndex(Math.max(dial.getZIndex(), boardView.getZIndex()) + 1);
    }



    public int incrementer;



    private void animateComboShader(final Runnable callback, final int rewardToIncrement){

        if(comboLight == null){
           comboLight = new Image(AtlasRegions.star_particle);
            comboLight.setVisible(false);
            stage.addActor(comboLight);
        }



        float width = stage.getWidth();
        float halfLight = comboLight.getWidth() * 0.5f;

        float duration = 0.7f;

        float startX;
        float startY;
        float control1X;
        float control1Y;
        float control2X;
        float control2Y;
        float endX;
        float endY;


        startX = width * 0.05f;
        startY = sideComboDisplay.getY() + (sideComboDisplay.getHeight() - comboLight.getHeight()) * 0.5f;

        endX = width * 0.5f - halfLight;
        endY = topPanel.getY() + topPanel.getHeight() * 0.5f;

        if(MathUtils.randomBoolean()) {
            control1X = width * 0.25f;
            control1Y = sideComboDisplay.getY();

            control2X = width * 0.5f;
            control2Y = sideComboDisplay.getY() + (endY - sideComboDisplay.getY()) * 0.5f;
        }else{
            control1X = startX;
            control1Y = startY + (endY - startY) * 0.5f;

            control2X = startX + (endX - startX) * 0.5f;
            control2Y = endY;
        }

        comboLight.setColor(UIConfig.SIDE_COMBO_STAR_COLOR);


        BezierToAction bezierToAction = new BezierToAction();
        bezierToAction.setDuration(duration);
        bezierToAction.setInterpolation(word.game.actions.Interpolation.cubicIn);
        bezierToAction.setStartPosition(startX, startY);
        bezierToAction.setPointA(control1X, control1Y);
        bezierToAction.setPointB(control2X, control2Y);
        bezierToAction.setEndPosition(endX, endY);



        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                comboLight.setVisible(false);
                tempComboReward += rewardToIncrement;
                GameData.saveComboReward(tempComboReward);
                topPanel.topComboDisplay.setComboCount(tempComboReward, callback);
            }
        });

        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(bezierToAction);
        sequenceAction.addAction(runnableAction);


        comboLight.setPosition(startX, startY);
        comboLight.setVisible(true);
        comboLight.addAction(sequenceAction);


    }






    private void determineBooster(){

        offeredBoosterInThisLevel = false;

        if(gameController.level.index < GameConfig.BOOSTERS_START_LEVEL) {
            return;
        }




        if((gameController.level.index - GameConfig.BOOSTERS_START_LEVEL) % (GameConfig.OFFER_BOOSTER_EVERY_N_LEVEL) != 0){
            return;
        }

        boolean atLeastOneBoosterEnabled = false;

        for(Integer i : GameConfig.ENABLED_BOOSTERS.keySet()){
            if(GameConfig.ENABLED_BOOSTERS.get(i)) {
                atLeastOneBoosterEnabled = true;
                break;
            }
        }


        if(!atLeastOneBoosterEnabled) {
            return;
        }


        nextBoosterType = GameData.getLastBoosterType();
        if(nextBoosterType == GameConfig.ENABLED_BOOSTERS.size() - 1)
            nextBoosterType = -1;

        while(true){
            nextBoosterType++;
            if(GameConfig.ENABLED_BOOSTERS.get(nextBoosterType))
                break;

            if(nextBoosterType == GameConfig.ENABLED_BOOSTERS.size() - 1)
                nextBoosterType = -1;
        }


        if(nextBoosterType == GameConfig.BOOSTER_UFO)
            determineUfo();
        else if(nextBoosterType == GameConfig.BOOSTER_BOMB)
            setBomb();
        else if(nextBoosterType == GameConfig.BOOSTER_GOLD_PACK)
            setGoldPack();
        else if(nextBoosterType == GameConfig.BOOSTER_MONSTER)
            setMonster();



        offeredBoosterInThisLevel = true;

    }






    private void determineUfo(){
        if(!GameData.hasUfoBeenConsumedInThisLevel()) ufo = new Ufo(this);
    }





    private void triggerUfoFlyIn(){
        if(ufo == null) return;
        CellView randomCell = boardView.getSingleRandomCell(null);
        if(randomCell != null) {
            randomCell.cellData.setState(Constants.TILE_STATE_UFO);
            randomCell.ufo = ufo;
            stage.getRoot().setTouchable(Touchable.disabled);
            ufo.flyIn(randomCell, ufoCallback);
        }
    }



    public void setUfoSuccess(CellView cellView){
        ufo.hide();

        GameData.setUfoHasBeenConsumedInThisLevel();
        Vector2 pos = cellView.localToActorCoordinates(topPanel.coinView, new Vector2(cellView.getWidth() * 0.5f, cellView.getHeight() * 0.5f));
        int currentCoins = HintManager.getRemainingCoins();
        HintManager.setCoinCount(currentCoins + GameConfig.NUMBER_OF_COINS_EARNED_FOR_HITTING_UFO);
        topPanel.coinView.createCoinAnimation(GameConfig.NUMBER_OF_COINS_EARNED_FOR_HITTING_UFO, pos.x, pos.y, null);
    }








    private Ufo.UfoCallback ufoCallback = new Ufo.UfoCallback() {

        @Override
        public void timeIsUp(CellView cellView) {
            cellView.ufo = null;
        }

        @Override
        public void flyInFinished() {
            stage.getRoot().setTouchable(Touchable.enabled);
            if(UIConfig.INTERACTIVE_TUTORIAL_ENABLED && !GameData.isUfoTutorialDisplayed()){
                tutorial = new TutorialBooster(GameScreen.this);
                tutorial.paddingX = 1.2f;
                stage.addActor(tutorial);
                tutorial.highlightActor(ufo, Tutorial.Shape.DISC);
                final TutorialBooster tutorialBooster = (TutorialBooster) tutorial;
                tutorial.showText(LanguageManager.get("ufo_tutorial"));
                tutorialBooster.setGotIt(LanguageManager.get("got_it"));
                tutorial.fadeIn(null);
                tutorial.tutorialSaver = new Tutorial.TutorialSaver() {
                    @Override
                    public void save() {
                        ((TutorialBooster) tutorial).gotit.setDisabled(true);
                        GameData.setUfoTutorialComplete();
                        ufo.startChrono();
                    }
                };
                tutorialBooster.setGotItListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        tutorialBooster.tutorialSaver.save();
                        tutorial.fadeOut(tutorialRemover, true);
                    }
                });
            }else{
                ufo.startChrono();
            }

        }


        @Override
        public void deleteUfo(){
            ufo.remove();
            CellView targetCell = ufo.targetCell;
            if(targetCell != null){
                targetCell.ufo = null;
            }

            if(boardView.ufo != null){
                boardView.ufo = null;
            }

            ufo = null;
        }


    };








    private void setBomb(){

        if(!GameData.hasBombBeenConsumedInThisLevel()){

             if(boardView.bomb == null) {
                 CellView randomCell = boardView.getSingleRandomCell(null);

                 if (randomCell != null) {
                     randomCell.cellData.setState(Constants.TILE_STATE_BOMBED);
                     randomCell.updateStateView();
                     boardView.bomb.setDefaultCount(gameController.level.getBoardModel().getAllWords(false).size );
                     GameData.saveTileState(randomCell.cellData.getX(), randomCell.cellData.getY(), Constants.TILE_STATE_BOMBED);
                 }
             }else {
                 int remainingCount = GameData.getNumberOfBombMoves();
                 boardView.bomb.setDefaultCount(remainingCount);
                 if (remainingCount == 0) {
                     Action delay = Actions.delay(0.5f);
                     Action run = Actions.run(new Runnable() {
                         @Override
                         public void run() {

                             if(wordConnectGame.adManager == null){
                                 onBombExplosionFinished.run();
                             }else{
                                 if(wordConnectGame.adManager.isRewardedAdEnabledToEarnMoves())
                                     showBombDialog();
                                 else
                                     onBombExplosionFinished.run();
                             }

                         }
                     });
                     stage.addAction(new SequenceAction(delay, run));
                 }

             }

             if(boardView.bomb != null){
                 boardView.bomb.getParent().setZIndex(gameController.level.getBoardModel().height * gameController.level.getBoardModel().width - 1);
             }
         }
    }




    public void setBombSuccess(CellView cellView){
        GameData.removeNumberOfBombMoves();
        GameData.setBombHasBeenConsumedInThisLevel();
        boardView.bomb.setDefused();
        if(feedback == null) createFeedback();
        if(feedback.getParent() == null) stage.addActor(feedback);
        feedback.setY(preview.getY() + preview.getHeight());

        Action delay = Actions.delay(0.5f);
        Action run = Actions.run(new Runnable() {
            @Override
            public void run() {
                feedback.show(UIConfig.BOMB_DEFUSED_RIBBON_COLOR, LanguageManager.get("bomb_defused"));
            }
        });
        feedback.addAction(new SequenceAction(delay, run));

    }



    public Runnable bombDeleter = new Runnable() {
        @Override
        public void run() {

            CellView targetCell = boardView.bomb.targetCell;

            if(targetCell.bomb != null){
                targetCell.bomb.remove();
                targetCell.bomb = null;
            }

            if(boardView.bomb != null){
                boardView.bomb.remove();
                boardView.bomb = null;
            }

        }
    };




    private RewardedVideoCloseCallback rewardedVideoForMovesClosed = new RewardedVideoCloseCallback() {
        @Override
        public void closed(boolean earnedReward) {
            if(earnedReward){
                gameController.bombDialogWillAppear = false;
                boardView.bomb.setDefaultCount(GameConfig.EXTRA_BOMB_MOVES_FOR_WATCHING_AD);
            } else showBombDialog();
        }
    };



    public void showBombDialog(){

        boardView.bomb.clearActions();
        boardView.bomb.setCount(0);


        final BombDialog.BombDecision decision = new BombDialog.BombDecision() {
            @Override
            public void bombAction(boolean watch) {
                if(watch) {
                    if(!wordConnectGame.adManager.isRewardedAdLoaded())
                        showToast(LanguageManager.get("no_video"));
                    else
                        wordConnectGame.adManager.showRewardedAd(rewardedVideoForMovesClosed);
                }else {
                    explodeBomb();
                }
            }
        };

        if(bombDialog == null) bombDialog = new BombDialog(stage.getWidth(), stage.getHeight(), GameScreen.this);
        else bombDialog.setVisible(true);

        bombDialog.setBombDecision(decision);
        stage.getRoot().addActorAt(zIndexDialog, bombDialog);
        bombDialog.show();

    }



    public void explodeBomb(){
        if(!ConfigProcessor.muted) {
            Sound blast = wordConnectGame.resourceManager.get(ResourceManager.SFX_BLAST, Sound.class);
            blast.play(SoundConfig.SFX_BOMB_VOLUME);
        }
        gameController.revertGameDataToPreBombState();
        boardView.bomb.explode(onBombExplosionFinished);
        boardView.addAction(Actions.fadeOut(0.5f));
    }




    private Runnable onBombExplosionFinished = new Runnable() {
        @Override
        public void run() {
            stage.getRoot().setTouchable(Touchable.disabled);
            bombBlastDialog = new AlertDialog(
                    stage.getWidth(),
                    stage.getHeight(),
                    GameScreen.this,
                    LanguageManager.get("failed_bomb"),
                    LanguageManager.get("blasted"),
                    LanguageManager.get("retry_bomb"),
                    retryAfterBombExplosion
            );
            bombBlastDialog.setDialogId(Constants.ALERT_DIALOG_BOMB_BLAST);
            stage.addActor(bombBlastDialog);
            bombBlastDialog.show();
        }
    };




    private Runnable retryAfterBombExplosion = new Runnable() {

        @Override
        public void run() {
            bombDeleter.run();
            gameController.clearLevelRelatedData(false, false);
            GameData.setNumberOfBombMoves(gameController.level.getBoardModel().getAllWords(false).size);
            if(dialAnimationContainer != null) dialAnimationContainer.getColor().a = 0f;
            gameController.prepareNextLevel();
        }
    };






    private void setGoldPack(){

        if(!GameData.hasGoldPackBeenConsumedInThisLevel()) {

            if (boardView.goldPack == null) {
                CellView randomCell = boardView.getSingleRandomCell(null);

                if (randomCell != null) {
                    randomCell.cellData.setState(Constants.TILE_STATE_GOLD_PACKED);
                    randomCell.updateStateView();
                    boardView.goldPack.setCount(gameController.level.getBoardModel().getAllWords(false).size);
                    GameData.saveTileState(randomCell.cellData.getX(), randomCell.cellData.getY(), Constants.TILE_STATE_GOLD_PACKED);
                }
            } else {
                int remainingCount = GameData.getNumberOfGoldPackMoves();
                boardView.goldPack.setCount(remainingCount);
            }
        }
    }






    public void setGoldPackSuccess(CellView cellView){

        int rewardCoinCount = boardView.goldPack.getCount();
        Label.LabelStyle wordTitlelabelStyle = new Label.LabelStyle(wordConnectGame.resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), Color.WHITE);
        goldPackBubbleLabel = new Label("+" + rewardCoinCount, wordTitlelabelStyle);
        goldPackBubbleLabel.setOrigin(Align.bottomLeft);
        goldPackBubbleLabel.setX(cellView.getX() + (cellView.getWidth() - goldPackBubbleLabel.getWidth()) * 0.5f);
        goldPackBubbleLabel.setY(cellView.getY() + cellView.getHeight());
        boardView.addActor(goldPackBubbleLabel);

        float time = 0.7f;

        Action moveTo = Actions.moveBy(0, cellView.getHeight() * 1.5f, time, Interpolation.sineOut);
        goldPackBubbleLabel.addAction(moveTo);

        Action fadeOut = Actions.fadeOut(time * 0.33f);
        Action bubbleRun = Actions.run(new Runnable() {
            @Override
            public void run() {
                goldPackBubbleLabel.remove();
                goldPackBubbleLabel = null;
            }
        });


        SequenceAction sequenceAction = new SequenceAction(Actions.delay(time * 0.66f), fadeOut, bubbleRun);
        goldPackBubbleLabel.addAction(sequenceAction);
        boardView.goldPack.hit = true;
        cellView.goldPack.hit = true;
        boardView.goldPack.remove();
        boardView.goldPack = null;


        if(cellView.goldPack != null){
            cellView.goldPack.remove();
        }
        cellView.goldPack = null;
        setCoinAnim(cellView, rewardCoinCount);
    }




    private void setCoinAnim(CellView cellView, int rewardCoinCount){
        int currentCoins = HintManager.getRemainingCoins();
        HintManager.setCoinCount(currentCoins + rewardCoinCount);
        Vector2 pos = cellView.localToActorCoordinates(topPanel.coinView, new Vector2(cellView.getWidth() * 0.5f, cellView.getHeight() * 0.5f));
        topPanel.coinView.createCoinAnimation(rewardCoinCount, pos.x, pos.y, null);
    }




    private void setMonster(){

        if(!GameData.hasMonsterBeenConsumedInThisLevel()) {
            if (boardView.monster == null) {
                CellView randomCell = boardView.getSingleRandomCell(null);

                if (randomCell != null) {
                    randomCell.cellData.setState(Constants.TILE_STATE_MONSTER);
                    randomCell.updateStateView();
                    GameData.saveTileState(randomCell.cellData.getX(), randomCell.cellData.getY(), Constants.TILE_STATE_MONSTER);
                }
            } else {
                Monster monster = boardView.monster;
                monster.setX(monster.targetCell.getX() + (monster.targetCell.getWidth() * 0.5f - monster.getWidth() * monster.getScaleX() * 0.407051f));
                monster.setY(monster.targetCell.getY() + monster.targetCell.getHeight() * 0.1f);
                monster.setZIndex(boardView.getChildren().size);
            }

        }
    }




    public void monsterJump(){
        //CellView randomCell = null;



        /*while(randomCell == null || randomCell.equals(boardView.monster.targetCell)){
            randomCell = boardView.getSingleRandomCell();
        }*/

        CellView randomCell = boardView.getSingleRandomCell(boardView.monster.targetCell);







        boardView.monster.targetCell.hasMonster = false;

        if(boardView.monster.targetCell.cellData.getState() == Constants.TILE_STATE_MONSTER) {
            boardView.monster.targetCell.cellData.setState(Constants.TILE_STATE_DEFAULT);
            GameData.removeTileState(boardView.monster.targetCell.cellData.getX(), boardView.monster.targetCell.cellData.getY());
        }


        if(randomCell == null){
            boardView.monster.remove();
            boardView.monster = null;
            return;//monster'ı ortadan kaldır
        }


        boardView.monster.targetCell = randomCell;
        randomCell.hasMonster = true;
        randomCell.cellData.setState(Constants.TILE_STATE_MONSTER);
        GameData.saveTileState(randomCell.cellData.getX(), randomCell.cellData.getY(), Constants.TILE_STATE_MONSTER);
        randomCell.updateStateView();


        float srcX = boardView.monster.getX();
        float srcY = boardView.monster.getY();
        float dstX = randomCell.getX() + (randomCell.getWidth() * 0.5f - boardView.monster.getWidth() * boardView.monster.getScaleX() * 0.407051f);
        float dstY = randomCell.getY() + randomCell.getHeight() * 0.1f;

        boardView.monster.jump(randomCell, srcX, srcY, dstX, dstY);
    }



    public void setMonsterSuccess(final CellView monsterCell){

        Action fadeOut = Actions.fadeOut(0.1f);
        Action run = Actions.run(new Runnable() {

            @Override
            public void run() {

                if(boardView.monster != null) {
                    boardView.monster.remove();
                    boardView.monster = null;
                }


            }
        });

        boardView.monster.addAction(new SequenceAction(fadeOut, run));
        monsterCell.hasMonster = false;





        Array<CellView> cells = boardView.selectMultipleCellsForHint(GameConfig.NUM_OF_TILES_TO_SET_COIN_AFTER_KILLING_THE_MONSTER);

        if(cells.size == 0){
            setCoinAnim(monsterCell, 1);
        }else{
            GameData.setMonsterHasBeenConsumedInThisLevel();
        }


        for(int i = 0; i < cells.size; i++){
            final CoinRotateAnim coin = new CoinRotateAnim();
            boardView.addActor(coin);
            coin.setScale(monsterCell.getWidth() * 0.75f / coin.getWidth());
            final float centerX = (monsterCell.getWidth() - coin.getWidth() * coin.getScaleX()) * 0.5f;
            final float centerY = (monsterCell.getHeight() - coin.getHeight() * coin.getScaleY()) * 0.5f;
            coin.setX(monsterCell.getX() + centerX);
            coin.setY(monsterCell.getY() + centerY);

            final CellView cellView = cells.get(i);

            cellView.cellData.setState(Constants.TILE_STATE_COINED);
            GameData.saveTileState(cellView.cellData.getX(), cellView.cellData.getY(), Constants.TILE_STATE_COINED);
            cellView.setCoin(coin);

            Action moveTo = Actions.moveTo(cellView.getX() + centerX, cellView.getY() + centerY, .5f, Interpolation.sineOut);
            Action run2 = Actions.run(new Runnable() {
                @Override
                public void run() {
                    coin.remove();
                    coin.setPosition(centerX, centerY);
                    cellView.addActor(coin);


                }
            });

            coin.addAction(new SequenceAction(moveTo, run2));
        }

    }







    private void navigateToIntroScreen(){
        wordConnectGame.setScreen(new IntroScreen(wordConnectGame));
    }





    public void createHintButtons(){

        if(singleRandomHintBtn == null){

            String fontType = UIConfig.HINT_BUTTON_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            BitmapFont font = wordConnectGame.resourceManager.get(fontType, BitmapFont.class);

            singleRandomHintBtn = new HintButton(AtlasRegions.hint_empty_up, AtlasRegions.hint_empty_down, AtlasRegions.single_hint_bulb, font);
            singleRandomHintBtn.setOrigin(Align.center);
            singleRandomHintBtn.setTransform(true);
            singleRandomHintBtn.setX(stage.getWidth() * (1f - UIConfig.LEFT_AND_RIGHT_MARGIN) - singleRandomHintBtn.getWidth());
            singleRandomHintBtn.setY(dial.getY() + dial.getHeight() - singleRandomHintBtn.getHeight() * 1.35f);
            singleRandomHintBtn.setCost(GameConfig.COIN_COST_OF_USING_SINGLE_RANDOM_REVEAL);
            singleRandomHintBtn.update(HintManager.getRemainingSingleRandomRevealCount());
            stage.addActor(singleRandomHintBtn);

            singleRandomHintBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    if(UIConfig.INTERACTIVE_TUTORIAL_ENABLED && gameController.level.index < Constants.TUTORIAL_SINGLE_RANDOM_LEVEL) {
                        showTooltip(Align.right, singleRandomHintBtn, LanguageManager.format("hint_locked", Constants.TUTORIAL_SINGLE_RANDOM_LEVEL + 1));
                    }else{
                        closeHintButtonTutorial();
                        revealLetter(RewardRevealType.SINGLE_RANDOM_REVEAL);
                    }
                }
            });

            ///////////////

            ImageButton.ImageButtonStyle multiRandomStyle = new ImageButton.ImageButtonStyle();
            multiRandomStyle.up = new TextureRegionDrawable(AtlasRegions.random_hint_multi_up);
            multiRandomStyle.down = new TextureRegionDrawable(AtlasRegions.random_hint_multi_down);


            multiRandomHintBtn = new HintButton(multiRandomStyle, font, AtlasRegions.hint_btn_cost_bg);
            multiRandomHintBtn.setOrigin(Align.center);
            multiRandomHintBtn.setTransform(true);
            multiRandomHintBtn.setX(stage.getWidth() * (1f - UIConfig.LEFT_AND_RIGHT_MARGIN) - multiRandomHintBtn.getWidth());
            float fingerTop = dial.getY() + singleRandomHintBtn.getHeight();
            float space = singleRandomHintBtn.getY() - fingerTop;
            multiRandomHintBtn.setY(fingerTop + space * 0.5f - multiRandomHintBtn.getHeight() * 0.5f);
            multiRandomHintBtn.setCost(GameConfig.COIN_COST_OF_USING_MULTI_RANDOM_REVEAL);
            multiRandomHintBtn.update(HintManager.getRemainingMultiRandomRevealCount());
            stage.addActor(multiRandomHintBtn);

            multiRandomHintBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    if(UIConfig.INTERACTIVE_TUTORIAL_ENABLED && gameController.level.index < Constants.TUTORIAL_MULTI_RANDOM_LEVEL) {
                        showTooltip(Align.right, multiRandomHintBtn, LanguageManager.format("hint_locked", Constants.TUTORIAL_MULTI_RANDOM_LEVEL + 1));
                    }else{
                        closeHintButtonTutorial();
                        revealLetter(RewardRevealType.MULTI_RANDOM_REVEAL);
                    }
                }
            });

            //////////////


            fingerHintBtn = new HintButton(AtlasRegions.finger_hint_up, AtlasRegions.finger_hint_down, font);
            fingerHintBtn.setOrigin(Align.center);
            fingerHintBtn.setTransform(true);
            fingerHintBtn.setX(stage.getWidth() * (1f - UIConfig.LEFT_AND_RIGHT_MARGIN) - singleRandomHintBtn.getWidth());
            fingerHintBtn.setY(dial.getY());
            fingerHintBtn.setCost(GameConfig.COIN_COST_OF_USING_FINGER_REVEAL);
            fingerHintBtn.update(HintManager.getRemainingFingerRevealCount());
            stage.addActor(fingerHintBtn);

            fingerHintBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(UIConfig.INTERACTIVE_TUTORIAL_ENABLED && gameController.level.index < Constants.TUTORIAL_FINGER_LEVEL) {
                        showTooltip(Align.right, fingerHintBtn, LanguageManager.format("hint_locked", Constants.TUTORIAL_FINGER_LEVEL + 1));
                    }else{
                        closeHintButtonTutorial();
                        revealLetter(RewardRevealType.FINGER_REVEAL);

                    }

                }
            });

            ///////

            rocketHintBtn = new HintButton(AtlasRegions.hint_empty_up, AtlasRegions.hint_empty_down, font);
            rocketHintBtn.setOrigin(Align.center);
            rocketHintBtn.setTransform(true);
            rocketHintBtn.setX(stage.getWidth() * UIConfig.LEFT_AND_RIGHT_MARGIN);
            rocketHintBtn.setY(singleRandomHintBtn.getY());
            rocketHintBtn.setCost(GameConfig.COIN_COST_OF_USING_ROCKET_REVEAL);
            rocketHintBtn.update(HintManager.getRemainingRocketRevealCount());
            stage.addActor(rocketHintBtn);

            rocket = new Rocket(this);
            rocket.defaultX = rocketHintBtn.getX() + (rocketHintBtn.getWidth() - rocket.getWidth()) * 0.5f;
            rocket.defaultY = rocketHintBtn.getY() + (rocketHintBtn.getHeight() - rocket.getHeight()) * 0.5f;
            rocket.setX(rocket.defaultX);
            rocket.setY(rocket.defaultY);
            stage.addActor(rocket);


            rocketHintBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(UIConfig.INTERACTIVE_TUTORIAL_ENABLED && gameController.level.index < Constants.TUTORIAL_ROCKET_LEVEL) {
                        showTooltip(Align.left, rocketHintBtn, LanguageManager.format("hint_locked", Constants.TUTORIAL_ROCKET_LEVEL + 1));
                    }else{
                        closeHintButtonTutorial();
                        revealLetter(RewardRevealType.ROCKET_REVEAL);
                    }

                }
            });


            //////////////////



            extraWordsButton = new ExtraWordsButton();
            extraWordsButton.setOrigin(Align.center);
            extraWordsButton.setTransform(true);
            positionExtraWordButton();
            stage.addActor(extraWordsButton);


            extraWordsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    if(GameConfig.DEBUG_BONUS_WORDS_WIN_DIALOG){
                        openBonusWordsDialog(true);
                        return;
                    }


                    if(tutorial != null) {
                        GameData.setExtraWordsTutorialComplete1();
                        tutorial.fadeOut(tutorialRemover, true);
                    }

                    openBonusWordsDialog(false);

                }
            });




            if(wordConnectGame.adManager != null && wordConnectGame.adManager.isRewardedAdEnabledToEarnCoins()) {
                rewardedVideoButton = new RewardedVideoButton(this);
                rewardedVideoButton.setOrigin(Align.center);
                rewardedVideoButton.setX(multiRandomHintBtn.getX());
                rewardedVideoButton.setY(singleRandomHintBtn.getY() + singleRandomHintBtn.getHeight() * 1.2f);
                stage.addActor(rewardedVideoButton);

                rewardedVideoButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(!rewardedVideoButton.timerRunning()) {
                            if(wordConnectGame.adManager.isRewardedAdLoaded())
                                openWatchAndEarnDialog(false);
                            else
                                showToast(LanguageManager.get("no_video"));
                        }else{
                            rewardedVideoButton.flashText();
                        }
                    }
                });

            }

            if(rewardedVideoButton != null){
                dial.setZIndex(rewardedVideoButton.getZIndex() + 1);

                long lastRewardedVideoTime = GameData.getLastRewardedAdTime();

                if(wordConnectGame.adManager.getIntervalBetweenRewardedAds() > 0 && lastRewardedVideoTime > 0)
                    rewardedVideoButton.startTimer(lastRewardedVideoTime);
            }

        }
    }








    public void positionExtraWordButton() {
            extraWordsButton.setX(stage.getWidth() * UIConfig.LEFT_AND_RIGHT_MARGIN);
            extraWordsButton.setY(multiRandomHintBtn.getY());
    };













    private void lockOrUnlockHintButtons(){
        if(!UIConfig.INTERACTIVE_TUTORIAL_ENABLED) return;

        if(gameController.level.index < Constants.TUTORIAL_SINGLE_RANDOM_LEVEL)
            singleRandomHintBtn.lock(true);
        else if(gameController.level.index == Constants.TUTORIAL_SINGLE_RANDOM_LEVEL)
            singleRandomHintBtn.unlock();

        if(gameController.level.index < Constants.TUTORIAL_FINGER_LEVEL)
            fingerHintBtn.lock(true);
        else if(gameController.level.index == Constants.TUTORIAL_FINGER_LEVEL)
            fingerHintBtn.unlock();

        if(gameController.level.index < Constants.TUTORIAL_MULTI_RANDOM_LEVEL)
            multiRandomHintBtn.lock(true);
        else if(gameController.level.index == Constants.TUTORIAL_MULTI_RANDOM_LEVEL)
            multiRandomHintBtn.unlock();

        if(gameController.level.index < Constants.TUTORIAL_ROCKET_LEVEL)
            rocketHintBtn.lock(false);
        else if(gameController.level.index == Constants.TUTORIAL_ROCKET_LEVEL)
            rocketHintBtn.unlock();

    }




    public void openBonusWordsDialog(boolean completed){
        if(!completed){
            if(bonusWordsIncompleteDialog == null) bonusWordsIncompleteDialog = new BonusWordsIncompleteDialog(stage.getWidth(), stage.getHeight(), this);
            stage.addActor(bonusWordsIncompleteDialog);
            bonusWordsIncompleteDialog.show();
        }else{
            if(bonusWordsCompleteDialog == null) bonusWordsCompleteDialog = new BonusWordsCompleteDialog(stage.getWidth(), stage.getHeight(), this, topPanel);
            stage.addActor(bonusWordsCompleteDialog);
            bonusWordsCompleteDialog.show();
        }
    }





    private Word giveRocketHint(){

        Word word = gameController.level.getBoardModel().getRandomWordForRocket(boardView);

        if(word == null)
            return null;

        stage.getRoot().setTouchable(Touchable.disabled);
        word.hasRocket = true;
        GameData.saveWordWithRocket(word.id);

        if(rocket.getZIndex() <= dial.getZIndex())
            rocket.setZIndex(dial.getZIndex() + 1);

        Array<CellView> cellViewsToAnimate = boardView.findWordCellViews(word);

        rocket.start(cellViewsToAnimate, word.direction);

        if(!ConfigProcessor.muted) {
            Sound sound = wordConnectGame.resourceManager.get(ResourceManager.SFX_ROCKET, Sound.class);
            sound.play(SoundConfig.SFX_ROCKET_VOLUME);
        }

        return word;
    }






    public void closeBoardOverlayAndAnimateHint(Runnable callback){
        stage.getRoot().setTouchable(Touchable.disabled);

        int remainingHints = HintManager.getRemainingFingerRevealCount();
        if(remainingHints > 0) {
            --remainingHints;
            HintManager.setFingerHintRevealCount(remainingHints);
        }else{
            int remainingCoins = HintManager.getRemainingCoins();
            int resultingCoins = remainingCoins - GameConfig.COIN_COST_OF_USING_FINGER_REVEAL;
            HintManager.setCoinCount(resultingCoins);
            topPanel.coinView.update(resultingCoins);
        }
        fingerHintBtn.update(remainingHints);
        boardOverlay.hide(callback);
    }




    private void resetGameView(){

        if(sideComboDisplay != null) {
            topPanel.topComboDisplay.reset();
            sideComboDisplay.setX(-sideComboDisplay.getWidth());
        }
    }




    public void createLevelContent(Level level){
        disableButtons(true);

        createAndPositionDial(level.getLetters());
        createAndPositionPreview();

        createAndPositionBoard(level.getBoardModel());

        initDial();

        createAndPositionShuffleButton();

        topPanel.coinView.cancel(false);
        if(topPanel.getY() != stage.getHeight()) topPanel.setY(stage.getHeight());


        topPanel.topComboDisplay.setLevelNumber(level.index + 1);
        determineBooster();


        createHintButtons();

        lockOrUnlockHintButtons();
        tempComboReward = 0;
        showUI();
    }






    private void checkTutorial(){

        if(!UIConfig.INTERACTIVE_TUTORIAL_ENABLED) return;

        if(boardView.bomb != null && !GameData.isBombTutorialDisplayed()){
            tutorial = new TutorialBooster(GameScreen.this);
            stage.addActor(tutorial);
            tutorial.paddingX = 1.3f;
            tutorial.highlightActor(boardView.bomb, Tutorial.Shape.DISC);
            TutorialBooster tutorialBooster = (TutorialBooster) tutorial;
            tutorial.showText(LanguageManager.get("bomb_tutorial"));
            tutorialBooster.setGotIt(LanguageManager.get("got_it"));
            tutorial.fadeIn(null);
            tutorial.tutorialSaver = new Tutorial.TutorialSaver() {
                @Override
                public void save() {
                    ((TutorialBooster) tutorial).gotit.setDisabled(true);
                    GameData.setBombTutorialComplete();
                }
            };
            tutorialBooster.setGotItListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tutorial.tutorialSaver.save();
                    tutorial.fadeOut(tutorialRemover, true);
                }
            });
            return;
        }


        if(boardView.goldPack != null && !GameData.isGoldPackTutorialDisplayed()){
            tutorial = new TutorialBooster(this);
            stage.addActor(tutorial);
            tutorial.paddingX = 1.5f;
            tutorial.highlightActor(boardView.goldPack, Tutorial.Shape.DISC);
            TutorialBooster tutorialBooster = (TutorialBooster)tutorial;
            tutorial.showText(LanguageManager.get("goldpack_tutorial"));
            tutorialBooster.setGotIt(LanguageManager.get("got_it"));
            tutorial.fadeIn(null);
            tutorial.tutorialSaver = new Tutorial.TutorialSaver() {
                @Override
                public void save() {
                    ((TutorialBooster) tutorial).gotit.setDisabled(true);
                    GameData.setGoldPackTutorialComplete();
                }
            };
            tutorialBooster.setGotItListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tutorial.tutorialSaver.save();
                    tutorial.fadeOut(tutorialRemover, true);
                }
            });
            return;
        }



        if(boardView.monster != null && !GameData.isMonsterTutorialDisplayed()){
            tutorial = new TutorialBooster(this);
            stage.addActor(tutorial);
            tutorial.paddingX = 1.3f;
            tutorial.highlightActor(boardView.monster, Tutorial.Shape.DISC);
            TutorialBooster tutorialBooster = (TutorialBooster)tutorial;
            tutorial.showText(LanguageManager.get("monster_tutorial"));
            tutorialBooster.setGotIt(LanguageManager.get("got_it"));
            tutorialBooster.fadeIn(null);
            tutorial.tutorialSaver = new Tutorial.TutorialSaver() {
                @Override
                public void save() {
                    ((TutorialBooster) tutorial).gotit.setDisabled(true);
                    GameData.setMonsterTutorialComplete();
                }
            };
            tutorialBooster.setGotItListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tutorial.tutorialSaver.save();
                    tutorial.fadeOut(tutorialRemover, true);
                }
            });
            return;
        }


        int levelIndex = gameController.level.index;

        if(levelIndex > Constants.TUTORIAL_ROCKET_LEVEL) return;

        int step = GameData.getTutorialStep();



        if(step == Constants.TUTORIAL_PLAY_BUTTON && levelIndex == Constants.TUTORIAL_DIAL_LEVEL) tutorialStep_2();
        else if(step == Constants.TUTORIAL_DIAL && levelIndex == Constants.TUTORIAL_SHUFFLE_LEVEL) tutorialStep_3();
        else if(step == Constants.TUTORIAL_SHUFFLE && levelIndex == Constants.TUTORIAL_SINGLE_RANDOM_LEVEL) tutorialStep_4();
        else if(step == Constants.TUTORIAL_SINGLE_RANDOM_HINT && levelIndex == Constants.TUTORIAL_FINGER_LEVEL) tutorialStep_5();
        else if(step == Constants.TUTORIAL_FINGER_HINT && levelIndex == Constants.TUTORIAL_MULTI_RANDOM_LEVEL) tutorialStep_6();
        else if(step == Constants.TUTORIAL_MULTI_RANDOM && levelIndex == Constants.TUTORIAL_ROCKET_LEVEL) tutorialStep_7();
    }




    private void tutorialStep_2(){
        tutorial = new TutorialDial(this, dial);
        tutorial.step = Constants.TUTORIAL_DIAL;
        stage.addActor(tutorial);
        tutorial.getColor().a = 0f;
        tutorial.highlightActor(dial, Tutorial.Shape.DISC);
        ((TutorialDial) tutorial).indicateLetters(dial.dialButtons, gameController.level.getBoardModel().getAllWords(true));
    }




    private void tutorialStep_3(){
        tutorial = new TutorialHintButton(this);
        tutorial.step = Constants.TUTORIAL_SHUFFLE;
        stage.addActor(tutorial);
        tutorial.highlightActor(shuffleButton, Tutorial.Shape.DISC);
        tutorial.indicateActor(180);
        tutorial.showText(LanguageManager.get("shuffle_tutorial"));
        tutorial.textContainer.setY(dial.getY() + dial.getHeight() - tutorial.textContainer.getHeight());

    }




    private void tutorialStep_4(){
        tutorial = new TutorialHintButton(this);
        tutorial.step = Constants.TUTORIAL_SINGLE_RANDOM_HINT;
        stage.addActor(tutorial);
        tutorial.highlightActor(singleRandomHintBtn, Tutorial.Shape.DISC);
        tutorial.indicateActor(0);
        tutorial.showText(LanguageManager.get("single_random_hint_tutorial"));
    }




    private void tutorialStep_5(){
        tutorial = new TutorialHintButton(this);
        tutorial.step = Constants.TUTORIAL_FINGER_HINT;
        stage.addActor(tutorial);
        tutorial.highlightActor(fingerHintBtn, Tutorial.Shape.DISC);
        tutorial.indicateActor(0);
        tutorial.showText(LanguageManager.get("finger_hint_tutorial"));
    }




    private void tutorialStep_6(){
        tutorial = new TutorialHintButton(this);
        tutorial.step = Constants.TUTORIAL_MULTI_RANDOM;
        stage.addActor(tutorial);
        tutorial.highlightActor(multiRandomHintBtn, Tutorial.Shape.DISC);
        tutorial.indicateActor(0);
        tutorial.showText(LanguageManager.get("multi_random_hint_tutorial"));
        tutorial.textContainer.setY(dial.getY() + dial.getHeight());
    }




    private void tutorialStep_7(){
        tutorial = new TutorialHintButton(this);
        tutorial.step = Constants.TUTORIAL_ROCKET;
        stage.addActor(tutorial);
        tutorial.highlightActor(rocketHintBtn, Tutorial.Shape.DISC);
        tutorial.indicateActor(180);
        tutorial.showText(LanguageManager.get("rocket_hint_tutorial"));
        rocket.setZIndex(rocketHintBtn.getZIndex() + 1);
    }




    public void tutorialExtraWords(){
        tutorial = new TutorialHintButton(this);
        stage.addActor(tutorial);
        tutorial.highlightActor(extraWordsButton, Tutorial.Shape.DISC);
        tutorial.indicateActor(180);
        tutorial.showText(LanguageManager.get("bonus_word_tutorial_1"));
        tutorial.textContainer.setY(dial.getY() + dial.getHeight());
        stage.getRoot().setTouchable(Touchable.enabled);
        tutorial.tutorialSaver = new Tutorial.TutorialSaver() {
            @Override
            public void save() {
                GameData.setExtraWordsTutorialComplete1();
            }
        };
    }






    private Runnable showUIAnimFinished = new Runnable() {
        @Override
        public void run() {
            topPanel.coinView.getColor().a = 1f;
            resumeCombo();
            checkTutorial();

            resumeFinalWordAnimation();

            singleRandomHintBtn.stopShowUp();
            IdleTimer.setCallback(idleTimerCallback);

            boolean showLuckyWheel = false;

            if(tutorial == null) {
                IdleTimer.setPaused(false);

                if(rewardedVideoButton != null){
                    rewardedVideoButton.startRewardedGlow();
                }
                if(!offeredBoosterInThisLevel) showLuckyWheel = checkWheelDialogTiming();
            }

            disableButtons(false);
            stage.getRoot().setTouchable(Touchable.enabled);

            triggerUfoFlyIn();

        }
    };




    private void showInterstitial() {
        if(wordConnectGame.adManager != null && wordConnectGame.adManager.isInterstitialAdEnabled()){
            if(GameConfig.shouldWeShowAnInterstitialAdForThisLevel(gameController.level.index)){
                if(wordConnectGame.adManager.isInterstitialAdLoaded()) {
                    wordConnectGame.adManager.showInterstitialAd(interstitialClosed);
                    stage.getRoot().setTouchable(Touchable.disabled);
                }else{
                    hideUI(showLevelFinishedView);
                }
            }else{
                hideUI(showLevelFinishedView);
            }
        }else{
            hideUI(showLevelFinishedView);
        }
    };



    private Runnable interstitialClosed = new Runnable() {
        @Override
        public void run() {
            hideUI(showLevelFinishedView);
        }
    };






    private void resumeFinalWordAnimation(){
       boardView.addAction(new SequenceAction(Actions.delay(Constants.RENDER_DELAY_FINAL_WORD_ANIM), Actions.run(gameController.finalWordAnimationChecker)));
    }





    private void createSmoke(){
        smoke = new Smoke();
        smoke.setX((stage.getWidth() - smoke.getWidth()) * 0.5f);
        float dialY = AtlasRegions.dial.getRegionHeight() * 0.1f;
        smoke.setY(dialY + (AtlasRegions.dial.getRegionHeight() - smoke.getHeight()) * 0.5f);
        stage.addActor(smoke);
    }





    private void createAndPositionDial(char[] letters){

        DialButton.labelStyleUp.fontColor = UIConfig.getDialButtonTextColorUpStateByLevelIndex(gameController.level.index);
        DialButton.labelStyleDown.fontColor = UIConfig.getDialButtonTextColorDownStateByLevelIndex(gameController.level.index);

        if(dial == null) {
            dial = new Dial();
            dial.setGameScreen(this);
            dial.setOrigin(Align.center);
            dial.setX((stage.getWidth() - dial.getWidth()) * 0.5f);
            if(UiUtil.isScreenWide()) dial.setY(dial.getHeight() * UIConfig.MARGIN_BOTTOM_WIDE_SCREEN);
            else dial.setY(dial.getHeight() * UIConfig.MARGIN_BOTTOM_NORMAL_SCREEN);

            dial.setGameController(gameController);
            gameController.setDial(dial);
            stage.addActor(dial);
        }

        dial.setDialBackgroundColor(UIConfig.getDialBackgroundColorByLevelIndex(gameController.level.index));
        dial.setLetters(letters);
    }






    private void createAndPositionPreview(){
        if(preview == null) {
            preview = new Preview(this);
            preview.setY(dial.getY() + dial.getHeight() + preview.getHeight() * 0.05f);
            stage.addActor(preview);
            gameController.setPreview(preview);
        }
        preview.setColor(ConfigProcessor.getLevelColor(gameController.level.index));
    }






    private void createAndPositionBoard(BoardModel boardModel){
        if(boardView == null) {
            boardView = new BoardView();
            stage.addActor(boardView);
            gameController.setBoard(boardView);
        }

        boardView.setThemeColor(ConfigProcessor.getLevelColor(gameController.level.index));
        boardView.setTileBackgroundColor(UIConfig.getTileBackgroundUnsolvedColorByLevelIndex(gameController.level.index));

        float height = calculateHeightForBoard();
        boardView.init(boardModel, this, stage.getWidth() - stage.getWidth() * UIConfig.LEFT_AND_RIGHT_MARGIN * 2f, height);

        boardView.setOrigin(Align.center);
        boardView.setX((stage.getWidth() - boardView.getWidth()) * 0.5f);

        float bottom = preview.getY() + preview.getHeight();
        float space = stage.getHeight() - topPanel.getHeight() - bottom;
        boardView.setY(bottom + (space - boardView.getHeight()) * 0.5f );

        boardView.setZIndex(dial.getZIndex() - 1);
        topPanel.setZIndex(boardView.getZIndex() + 1);
    }





    private float calculateHeightForBoard(){
        float top = stage.getHeight() - topPanel.getHeight();
        return top - (preview.getY() + preview.getHeight());
    }






    private void initDial(){
        if(dialAnimationContainer != null) {
            dialAnimationContainer.setDial(dial);
            dialAnimationContainer.init();
        }
        dial.setColor(ConfigProcessor.getLevelColor(gameController.level.index));
    }






    private void createAndPositionShuffleButton(){
        if(shuffleButton == null) {
            shuffleButton = new ImageButton(new TextureRegionDrawable(AtlasRegions.shuffle_up), new TextureRegionDrawable(AtlasRegions.shuffle_down));
            shuffleButton.setOrigin(Align.center);
            shuffleButton.setTransform(true);
            shuffleButton.setX(stage.getWidth() * UIConfig.LEFT_AND_RIGHT_MARGIN);
            shuffleButton.setY(dial.getY());
            stage.addActor(shuffleButton);

            shuffleButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    closeShuffleTutorial();
                    dial.shuffle();
                    if (!ConfigProcessor.muted) {
                        Sound shuffle = wordConnectGame.resourceManager.get(ResourceManager.SFX_SHUFFLE, Sound.class);
                        shuffle.play(SoundConfig.SFX_SHUFFLE_VOLUME);
                    }
                }
            });
        }
    }





    private void closeShuffleTutorial(){
        if(tutorial != null){
            GameData.saveTutorialStep(tutorial.step);
            tutorial.fadeOut(tutorialRemover, true);
        }
    }





    public Runnable tutorialRemover = new Runnable() {

        @Override
        public void run() {
            if(tutorial != null){
                tutorial.remove();
                tutorial = null;
            }

            if(rewardedVideoButton != null) rewardedVideoButton.startRewardedGlow();
        }
    };





    private void closeHintButtonTutorial(){
        if(tutorial != null){
            if(tutorial instanceof TutorialHintButton){
                GameData.saveTutorialStep(tutorial.step);
                TutorialHintButton tutorialHintButton = (TutorialHintButton) tutorial;
                tutorialHintButton.fadeOut(tutorialRemover, true);
            }
        }
    }





    public void animateHint(final Array<CellView> cellViews, Actor button){

        if(!ConfigProcessor.muted) {
            Sound sound = wordConnectGame.resourceManager.get(ResourceManager.SFX_HINT, Sound.class);
            sound.play(SoundConfig.SFX_HINT_VOLUME);
        }

        if(button instanceof CellView){//this is finger hint
            button = fingerHintBtn;
            cellViews.get(0).animateUnsolvedBgColor();
        }

        for(int i = 0; i < cellViews.size; i++){
            final CellView cellView = cellViews.get(i);
            cellView.cellData.setState(Constants.TILE_STATE_REVEALED);
            GameData.saveTileState(cellView.cellData.getX(), cellView.cellData.getY(), Constants.TILE_STATE_REVEALED);
            HintComet hintComet = new HintComet();
            cellView.hintComet = hintComet;

            float x = button.getX() + button.getWidth() * 0.5f - hintComet.getWidth() * 0.5f;
            float y = button.getY() + button.getHeight() * 0.5f - hintComet.getWidth() * 0.5f;

            hintComet.setPosition(x, y);

            Vector2 targetVec2 = cellView.getStageCoords();
            float diff = (hintComet.getWidth() - cellView.getWidth()) * 0.5f;
            stage.addActor(hintComet);
            hintComet.setZIndex(singleRandomHintBtn.getZIndex() - 1);


            AngledBezierToAction bezier = new AngledBezierToAction();
            bezier.setDuration(0.7f);
            bezier.setStartPosition(x, y);
            bezier.setPointA(x, y);
            bezier.setPointB(stage.getWidth() * 0.5f, button.getY());
            bezier.setEndPosition(targetVec2.x - diff, targetVec2.y - diff);
            bezier.setInterpolation(Interpolation.sineIn);

            final boolean enable = i == cellViews.size - 1;

            RunnableAction end = new RunnableAction();
            end.setRunnable(new Runnable() {
                @Override
                public void run() {
                    cellView.starBurst();

                    cellView.updateStateView();

                    cellView.growAndShrink(null);
                    if(cellView.hintComet != null){
                        cellView.hintComet.destroy();
                        cellView.hintComet.remove();
                        cellView.hintComet = null;
                    }

                    if(enable){
                        resumeIdleTimer();
                        gameController.findAndCompleteInCompleteCellViewsAfterGivingHint(cellViews);
                        stage.getRoot().setTouchable(Touchable.enabled);
                    }

                }
            });

            SequenceAction sequenceAction = new SequenceAction(bezier, Actions.fadeOut(0.05f), end);
            hintComet.addAction(sequenceAction);

        }
    }






    private void revealLetter(RewardRevealType type){
        stage.getRoot().setTouchable(Touchable.disabled);
        stopIdleTimer();

        boolean hasEnoughCredit = true;
        int remainingCoins = HintManager.getRemainingCoins();

        if(type == RewardRevealType.SINGLE_RANDOM_REVEAL){
            if(boardView.getSingleRandomCell(null) == null){
                stage.getRoot().setTouchable(Touchable.enabled);
                return;
            }
            int remaningHints = HintManager.getRemainingSingleRandomRevealCount();
            if(remaningHints == 0){
                if(remainingCoins >= GameConfig.COIN_COST_OF_USING_SINGLE_RANDOM_REVEAL){
                    int resultingCoins = remainingCoins - GameConfig.COIN_COST_OF_USING_SINGLE_RANDOM_REVEAL;
                    HintManager.setCoinCount(resultingCoins);
                    topPanel.coinView.update(resultingCoins);
                }else{
                    hasEnoughCredit = false;
                }
            }

            if(hasEnoughCredit){
                if(boardView.revealSingleRandomHint(singleRandomHintBtn) != null){
                    if(remaningHints > 0) {
                        --remaningHints;
                        HintManager.setSingleRandomRevealCount(remaningHints);
                    }
                    singleRandomHintBtn.update(remaningHints);
                }else{
                    stage.getRoot().setTouchable(Touchable.enabled);
                }
            }

        }else if(type == RewardRevealType.MULTI_RANDOM_REVEAL){
            if(boardView.selectMultipleCellsForHint(GameConfig.getNumberOfTilesToRevealForMultiRandomHint(gameController.level.index)).size == 0){
                stage.getRoot().setTouchable(Touchable.enabled);
                return;
            }


            int remaningHints = HintManager.getRemainingMultiRandomRevealCount();
            if(remaningHints == 0){
                if(remainingCoins >= GameConfig.COIN_COST_OF_USING_MULTI_RANDOM_REVEAL){
                    int resultingCoins = remainingCoins - GameConfig.COIN_COST_OF_USING_MULTI_RANDOM_REVEAL;
                    HintManager.setCoinCount(resultingCoins);
                    topPanel.coinView.update(resultingCoins);
                }else{
                    hasEnoughCredit = false;
                }
            }
            if(hasEnoughCredit){
                if(boardView.revealMultiRandomHint(multiRandomHintBtn, gameController.level.index) > 0){
                    if(remaningHints > 0) {
                        --remaningHints;
                        HintManager.setMultiRandomRevealCount(remaningHints);
                    }
                    multiRandomHintBtn.update(remaningHints);
                }else{
                    stage.getRoot().setTouchable(Touchable.enabled);
                }
            }

        }else if(type == RewardRevealType.FINGER_REVEAL){

            int remainingHints = HintManager.getRemainingFingerRevealCount();
            if(remainingHints == 0){
                if(remainingCoins >= GameConfig.COIN_COST_OF_USING_FINGER_REVEAL){
                    /*int resultingCoins = remainingCoins - GameConfig.COIN_COST_OF_USING_FINGER_REVEAL;
                    HintManager.setCoinCount(resultingCoins);
                    topPanel.coinView.update(resultingCoins);*/
                }else{
                    hasEnoughCredit = false;
                }
            }
            //
            if(hasEnoughCredit){
                boardView.setFingerHintSelectionModeActive(true);
                if(boardOverlay == null){
                    TextureAtlas atlas = wordConnectGame.resourceManager.get(ResourceManager.ATLAS_1, TextureAtlas.class);
                    boardOverlay = new BoardOverlay(stage.getWidth(), stage.getHeight(), atlas.findRegion("close"), fingerHintBtn, boardView, this);
                }
                boardOverlay.setVisible(true);
                stage.addActor(boardOverlay);
                boardView.setZIndex(boardOverlay.getZIndex() + 1);
                boardOverlay.show();
            }


        }else if(type == RewardRevealType.ROCKET_REVEAL){
            if(gameController.level.getBoardModel().getRandomWordForRocket(boardView) == null){
                stage.getRoot().setTouchable(Touchable.enabled);
                showToast(LanguageManager.get("not_available_for_rocket"));
                return;
            }
            int remainingHints = HintManager.getRemainingRocketRevealCount();
            if(remainingHints == 0){
                if(remainingCoins >= GameConfig.COIN_COST_OF_USING_ROCKET_REVEAL){
                    int resultingCoins = remainingCoins - GameConfig.COIN_COST_OF_USING_ROCKET_REVEAL;
                    HintManager.setCoinCount(resultingCoins);
                    topPanel.coinView.update(resultingCoins);
                }else{
                    hasEnoughCredit = false;
                }
            }

            if(hasEnoughCredit){
                if(giveRocketHint() != null){
                    if(remainingHints > 0) {
                        --remainingHints;
                        HintManager.setRocketRevealCount(remainingHints);
                    }
                    rocketHintBtn.update(remainingHints);
                }else{
                    showToast(LanguageManager.get("not_available_for_rocket"));
                    stage.getRoot().setTouchable(Touchable.enabled);
                }
            }
        }



        if(!hasEnoughCredit && GameConfig.SHOW_WATCH_AD_WHEN_NO_COINS_AND_HINTS_LEFT && wordConnectGame.adManager != null && wordConnectGame.adManager.isRewardedAdEnabledToEarnCoins()) {
            openWatchAndEarnDialog(false);
        }
    }






    @Override
    protected boolean onBackPress() {
        boolean hasDialog = super.onBackPress();

        if(!hasDialog){

            if(GameConfig.SKIP_INTRO){
                if(wordConnectGame.appExit != null) wordConnectGame.appExit.exitApp();
                else Gdx.app.exit();
                return false;
            }



            ConfirmDialog confirmDialog = new ConfirmDialog(
                    stage.getWidth(),
                    stage.getHeight(),
                    this,
                    LanguageManager.get("confirm"),
                    LanguageManager.get("home_return"),
                    LanguageManager.get("yes"),
                    LanguageManager.get("no")
            );
            confirmDialog.setDialogId(Constants.CONFIRM_DIALOG_BACK_PRESS);

            stage.addActor(confirmDialog);
            confirmDialog.show();

            confirmDialog.setConfirmCallback(new ConfirmDialog.ConfirmCallback() {
                @Override
                public void confirmClicked(String buttonLabel) {
                    if(buttonLabel != null && buttonLabel.equals(LanguageManager.get("yes")))
                        navigateToIntroScreen();
                }
            });

            return true;
        }

        return false;
    }




    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }






    @Override
    public void showDictionary(String[] words) {

        if(!ConnectionManager.network.isConnected()){
            showToast(LanguageManager.get("no_connection"));
            return;
        }

        if(words != null)
            DictionaryDialog.words = words;

        if(dictionaryDialog == null)
            dictionaryDialog = new DictionaryDialog(stage.getWidth(), stage.getHeight(), this);
        else
            dictionaryDialog.setVisible(true);

        stage.addActor(dictionaryDialog);
        dictionaryDialog.show();
    }






    public void levelFinished(){
        stopIdleTimer();
        if(rewardedVideoButton != null) rewardedVideoButton.stopRewardedGlow();
        if(dialAnimationContainer != null) dialAnimationContainer.setStage(0);
        showInterstitial();
    }




    private void showUI(){
        float time = 0.1f;

        CoinView coinView = topPanel.coinView;
        coinView.getColor().a = 1f;
        coinView.setVisible(true);

        if(coinView.getParent() != topPanel) throw new RuntimeException("a");

        topPanel.addAction(Actions.moveBy(0, -topPanel.getHeight(), 0.2f, word.game.actions.Interpolation.backOut));

        time += 0.1f;
        UiUtil.actorAnimIn(boardView, time, null);

        if(rewardedVideoButton != null){
            time += 0.1f;
            UiUtil.actorAnimIn(rewardedVideoButton, time, null);
        }

        time += 0.1f;
        UiUtil.actorAnimIn(rocketHintBtn, time, null);
        UiUtil.actorAnimIn(rocket, time, null);
        UiUtil.actorAnimIn(singleRandomHintBtn, time, null);
        UiUtil.actorAnimIn(dial, time, null);

        time += 0.1f;
        UiUtil.actorAnimIn(extraWordsButton, time, null);
        UiUtil.actorAnimIn(multiRandomHintBtn, time, null);

        time += 0.1f;
        UiUtil.actorAnimIn(shuffleButton, time, null);
        UiUtil.actorAnimIn(fingerHintBtn, time, showUIAnimFinished);
    }





    private void hideUI(Runnable callback){

        if(sideComboDisplay != null && sideComboDisplay.getX() + sideComboDisplay.getWidth() > 0) sideComboDisplay.slideLeft();

        float time = 0.2f;

        topPanel.addAction(Actions.moveBy(0, topPanel.getHeight(), 0.2f, word.game.actions.Interpolation.backIn));

        time += 0.1f;
        UiUtil.actorAnimOut(boardView, time, null);

        if(rewardedVideoButton != null) {
            time += 0.1f;
            UiUtil.actorAnimOut(rewardedVideoButton, time, null);
        }

        time += 0.1f;
        UiUtil.actorAnimOut(rocketHintBtn, time, null);
        UiUtil.actorAnimOut(rocket, time, null);
        UiUtil.actorAnimOut(singleRandomHintBtn, time, null);
        UiUtil.actorAnimOut(dial, time, null);

        if(dialAnimationContainer != null){
            dialAnimationContainer.addAction(Actions.sequence(Actions.delay(time), Actions.fadeOut(0.1f)));
        }


        time += 0.1f;
        UiUtil.actorAnimOut(extraWordsButton, time, null);
        UiUtil.actorAnimOut(multiRandomHintBtn, time, null);

        time += 0.1f;
        UiUtil.actorAnimOut(shuffleButton, time, null);
        UiUtil.actorAnimOut(fingerHintBtn, time, callback);

    }







    private Runnable showLevelFinishedView = new Runnable() {
        @Override
        public void run() {

            if(backNavQueue.size() > 0){
                BackNavigator dialog = backNavQueue.peek();
                if(dialog instanceof BaseDialog){
                    BaseDialog baseDialog = (BaseDialog)dialog;
                    baseDialog.hide();
                }
            }

            if(levelEndView == null) {
                levelEndView = new LevelEndView(GameScreen.this, stage.getWidth(), stage.getHeight());
                levelEndView.addDictionaryShowListener(GameScreen.this);
                levelEndView.addNextLevelListener(new Runnable() {
                    @Override
                    public void run() {

                        if(gameController.level.index == LanguageManager.locale.LevelCount) {
                            navigateToIntroScreen();
                        }else{
                            topPanel.coinView.update(HintManager.getRemainingCoins());
                            gameController.prepareNextLevel();
                            levelEndView.remove();
                            levelEndView.setVisible(false);
                        }

                    }
                });

            }


            stage.addActor(levelEndView);
            levelEndView.setVisible(true);
            levelEndView.getColor().a = 1f;

            int startFrom = HintManager.getRemainingCoins();

            levelEndView.startComboAnimWithRewards(tempComboReward, gameController.level.index);
            HintManager.setCoinCount(startFrom + tempComboReward);
            resetGameView();
        }
    };





    public void destroyLevel(){
        boardView.clearContent();
        dial.clearContent();
        resetCombo(null);

        //if(dialAnimationContainer != null) dialAnimationContainer.setStage(0);
        //topPanel.topComboDisplay.setComboCountWithoutAnim(0);
    }




    private Runnable idleTimerCallback = new Runnable() {
        @Override
        public void run() {
            singleRandomHintBtn.showUp();

        }
    };






    public void stopIdleTimer(){
        if(singleRandomHintBtn != null){
            singleRandomHintBtn.stopShowUp();
        }
        IdleTimer.setPaused(true);
    }





    public void resumeIdleTimer(){
        IdleTimer.reset();
        IdleTimer.setPaused(false);
    }





    public ChangeListener gotoIntroScreen = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            stage.getRoot().setTouchable(Touchable.disabled);
            hideUI(new Runnable() {

                @Override
                public void run() {
                    IntroScreen introScreen = new IntroScreen(wordConnectGame);
                    wordConnectGame.setScreen(introScreen);
                }
            });

        }
    };




    public void disableButtons(boolean e){
        if(topPanel.backBtn != null) topPanel.backBtn.setDisabled(e);
        if(topPanel.btnMenu != null) topPanel.btnMenu.setDisabled(e);
        if(topPanel.coinView != null && topPanel.coinView.plus != null) topPanel.coinView.plus.setDisabled(e);
        if(rocketHintBtn != null) rocketHintBtn.setDisabled(e);
        if(extraWordsButton != null) extraWordsButton.setDisabled(e);
        if(shuffleButton != null) shuffleButton.setDisabled(e);
        if(fingerHintBtn != null) fingerHintBtn.setDisabled(e);
        if(multiRandomHintBtn != null) multiRandomHintBtn.setDisabled(e);
        if(singleRandomHintBtn != null) singleRandomHintBtn.setDisabled(e);
        if(rewardedVideoButton != null) rewardedVideoButton.button.setDisabled(e);
    }






    @Override
    public void dispose() {
        stopIdleTimer();
        if(dial != null) dial.dispose();
        if(dialAnimationContainer != null) dialAnimationContainer.dispose();
        Pools.clearAll();
        super.dispose();
    }



    @Override
    public void render(float delta) {
        super.render(delta);
        IdleTimer.update(delta);

        if( cameraShaker.isCameraShaking() ){
            Vector2 shake = cameraShaker.getNewShakePosition();
            stage.getRoot().setPosition(shake.x, shake.y);
            shake.x *= ResourceManager.scaleFactor;
            shake.y *= ResourceManager.scaleFactor;
            if(dialAnimationContainer != null) dialAnimationContainer.dialAnimation.setUniformVec2("u_pos", shake);
        }

    }


}
