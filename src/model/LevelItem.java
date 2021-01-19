package model;

public enum LevelItem {
    MOUNTAIN('#'), TREE('T'), RANGER('R'), FRUITS('F'), EMPTY(' '), EMPTY_OBSERVED('O');
    LevelItem(char rep){ representation = rep; }
    public final char representation;
}
