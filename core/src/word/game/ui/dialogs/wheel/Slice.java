package word.game.ui.dialogs.wheel;

public class Slice {

    public String text;
    public RewardRevealType reward;
    public int quantity;
    public int probability;


    public Slice(String text, RewardRevealType reward, int quantity, int probability) {
        this.text = text;
        this.reward = reward;
        this.quantity = quantity;
        this.probability = probability;
    }
}
