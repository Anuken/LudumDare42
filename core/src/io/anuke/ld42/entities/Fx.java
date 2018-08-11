package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;

public class Fx{
	public static final Effect

	explosion = new Effect(10, e -> {
		Draw.color(Color.YELLOW);
		Fill.circle(e.x, e.y, 20 * e.fin());
		Draw.reset();
	}),

	smoke = new Effect(50, e -> {
		Draw.color(Color.MAROON, Color.BLACK, e.fin());
		Fill.circle(e.x, e.y, 4 * e.fslope());
		Draw.reset();
	}),

	tentahit = new Effect(20, e -> {
		Draw.color(Color.MAROON, Color.BLACK, e.fout());
		Angles.randLenVectors(e.id, 10, e.fin() * 20f, (x, y) -> {
			Fill.circle(x + e.x, y + e.y, e.fout()*4f);
		});
		Draw.reset();
	}),

	hit = new Effect(10, e -> {
		Lines.stroke(3f);
		Draw.color(Color.WHITE, Color.ORANGE, e.fin());
		Lines.spikes(e.x, e.y, 5 + e.fin() * 40f, 10, 8);
		Draw.reset();
	});
	
}
