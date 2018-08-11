package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.entities.impl.SolidEntity;
import io.anuke.ucore.entities.trait.DrawTrait;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.util.Translator;

public class Player extends SolidEntity implements DrawTrait{
    private Translator movement = new Translator();
    private float speed = 3f;

    public Player(){
        hitboxTile.set(0, 2, 4, 4);
    }

    @Override
    public void draw(){
        Draw.grect("char", x, y);
    }

    @Override
    public void update(){
        movement.set(Inputs.getAxis("move_x") * speed, Inputs.getAxis("move_y") * speed).limit(speed);
        move(movement.x, movement.y);
    }
}
