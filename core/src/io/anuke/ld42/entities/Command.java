package io.anuke.ld42.entities;

import io.anuke.ld42.Vars;
import io.anuke.ucore.core.Effects;

public enum Command{
    tremors{
        public void run(){
            Effects.shake(10f, 50f, Vars.player);
        }
    };
    public float duration = 60f;
    public abstract void run();
}
