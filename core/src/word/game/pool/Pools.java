package word.game.pool;

public class Pools {


    public static LetterPool letterPool = new LetterPool();
    public static CellViewPool cellViewPool = new CellViewPool();
    public static CellModelPool cellModelPool = new CellModelPool();
    public static DialButtonPool dialButtonPool = new DialButtonPool();
    public static AnimationLabelPool animationLetterPool = new AnimationLabelPool();
    public static CoinPool coinPool = new CoinPool();
    public static WordPool wordPool = new WordPool();
    public static CellViewParticlePool cellViewParticlePool = new CellViewParticlePool();


    public static void clearAll(){
        letterPool.clear();
        cellViewPool.clear();
        cellModelPool.clear();
        dialButtonPool.clear();
        animationLetterPool.clear();
        coinPool.clear();
        wordPool.clear();
        cellViewParticlePool.clear();
    }
}
