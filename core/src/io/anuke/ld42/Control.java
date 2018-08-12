package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import io.anuke.ld42.GameState.State;
import io.anuke.ld42.entities.Aysa;
import io.anuke.ld42.entities.CaveCreature;
import io.anuke.ld42.entities.LayerEffect;
import io.anuke.ld42.entities.Player;
import io.anuke.ld42.entities.traits.LayerTrait;
import io.anuke.ld42.entities.traits.LayerTrait.Layer;
import io.anuke.ld42.entities.traits.ShadowTrait;
import io.anuke.ld42.io.MapLoader;
import io.anuke.ucore.core.*;
import io.anuke.ucore.core.Inputs.Axis;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.EntityDraw;
import io.anuke.ucore.entities.EntityPhysics;
import io.anuke.ucore.entities.impl.EffectEntity;
import io.anuke.ucore.entities.trait.DrawTrait;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.graphics.Surface;
import io.anuke.ucore.input.Input;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.Atlas;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Pooling;
import io.anuke.ucore.util.Tmp;

import static io.anuke.ld42.Vars.*;

public class Control extends RendererModule{
	private Array<Entity>[] drawLine = new Array[0];

	private TiledMap map;
	private TiledMapTileLayer wallLayer;
	private TiledMapTileLayer floorLayer;

	private Texture fog;

    public Surface effects;
	public float hitTime;

	private float flashDuration = 50f;
    private float flashTime;
    private float timeScale = 1f;
    private Color flashColor;
	
	public Control(){
		Core.cameraScale = 3;
	    Core.batch = new SpriteBatch();
		Core.atlas = new Atlas("sprites.atlas");
	
		KeyBinds.defaults(
			"move_x", new Axis(Input.A, Input.D),
			"move_y", new Axis(Input.S, Input.W),
			"dash", Input.SPACE, // dash key
            "teleport", Input.SHIFT_LEFT, // teleport key
			"shoot", Input.MOUSE_LEFT,
			"pause", Input.ESCAPE
		);

		Effects.setShakeFalloff(40000);
		
		Settings.loadAll("io.anuke.ld42");
		Timers.setDeltaProvider(() -> Gdx.graphics.getDeltaTime() * 60f * timeScale);

		EntityPhysics.initPhysics();
		EntityPhysics.collisions().setCollider(tileSize,
		(x, y) -> wallLayer.getCell(x, y) != null && wallLayer.getCell(x, y).getTile().getProperties().containsKey("solid"),
		(x, y, rect) -> rect.setSize(tileSize).setPosition(x*tileSize - tileSize/2f, y*tileSize));

		Effects.setEffectProvider((effect, color, x, y, rotation, data) -> {
			EffectEntity entity = Pooling.obtain(LayerEffect.class);
			entity.effect = effect;
			entity.color = color;
			entity.rotation = rotation;
			entity.data = data;
			entity.set(x, y);
			entity.add();
		});

		fog = new Texture("sprites/fog.png");
		fog.setWrap(TextureWrap.MirroredRepeat, TextureWrap.MirroredRepeat);

		effects = Graphics.createSurface();

		pixelate();

		player = new Player();
		player.add();

		loadMap("map");
	}

    public void flash(Color color){
        flashTime = flashDuration;
        flashColor = color;
    }

	public void reset(){
		player.heal();
	    //TODO reset game state
	}

	public void loadMap(String name){
		map = new MapLoader().load("maps/" + name + ".tmx");
		floorLayer = (TiledMapTileLayer) map.getLayers().get("floor");
		wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");

		player.set(floorLayer.getWidth() * tileSize / 2f, floorLayer.getHeight() * tileSize/2f);

		Aysa aysa = new Aysa();
		aysa.set(player.x, player.y - 50);
		aysa.add();

		CaveCreature c = new CaveCreature();
		c.set(player.x, player.y + 50);
		c.add();

		enemy = c;

		EntityPhysics.initPhysics(0, 0, wallLayer.getWidth() * tileSize, wallLayer.getHeight() * tileSize);
	}
	
	@Override
	public void update(){
		//ui.dialog.display("Aysa", "aysa_default", "lorem ipsumeee text text text text sentence sentance sentience centennial");
		
		//TODO remove
		if(Inputs.keyDown(Input.ESCAPE)){
			Gdx.app.exit();
        }

        if(enemy != null && enemy.isDead()){
		    enemy = null;
        }
		
		if(GameState.is(State.playing)){
			Entities.update();
			Timers.update();
			smoothCamera(player.x, player.y, 0.1f);
			limitCamera(6f, player.x, player.y);
			updateShake();
			
			if(Inputs.keyTap("pause")){
				GameState.set(State.paused);
				ui.paused.show();
			}
		}else if(GameState.is(State.paused)){
			if(Inputs.keyTap("pause")){
				GameState.set(State.playing);
				ui.paused.hide();
			}
		}

		drawDefault();
		record();
	}
	
	@Override
	public void draw(){
		Draw.color();

		//insert entities into draw lines
		for(Entity entity : Entities.defaultGroup().all()){
			float y = (entity instanceof LayerTrait ? ((LayerTrait) entity).getLayerY() : entity.getY());
			float scl = (y - Core.camera.position.y);
			int position = (int)(scl / tileSize) + drawLine.length/2;
			if(entity instanceof DrawTrait && getLayer(entity) == Layer.sorted && position > 0 && position < drawLine.length){
				drawLine[position].add(entity);
			}
		}

		int drawRangeX = (int)(Core.camera.viewportWidth/2 + 1);
		int camx = Mathf.scl(Core.camera.position.x, tileSize), camy = Mathf.scl(Core.camera.position.y, tileSize);

		//draw floor
		for(int y = drawLine.length/2 - 1; y >= -drawLine.length/2; y --){
			for(int x = -drawRangeX; x <= drawRangeX; x++){
				int worldx = camx + x, worldy = camy + y;
				Cell cell = floorLayer.getCell(worldx, worldy);
				if(cell == null) continue;
				Draw.rect(cell.getTile().getTextureRegion(), worldx * tileSize, worldy * tileSize + cell.getTile().getTextureRegion().getRegionHeight()/2f);
			}
		}

		EntityDraw.draw(Entities.defaultGroup(), entity -> getLayer(entity) == Layer.floor);

		//draw shadows
		Graphics.surface(effects);
		for(Entity entity : Entities.defaultGroup().all()){
			if(!(entity instanceof ShadowTrait)) continue;
			ShadowTrait sh = (ShadowTrait)entity;
			Draw.rect("shadow" + sh.shadowSize(), entity.getX(), entity.getY());
		}

		//draw wall shadows
		for(int y = drawLine.length/2 - 1; y >= -drawLine.length/2; y --){
			for(int x = -drawRangeX; x <= drawRangeX; x++){
				int worldx = camx + x, worldy = camy + y;
				Cell cell = wallLayer.getCell(worldx, worldy);
				if(cell == null) continue;

				float t = tileSize/2f;
				float cx = worldx*tileSize, cy = worldy*tileSize + tileSize/2f;
				float mv = 20f;
				float sx = 1, sy = 2f;
				float x1 = cx + t, y1 = cy + t, x2 = cx - t, y2 = cy - t,
				x3 = x2 - mv*sx, y3 = y2 + mv*sy, x4 = cx - t - mv*sx, y4 = cy + t + mv*sy, x5 = x1 - mv*sx, y5 = y1 + mv*sy;
				Fill.quad(x1, y1, x2, y2, x3, y3, x4, y4);
				Fill.tri(x1, y1, x4, y4, x5, y5);
			}
		}

		Draw.color(0, 0, 0, shadowOpacity);
		Graphics.flushSurface();
		Draw.color();

		//draw walls
		for(int y = drawLine.length/2 - 1; y >= -drawLine.length/2; y --){
			Array<Entity> line = drawLine[y + drawLine.length/2];

			Sort.instance().sort(line, (a, b) -> -Float.compare(a.getY(), b.getY()));
			for(Entity entity : line){
				((DrawTrait)entity).draw();
			}

			for(int x = -drawRangeX; x <= drawRangeX; x++){
				int worldx = camx + x, worldy = camy + y;
				Cell cell = wallLayer.getCell(worldx, worldy);
				if(cell == null) continue;
				Draw.rect(cell.getTile().getTextureRegion(), worldx * tileSize, worldy * tileSize + cell.getTile().getTextureRegion().getRegionHeight()/2f);
			}
		}

		//clear draw lines
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i].clear();
		}

		EntityDraw.draw(Entities.defaultGroup(), entity -> getLayer(entity) == Layer.wall);

		drawFog();

		//draw hit flash
		if(hitTime > 0){
			Draw.color(Color.SCARLET);
			Draw.alpha(Mathf.clamp(hitTime/5f));
			Lines.stroke(4f);
			Lines.crect(Core.camera.position.x, Core.camera.position.y, Core.camera.viewportWidth, Core.camera.viewportHeight);
			Draw.reset();
			hitTime -= Timers.delta();
		}

        if(flashTime > 0){
            Draw.color(flashColor);
            Draw.alpha(Interpolation.fade.apply(Mathf.clamp(flashTime/flashDuration)));
            Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.viewportWidth, Core.camera.viewportHeight);
            Draw.color();

            flashTime -= Gdx.graphics.getDeltaTime() * 60f;
        }

        if(flashTime / flashDuration < 0.5f){
            timeScale = Mathf.lerp(timeScale, 1f, Gdx.graphics.getDeltaTime() * 60f * 0.01f);
        }else{
            timeScale = Mathf.lerp(timeScale, 0f, Gdx.graphics.getDeltaTime() * 60f * 0.2f);
        }
	}

	@Override
	public void resize(){
		drawLine = new Array[(int)(8 + Core.camera.viewportHeight / tileSize)];
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i] = new Array<>();
		}
	}

	Layer getLayer(Entity entity){
		if(!(entity instanceof LayerTrait)) return Layer.sorted;
		return ((LayerTrait) entity).getLayer();
	}

	void drawFog(){
	    float f = fog.getWidth();
	    float scl = 1f;
	    float tscl = 2000f;

		for(int i = 0; i < 3; i++){
			Draw.colorl(0.3f - i * 0.05f);
			Draw.alpha(0.2f - i*0.05f);

			float fx = Core.camera.position.x / f + Timers.time()/(tscl - i *100) + i *0.632f,
				  fy = Core.camera.position.y/f + i *0.321f;
			float uw = Core.camera.viewportWidth/fog.getWidth()/2f/scl;
			float uh = Core.camera.viewportHeight/fog.getHeight()/2f/scl;

			Core.batch.draw(fog,
			Core.camera.position.x - Core.camera.viewportWidth/2f,
			Core.camera.position.y - Core.camera.viewportHeight/2f,
			Core.camera.viewportWidth,
			Core.camera.viewportHeight,
			fx - uw, fy - uh,
			fx + uw, fy + uh);
		}

        Draw.color();
    }

	void drawDebug(){

		for(Entity entity : Entities.all()){
			if(!(entity instanceof SolidTrait)) continue;
			SolidTrait solid = (SolidTrait)entity;
			solid.getHitboxTile(Tmp.r1);
			Draw.color(Color.GREEN);
			Lines.rect(Tmp.r1);
			Draw.color(Color.RED);
			solid.getHitbox(Tmp.r1);
			Lines.rect(Tmp.r1);
		}
		Draw.color();
	}
}
