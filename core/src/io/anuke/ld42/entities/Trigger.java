package io.anuke.ld42.entities;

import com.badlogic.gdx.maps.MapObject;

public class Trigger{
    public final int pos;
    public final boolean x;
    public final MapObject object;

    public Trigger(int pos, boolean x, MapObject object){
        this.pos = pos;
        this.x = x;
        this.object = object;
    }
}
