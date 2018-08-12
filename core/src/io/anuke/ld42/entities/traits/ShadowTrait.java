package io.anuke.ld42.entities.traits;

import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.graphics.Draw;

public interface ShadowTrait extends Entity{
    int shadowSize();
    int shadowOffsetX();
    int shadowOffsetY();

    default void drawShadow(){
        Draw.rect("shadow" + shadowSize(), getX() + shadowOffsetX(), getY() + shadowOffsetY());
    }
}
