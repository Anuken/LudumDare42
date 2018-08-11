package io.anuke.ld42.dialogs;

import io.anuke.ucore.scene.ui.Dialog;
import static io.anuke.ld42.Vars.*;

public class TutorialDialog extends Dialog{

    public TutorialDialog(){
        super("Tutorial");

        content().left();
        content().margin(10);

        for(String s : tutorialText){
            content().add(s);
            content().row();
        }

        addCloseButton();
    }
}
