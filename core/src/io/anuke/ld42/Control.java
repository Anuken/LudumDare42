package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import io.anuke.ld42.GameState.State;
import io.anuke.ld42.entities.*;
import io.anuke.ld42.entities.traits.LayerTrait;
import io.anuke.ld42.entities.traits.LayerTrait.Layer;
import io.anuke.ld42.entities.traits.ShadowTrait;
import io.anuke.ld42.io.MapLoader;
import io.anuke.ld42.ui.DialogEntry;
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
import io.anuke.ucore.lights.PointLight;
import io.anuke.ucore.lights.RayHandler;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.*;

import static io.anuke.ld42.Vars.*;

public class Control extends RendererModule{
	private Array<Entity>[] drawLine = new Array[0];

	private TiledMap map;
	private TiledMapTileLayer wallLayer;
	private TiledMapTileLayer floorLayer;
	private MapLayer objectLayer;

	private Texture fog;

    public Surface effects;
	public float hitTime;

	private float flashDuration = 50f;
    private float flashTime;
    private float timeScale = 1f;
    private Color flashColor;

    private RayHandler rays;
	private PointLight light;

	private Array<Trigger> triggers = new Array<>();

	public Control(){

		Core.cameraScale = 3;
	    Core.batch = new SpriteBatch();
		Core.atlas = new Atlas("sprites.atlas");
	
		KeyBinds.defaults(
			"move_x", new Axis(Input.A, Input.D),
			"move_y", new Axis(Input.S, Input.W),
			"next", Input.SPACE,
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

		Musics.load("sine.ogg");
		Musics.createTracks("intro", "sine");

		fog = new Texture("sprites/fog.png");
		fog.setWrap(TextureWrap.MirroredRepeat, TextureWrap.MirroredRepeat);

		effects = Graphics.createSurface();

		pixelate();

		player = new Player();
		player.add();

		loadMap("map");

		rays = new RayHandler();
		rays.setAmbientLight(Color.WHITE);

		light = new PointLight(rays, 50);
		light.setColor(Color.WHITE);
		light.setDistance(100);
		light.add(rays);
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
		objectLayer = map.getLayers().get("objects");

		triggers.clear();

		for(MapObject obj : objectLayer.getObjects()){
			MapProperties props = obj.getProperties();
			if(props.containsKey("type")){
				int x = (int)(((TiledMapTileMapObject)obj).getX()/tileSize);
				int y =  (int)((((TiledMapTileMapObject)obj).getY())/tileSize);
				triggers.add(new Trigger(props.get("type").equals("x") ? x : y, props.get("type").equals("x"), obj));
			}
		}

		player.set(16, 48);

		Aysa aysa = new Aysa();
		aysa.set(player.x, player.y - 50);
		aysa.add();

		CaveCreature c = new CaveCreature();
		c.set(floorLayer.getWidth() * tileSize/2f, floorLayer.getHeight() * tileSize/2f);
		c.add();

		enemy = c;

		EntityPhysics.initPhysics(0, 0, wallLayer.getWidth() * tileSize, wallLayer.getHeight() * tileSize);
	}
	
	@Override
	public void update(){
		light.setPosition(player.x, player.y);

		if(Inputs.keyTap("next")){
			ui.dialog.next();
		}
		
		//TODO remove
		if(Inputs.keyDown(Input.ESCAPE)){
			Gdx.app.exit();
        }

        if(enemy != null && enemy.isDead()){
		    enemy = null;
        }

        int px = (int)(player.x / tileSize), py = (int)(player.y / tileSize);

        for(Trigger trigger : triggers){
			if((!trigger.x && trigger.pos == px) || (trigger.x && trigger.pos == py)){
				MapProperties props = trigger.object.getProperties();
				if(props.containsKey("text")){
					Array<DialogEntry> entries = new Array<>();
					String[] dialog = ((String)props.get("text")).split("\n");
					for(String s : dialog){
						String facepic = s.substring(0, s.indexOf(':'));
						String name = Strings.capitalize(facepic.substring(0, facepic.indexOf(' ')));
						String text = s.substring(s.indexOf(": ") + 3);
						text = text.substring(0, text.length()-1);
						entries.add(new DialogEntry(name, facepic, text));
					}
					ui.dialog.display(entries);
				}else{
					throw new RuntimeException("Invalid trigger.");
				}
				triggers.removeValue(trigger, true);
				break;
			}
		}

        player.x = Mathf.clamp(player.x, 0, tileSize * floorLayer.getWidth());
		player.y = Mathf.clamp(player.y, 16, tileSize * floorLayer.getHeight());

		smoothCamera(player.x, player.y, 0.1f);
		limitCamera(6f, player.x, player.y);
		updateShake();
		clampCamera(0, 16, tileSize * floorLayer.getWidth(), tileSize * floorLayer.getHeight());

		if(GameState.is(State.playing)){
			Entities.update();
			Timers.update();
			
			if(Inputs.keyTap("pause")){
				GameState.set(State.paused);
				ui.paused.show();
			}
		}else if(GameState.is(State.paused)){
			if(Inputs.keyTap("pause") && !ui.dialog.active()){
				GameState.set(State.playing);
				ui.paused.hide();
			}
		}

		drawDefault();
		if(drawLights){
			rays.setCombinedMatrix(Core.camera);
			rays.updateAndRender();
		}
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
			sh.drawShadow();
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

        if(GameState.is(State.intro)){
			drawIntro();
		}
	}

	@Override
	public void resize(){
		drawLine = new Array[(int)(8 + Core.camera.viewportHeight / tileSize)];
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i] = new Array<>();
		}

		rays.resizeFBO(Gdx.graphics.getWidth()/Core.cameraScale, Gdx.graphics.getHeight()/Core.cameraScale);
	}

	Layer getLayer(Entity entity){
		if(!(entity instanceof LayerTrait)) return Layer.sorted;
		return ((LayerTrait) entity).getLayer();
	}

	void drawIntro(){

		Draw.color(Color.BLACK);
		Draw.alpha(Mathf.clamp(1f-ui.intro.fadeInTime));
		Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.viewportWidth, Core.camera.viewportHeight);

		float time = ui.intro.time;

		Graphics.setAdditiveBlending();
		Draw.color(Color.SCARLET);
		for(int i = 0; i < 100; i++){
			float rx = Mathf.randomSeedRange(i+1, Core.camera.viewportWidth/2f) + Core.camera.position.x +
						Mathf.sin(time + i*312, 100f+i*3, 100f-i);
			float ry = Mathf.randomSeedRange(i+2, Core.camera.viewportHeight/2f) + Core.camera.position.y +
						Mathf.sin(time + i*254, 80f+i*2, -100f+i);
			float rs = 0.8f + Mathf.absin(time +i*412, 40f, 0.5f);
			Draw.alpha(0.5f * ui.intro.particleFadeTime);
			Fill.circle(rx, ry, 15f*rs);
			Fill.circle(rx, ry, 10f*rs);
			Fill.circle(rx, ry, 5f*rs);
		}
		Draw.color();
		Graphics.setNormalBlending();
	}

	void drawFog(){
	    float f = fog.getWidth();
	    float scl = 1f;
	    float tscl = 2000f;

		for(int i = 0; i < 3; i++){
			Draw.colorl(0.3f - i * 0.05f);
			Draw.alpha(0.3f - i*0.05f);

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
