package io.anuke.ld42.entities;

import io.anuke.ld42.entities.traits.EnemyTrait;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Sounds;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.util.Angles;

import static io.anuke.ld42.Vars.control;
import static io.anuke.ld42.Vars.ui;

public class Player extends Spark{
    private float speed = 3f;

    private float shootTime = -1f;

    public Player(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 2, 4, 4);
        heal();
    }

    @Override
    public boolean collides(SolidTrait other){
        return other instanceof Bullet && ((Bullet)other).getOwner() instanceof EnemyTrait;
    }

    @Override
    public void onDeath(){
        control.reset();
        Sounds.play("death");
    }

    @Override
    public void onHit(SolidTrait entity){
        control.hitTime = 10f;
        Sounds.play("hurt");
    }

    @Override
    public float maxHealth(){
        return 7;
    }

    @Override
    public String name(){
        return "char";
    }

    @Override
    public void draw(){
        float px = x, py = y;
        x = (int)x;
        y = (int)y;

        if(shootTime < 0){
            Draw.grect(name() +
            (walktime > 0 ? "-walk-" + ((int) (walktime / 10f) % 2) : ""), x, y, !direction);
        }else{
            Draw.grect(name() +"-"+(walktime > 0 ? "walk" : "" )+"shoot-1", x, y, !direction);
        }
        x = px;
        y = py;
    }

    @Override
    public void drawShadow(){
        float px = x, py = y;
        x = (int)x;
        y = (int)y;

        super.drawShadow();

        x = px;
        y = py;
    }

    @Override
    public void update(){
        if(ui.dialog.active()) return;

        movement.set(Inputs.getAxis("move_x") * speed, Inputs.getAxis("move_y") * speed).limit(speed).scl(Timers.delta());;
        if(!movement.isZero()){
            direction = (Math.abs(movement.x) > 0 ? movement.x > 0 : movement.y > 0);
            walktime += Timers.delta();
        }else{
            walktime = 0;
        }

        if(shootTime >= 0){
            shootTime -= Timers.delta();
        }

        if(Inputs.keyDown("shoot")){
            if(Timers.get(this, "shoot", 40)){
                shootTime = 20f;
                for(int i = 0; i < 3; i++){
                    Timers.run(i*3, () -> {
                        Sounds.play("shoot");
                        bullet(BulletType.playerBullet, Angles.mouseAngle(x, y + height));
                        Effects.effect(Fx.playershoot, x, y + height);
                    });
                }
            }

            float ang = Angles.mouseAngle(x, y + height);

            direction = !(ang > 90 && ang < 270);
        }

        move(movement.x, movement.y);
    }
}
