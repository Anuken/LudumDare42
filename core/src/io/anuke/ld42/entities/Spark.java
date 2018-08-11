package io.anuke.ld42.entities;

import io.anuke.ucore.entities.impl.DestructibleEntity;
import io.anuke.ucore.entities.trait.DrawTrait;

public abstract class Spark extends DestructibleEntity implements DrawTrait, ShadowTrait{

    @Override
    public int shadowSize(){
        return 14;
    }
}
