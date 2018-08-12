package io.anuke.ld42.entities;

import io.anuke.ld42.entities.traits.LayerTrait;
import io.anuke.ld42.entities.traits.ShadowTrait;
import io.anuke.ucore.entities.impl.DestructibleEntity;
import io.anuke.ucore.entities.trait.DrawTrait;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Translator;

public abstract class Spark extends DestructibleEntity implements DrawTrait, ShadowTrait, LayerTrait{
    protected static Translator movement = new Translator();

    public float height = 12f;
    public boolean direction;
    public float walktime;

    public void bullet(BulletType type, float angle){
        Bullet bullet = new Bullet(type, this, angle);
        bullet.set(x, y + height);
        bullet.add();
    }

    public void bullet(BulletType type, float x, float y, float angle){
        Bullet bullet = new Bullet(type, this, angle);
        bullet.set(x, y);
        bullet.add();
    }

    public abstract String name();

    @Override
    public void draw(){
        Draw.grect(name() +
        (walktime > 0 ? "-walk-" + ( (int)(walktime / 10f) % 2) : ""), x, y, !direction);
    }

    @Override
    public int shadowOffsetX(){
        return -Mathf.sign(direction)*2;
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
