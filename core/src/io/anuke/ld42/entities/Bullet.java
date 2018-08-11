package io.anuke.ld42.entities;

import io.anuke.ld42.entities.traits.LayerTrait;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.EntityPhysics;
import io.anuke.ucore.entities.impl.BulletEntity;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.util.Tmp;

public class Bullet extends BulletEntity<BulletType> implements LayerTrait{
    private float lastHit;

    public Bullet(BulletType type, Entity owner, float angle){
        super(type, owner, angle);
    }

    @Override
    public boolean collides(SolidTrait other){
        return super.collides(other ) && Timers.time() - lastHit > 30;
    }

    @Override
    public void collision(SolidTrait other, float x, float y){
        super.collision(other, x, y);
        this.lastHit = Timers.time();
    }

    @Override
    public Layer getLayer(){
        return type.layer;
    }

    @Override
    public void update(){
        super.update();
        getHitboxTile(Tmp.r1);

        if(EntityPhysics.collisions().overlapsTile(Tmp.r1)){
            type.hit(this);
            remove();
        }
    }

    @Override
    public void reset(){
        super.reset();
        this.lastHit = 0;
    }
}
