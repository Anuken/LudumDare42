package io.anuke.ld42.entities.traits;

import io.anuke.ucore.entities.trait.Entity;

public interface LayerTrait extends Entity{

    default Layer getLayer(){
        return Layer.sorted;
    }
    default float getLayerY(){
        return getY();
    }

    enum Layer{
        sorted, wall, floor
    }
}
