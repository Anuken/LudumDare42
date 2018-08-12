package io.anuke.ld42.entities;

import io.anuke.ld42.entities.traits.EnemyTrait;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.EntityPhysics;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.entities.trait.HealthTrait;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.util.Mathf;

public class Aysa extends Spark{
    Entity target;
    boolean strafe;

    public Aysa(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 2, 4, 4);
    }

    @Override
    public boolean collides(SolidTrait other){
        return other instanceof Bullet && ((Bullet)other).getOwner() instanceof EnemyTrait;
    }

    @Override
    public void update(){
        if(target == null){
            target = EntityPhysics.getClosest(Entities.defaultGroup(), x, y, 1000f, e -> e instanceof CaveCreature);
        }

        if(target == null || ((HealthTrait)target).isDead()) return;

        if(Timers.get(this, "shoot", 40)){
            bullet(BulletType.aysaBullet, angleTo(target));
        }

        float speed = 1.5f;
        movement.set(x, y).sub(target.getX(), target.getY()).rotate(strafe ? 90 : 270).setLength(speed);

        if(Mathf.chance(0.01 * Timers.delta())){
            strafe = !strafe;
        }

        direction = movement.x > 0;
        walktime += Timers.delta();
        move(movement.x, movement.y);
    }

    @Override
    public String name(){
        return "aysa";
    }

    @Override
    public float maxHealth(){
        return 10;
    }
}
