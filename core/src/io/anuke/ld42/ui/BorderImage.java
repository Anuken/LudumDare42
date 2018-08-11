package io.anuke.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.layout.Unit;
import io.anuke.ucore.util.Tmp;

public class BorderImage extends Image{
    private float thickness = 3f;

    public BorderImage(){
    }

    public BorderImage(Texture texture){
        super(texture);
    }

    public BorderImage(Texture texture, float thick){
        super(texture);
        thickness = thick;
    }

    public BorderImage(TextureRegion region, float thick){
        super(region);
        thickness = thick;
    }

    @Override
    public void draw(Batch batch, float alpha){
        Tmp.c1.set(getColor());
        setColor(Color.WHITE);
        super.draw(batch, alpha);
        setColor(Tmp.c1);

        float scaleX = getScaleX();
        float scaleY = getScaleY();

        Draw.color(getColor());
        Lines.stroke(Unit.dp.scl(thickness));
        Lines.rect(x + imageX, y + imageY, imageWidth * scaleX, imageHeight * scaleY);
        Draw.reset();
    }
}
