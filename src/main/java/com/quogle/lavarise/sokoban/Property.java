package com.quogle.lavarise.sokoban;

public enum Property {
    SELECTABLE,
    ICE,       // Causes sliding
    FIRE,      // Can burn certain tiles or objects
    WATER;

    public boolean isTransferable() {
        return this != SELECTABLE;
    }


    public void Ice() {
        return;
    }
}

