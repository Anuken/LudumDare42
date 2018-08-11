package io.anuke.ld42.entities;

import io.anuke.ld42.entities.traits.ShadowTrait;
import io.anuke.ucore.entities.impl.DestructibleEntity;
import io.anuke.ucore.entities.trait.DrawTrait;

public abstract class Spark extends DestructibleEntity implements DrawTrait, ShadowTrait{
    public float height = 12f;
    public boolean direction;
    public float walktime;

    public void bullet(BulletType type, float angle){
        Bullet bullet = new Bullet(type, this, angle);
        bullet.set(x, y + height);
        bullet.add();
    }

    @Override
    public int shadowOffsetX(){
        return 0;
    }

    @Override
    public int shadowOffsetY(){
        return 0;
    }

    @Override
    public int shadowSize(){
        return 12;
    }
}
