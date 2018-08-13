package io.anuke.ld42.entities;

import io.anuke.ld42.Vars;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;

public enum Command{
    tremors{
        public void run(){
            Effects.shake(10f, 50f, Vars.player);
        }
    },
    wakeCaveBeast{
        @Override
        public void run(){
            Timers.run(60f, () -> ((CaveCreature)Vars.enemy).shooting = true);
        }
    };
    public float duration = 60f;
    public abstract void run();
}
