package io.anuke.ld42.entities;

import com.badlogic.gdx.utils.Array;
import io.anuke.ld42.ui.DialogEntry;

public class Trigger{
    public final int pos;
    public final boolean x;
    public final Class<?> enemy;
    public final Array<DialogEntry> dialog;

    public Trigger(Class<?> enemy, int pos, boolean x, Array<DialogEntry> dialog){
        this.enemy = enemy;
        this.pos = pos;
        this.x = x;
        this.dialog = dialog;
    }
}
