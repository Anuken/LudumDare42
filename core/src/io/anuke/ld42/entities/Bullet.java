package io.anuke.ld42.entities;

import io.anuke.ucore.entities.EntityPhysics;
import io.anuke.ucore.entities.impl.BaseBulletType;
import io.anuke.ucore.entities.impl.BulletEntity;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.util.Tmp;

public class Bullet extends BulletEntity<BulletType>{

    public Bullet(BulletType type, Entity owner, float angle){
        super(type, owner, angle);
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
}
