package io.anuke.ld42.ui;

import io.anuke.ld42.entities.Command;

public class DialogEntry{
    public final String name, facepic, text;
    public final Command command;

    public DialogEntry(String name, String facepic, String text){
        this.name = name;
        this.facepic = facepic;
        this.text = text;
        this.command = null;
    }

    public DialogEntry(Command command){
        this.command = command;
        this.name = this.facepic = this.text = null;
    }
}
