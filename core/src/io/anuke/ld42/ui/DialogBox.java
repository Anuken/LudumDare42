package io.anuke.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.graphics.Draw;

public class DialogBox{
    private String text;
    private String face;
    private String character;

    private Array<DialogEntry> dialog = new Array<>();

    public DialogBox(){
        Core.scene.table(hud -> {
            hud.bottom();

            hud.table("button", box -> {
                BorderImage img = new BorderImage(Draw.region("blank"), 0f);
                img.setColor(Color.GRAY);
                img.update(() -> img.setDrawable(face == null ? Draw.region("clear") : Draw.region(face)));

                box.margin(10).left();
                box.add(img).size(64f * 2f).pad(-6).padRight(10);
                box.table(in -> {
                    in.left().top();

                    in.label(() -> character).color(Color.CORAL).padBottom(-1).padTop(-1).growX().left();
                    in.row();
                    in.labelWrap(() -> text).left().top().growX();
                }).growX().growY();
            }).width(600f);
        }).visible(() -> text != null && face != null && character != null);
    }

    public boolean active(){
        return dialog.size > 0;
    }

    public void next(){
        if(dialog.size == 0){
            text = face = character = null;
            return;
        }
        display(dialog.pop());
    }

    public void display(Array<DialogEntry> dialog){
        this.dialog.clear();
        this.dialog.addAll(dialog);
        this.dialog.reverse();
        next();
    }

    private void display(DialogEntry entry){
        this.character = entry.name;
        this.face = entry.facepic;
        this.text = entry.text;
    }

}
