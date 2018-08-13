package io.anuke.ld42.entities;

import io.anuke.ld42.Vars;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;

import static io.anuke.ld42.Vars.ui;

public enum Command{
    tremors{
        public void run(){
            Effects.shake(10f, 50f, Vars.player);
        }
    },
    wake{
        @Override
        public void run(){
            Timers.run(60f, () -> Vars.enemy.setActive(true));
        }
    },
    exit{
        @Override
        public void run(){
            ui.win.show();
        }
    },
    blackscreen{
        public void run(){
            Vars.control.black = true;
        }

    };
    public abstract void run();
}
