package io.anuke.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.graphics.Draw;

public class DialogBox{
    private String text;
    private String face;
    private String character;

    public DialogBox(){
        Core.scene.table(hud -> {
            hud.bottom();

            hud.table("button", box -> {
                BorderImage img = new BorderImage(Draw.region("blank"), 0f);
                img.setColor(Color.GRAY);
                img.update(() -> img.setDrawable(face == null ? "white" : face));

                box.margin(10).left();
                box.add(img).size(64f * 2f).pad(-6).padRight(10);
                box.table(in -> {
                    in.left().top();

                    in.label(() -> character).color(Color.CORAL).padBottom(-1).padTop(-1).growX().left();
                    in.row();
                    in.labelWrap(() -> text).left().top().growX();
                }).growX().growY();
            }).width(600f);
        }).visible(() -> text != null);
    }

    public void display(String character, String face, String text){
        this.character = character;
        this.text = text;
        this.face = face;
    }

}
