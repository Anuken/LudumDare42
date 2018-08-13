package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.graphics.Palette;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.util.Mathf;

public class Artifact extends Enemy{

    {
        height = 38;
    }

    @Override
    public void behavior(){

    }

    @Override
    public int shadowSize(){
        return 24;
    }

    @Override
    public int shadowOffsetX(){
        return -1;
    }

    @Override
    public Color lightColor(){
        return Palette.artifact;
    }

    @Override
    public float lightRadius(){
        return 300f;
    }

    @Override
    public void draw(){
        Draw.grect("artifact", x, y);

        float f = Mathf.absin(Timers.time(), 8f, 4f);


        Draw.color(Palette.artifact);
        Draw.alpha(0.5f);
        Fill.circle(x-1, y + height + f, 6f);
        Fill.circle(x-1, y + height + f, 4f);
        Draw.color(Color.WHITE);
        Fill.circle(x-1, y + height + f, 2f);
    }

    @Override
    public float maxHealth(){
        return 200;
    }
}
