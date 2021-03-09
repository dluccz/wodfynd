package word.game.util;

public class MathUtil {

    public static float scaleNumber(float Input, float InputLow, float InputHigh, float OutputLow, float OutputHigh){
        return ((Input - InputLow) / (InputHigh - InputLow)) * (OutputHigh - OutputLow) + OutputLow;
    }
}
