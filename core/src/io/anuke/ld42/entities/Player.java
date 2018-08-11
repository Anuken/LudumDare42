package io.anuke.ld42.entities;

import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.util.Angles;

public class Player extends Spark{
    private float speed = 3f;

    private static final int blinkPower  = 20;
    private static final float blinkCooldown = 60f;
    private float blinkCooldownTimer = 0f;

    public Player(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 8, 16, 16);
        heal();
    }

    @Override
    public void onDeath(){
        //ui.gameover.show();
        //control.reset();
    }

    @Override
    public float maxHealth(){
        return 5;
    }

    @Override
    public String name(){
        return "char";
    }

    @Override
    public void update(){
        movement.set(Inputs.getAxis("move_x") * speed, Inputs.getAxis("move_y") * speed).limit(speed);
        if(!movement.isZero()){
            direction = (Math.abs(movement.x) > 0 ? movement.x > 0 : movement.y > 0);
            walktime += Timers.delta();
        }else{
            walktime = 0;
        }
        move(movement.x, movement.y);

        if(Inputs.keyDown("shoot") && Timers.get(this, "shoot", 10)){
            bullet(BulletType.testType, Angles.mouseAngle(x, y + height));
        }

        // blink - tap shift while moving to do a quick dash in that direction

        if(blinkCooldownTimer > 0){ // if timer is running
            blinkCooldownTimer -= Timers.delta();
        }

        if(Inputs.keyTap("dash") && !movement.isZero() && blinkCooldownTimer <= 0){ // if moving and pressing shift and cooldown is done
            move(movement.x  * blinkPower, movement.y * blinkPower);
            blinkCooldownTimer = blinkCooldown;
        }

        //TODO teleport - hold shift while no movement keys are pressed to charge a teleport which moves player to cursor when charge is complete
    }
}
