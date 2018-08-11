package io.anuke.ld42.entities;

import io.anuke.ld42.entities.traits.LayerTrait;
import io.anuke.ucore.entities.impl.EffectEntity;

public class LayerEffect extends EffectEntity implements LayerTrait{

    @Override
    public Layer getLayer(){
        return Layer.wall;
    }
}
