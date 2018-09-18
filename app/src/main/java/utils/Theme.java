package utils;

import com.giz.notes3.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Theme {
    public static final int THEME_COLOR_DEFAULT = R.color.theme_default;
    public static final int THEME_COLOR_BLUE = R.color.theme_blue;
    public static final int THEME_COLOR_PINK = R.color.theme_pink;
    public static final int THEME_COLOR_RED = R.color.theme_red;
    public static final int THEME_COLOR_GRAY = R.color.theme_gray;
    public static final int THEME_COLOR_YELLOW = R.color.theme_yellow;
    public static final int THEME_COLOR_ORANGE = R.color.theme_orange;
    public static final int THEME_COLOR_GREEN = R.color.theme_green;
    public static final int THEME_COLOR_PURPLE = R.color.theme_purple;

    public static List<Integer> getThemeColors(){
        List<Integer>  colors = new ArrayList<>();
        colors = Arrays.asList(THEME_COLOR_DEFAULT, THEME_COLOR_BLUE, THEME_COLOR_PINK,
                THEME_COLOR_RED, THEME_COLOR_GRAY, THEME_COLOR_YELLOW, THEME_COLOR_ORANGE,
                THEME_COLOR_GREEN, THEME_COLOR_PURPLE);
        return colors;
    }
}
