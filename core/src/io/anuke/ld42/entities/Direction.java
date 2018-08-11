package io.anuke.ld42.entities;

public enum Direction {
    back("back", false),
    left("side", true),
    right("side", false);

    public final boolean flipped;
    public final String texture;

    Direction(String texture, boolean flipped){
        this.texture = texture;
        this.flipped = flipped;
    }
}