package io.anuke.ld42.ui;

import io.anuke.ucore.scene.ui.Dialog;
import static io.anuke.ld42.Vars.*;

public class AboutDialog extends Dialog{

    public AboutDialog(){
        super("About");

        content().left();
        content().margin(10);

        for(String s : aboutText){
            content().add(s).growX().left();
            content().row();
        }

        addCloseButton();
    }
}
