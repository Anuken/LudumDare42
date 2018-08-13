package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import io.anuke.ld42.GameState.State;
import io.anuke.ld42.entities.*;
import io.anuke.ld42.entities.traits.EnemyTrait;
import io.anuke.ld42.entities.traits.LayerTrait;
import io.anuke.ld42.entities.traits.LayerTrait.Layer;
import io.anuke.ld42.entities.traits.ShadowTrait;
import io.anuke.ld42.graphics.Palette;
import io.anuke.ld42.graphics.Shaders;
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
import io.anuke.ucore.graphics.*;
import io.anuke.ucore.input.Input;
import io.anuke.ucore.lights.PointLight;
import io.anuke.ucore.lights.RayHandler;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.*;

import static io.anuke.ld42.Vars.*;

public class Control extends RendererModule{
    private static float limit = 240f;

	private Array<Entity>[] drawLine = new Array[0];

	private TiledMap map;
	private TiledMapTileLayer wallLayer;
	private TiledMapTileLayer floorLayer;
	private boolean[][] occluded;
	private boolean[][] solid;
	private MapLayer objectLayer;

	private Texture fog;
	private FrameBuffer fbo;

    public Surface effects;
	public float hitTime;

	private float flashDuration = 50f;
    private float flashTime;
    private float timeScale = 1f;
    private Color flashColor;

    public RayHandler rays;
	private PointLight light;

	private float limitx, limity;
	private Array<Trigger> triggers = new Array<>();
	private ObjectMap<String, GridPoint2> markers = new ObjectMap<>();
	private Class<?> lastEnemy;

	public boolean black;

    private String[] noises = {"waterdrop", "waterdrop2", "switch1"};
    private double noiseChance = 0.003;

	public Control(){

		Core.cameraScale = 3;
	    Core.batch = new SpriteBatch();
		Core.atlas = new Atlas("sprites.atlas");
	
		KeyBinds.defaults(
			"move_x", new Axis(Input.A, Input.D),
			"move_y", new Axis(Input.S, Input.W),
			"next", Input.SPACE,
			"dash", Input.SPACE,
			"shoot", Input.MOUSE_LEFT,
			"pause", Input.ESCAPE
		);

		Musics.setFadeTime(150f);

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

		Sounds.load("blobdie.mp3", "death.mp3", "distortroar.mp3", "growl1.mp3", "hurt.mp3", "pickup.wav", "respawn.mp3", "scream.mp3", "shoot.mp3", "slash.mp3", "slash2.mp3", "switch1.mp3", "tentadie.wav", "waterdrop.mp3", "waterdrop2.mp3");

		Musics.load("sine.ogg", "ambient.ogg", "artifact.mp3", "tenta.mp3", "wraith.mp3");
		Musics.createTracks("intro", "sine");
        Musics.createTracks("ambient", "ambient");
        Musics.createTracks("artifact", "artifact");
        Musics.createTracks("tenta", "tenta");
        Musics.createTracks("wraith", "wraith");

		fog = new Texture("sprites/fog.png");
		fog.setWrap(TextureWrap.MirroredRepeat, TextureWrap.MirroredRepeat);

		effects = Graphics.createSurface();

		pixelate();

		player = new Player();
		player.add();

		loadMap("map");

		rays = new RayHandler();

		light = new PointLight(rays, raynum);
		light.setColor(Color.valueOf("f8c79c"));
		light.setDistance(100);
		light.add(rays);
	}

	public void wall(int x, int y, int id){
	    if(id == 0 && wallLayer.getCell(x, y) != null){
	        wallLayer.setCell(x, y, null);
            Effects.effect(Fx.pillardown, x * tileSize, y*tileSize + tileSize/2f);
        }else if(id != 0 && wallLayer.getCell(x, y) == null){
            Cell cell = new Cell();
            cell.setTile(map.getTileSets().getTile(id));
            wallLayer.setCell(x, y, cell);
            Effects.effect(Fx.pillarup, x * tileSize, y*tileSize + tileSize/2f);
        }
    }

    public void flash(Color color){
        flashTime = flashDuration;
        flashColor = color;
    }

	public void reset(){
		Timers.run(1f, () -> {
			flash(Color.SCARLET);
			for(Entity entity : Entities.all()){
				if(entity instanceof Bullet || entity instanceof EffectEntity){
					entity.remove();
				}
			}
			enemy.remove();

			try{
                String checkpoint = ClassReflection.getSimpleName(lastEnemy);
			    EnemyTrait e = (EnemyTrait) ClassReflection.newInstance(lastEnemy);
			    e.set(markers.get(checkpoint).x * tileSize, markers.get(checkpoint).y * tileSize);
			    e.add();
			    enemy = e;
			    Command.wake.run();
            }catch(Exception e){
			    throw new RuntimeException(e);
            }
			String checkpoint = "checkpoint_"+ClassReflection.getSimpleName(lastEnemy).toLowerCase();
			player.set(markers.get(checkpoint).x * tileSize, markers.get(checkpoint).y * tileSize);
			player.heal();
			Command.wake.run();
		});
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
			int x = (int)(((TiledMapTileMapObject)obj).getX()/tileSize);
			int y = (int)((((TiledMapTileMapObject)obj).getY())/tileSize);

			if(props.containsKey("type") && props.containsKey("text")){
				Array<DialogEntry> entries = getEntries((String)props.get("text"));

				Class<?> enemyClass = null;
				try{
				    enemyClass = ClassReflection.forName("io.anuke.ld42.entities." + props.get("enemy"));
                }catch(Exception e){}


				String type = (String) props.get("type");
				if(type.equals("x")){
					triggers.add(new Trigger(enemyClass, y, true, entries));
				}else if(type.equals("y")){
					triggers.add(new Trigger(enemyClass, x, false, entries));
				}else{
					//TODO
				}
			}

			if(obj.getName() != null){
                try{
                    Class<?> enemyClass = ClassReflection.forName("io.anuke.ld42.entities." + obj.getName());
                    EnemyTrait e = (EnemyTrait)ClassReflection.newInstance(enemyClass);
                    e.setActive(false);
                    e.set(x * tileSize, y * tileSize);
                    e.add();
                }catch(Exception e){}
			    markers.put(obj.getName(), new GridPoint2(x, y));
            }
		}

		String start = (debug ? "startpoint_debug" : "startpoint");

		player.set(markers.get(start).x * tileSize, markers.get(start).y * tileSize);

		aysa = new Aysa();
		aysa.set(markers.get("spawnpoint_aysa").x * tileSize, markers.get("spawnpoint_aysa").y * tileSize);
		aysa.add();

		fbo = new FrameBuffer(Format.RGBA8888, wallLayer.getWidth(), wallLayer.getHeight(), false);
		Core.batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());
		fbo.begin();
		Graphics.begin();

		occluded = new boolean[wallLayer.getWidth()][wallLayer.getHeight()];
		solid = new boolean[wallLayer.getWidth()][wallLayer.getHeight()];

		Draw.color(Color.BLACK);
		for(int x = 0; x < fbo.getWidth(); x++){
			outer:
			for(int y = 0; y < fbo.getHeight(); y++){
				Cell cell = wallLayer.getCell(x, y);
				solid[x][y] = cell!= null && cell.getTile().getProperties().containsKey("solid");
				if(cell != null){
					for(GridPoint2 p : Geometry.d4){
						Cell f = wallLayer.getCell(x + p.x, y + p.y);
						if(f == null){
							occluded[x][y] = true;
							continue outer;
						}
					}
					Fill.crect(x, fbo.getHeight() - 1 - y, 1, 1);
				}
			}
		}
		Draw.color();
		Graphics.end();
		fbo.end();

		EntityPhysics.initPhysics(0, 0, wallLayer.getWidth() * tileSize, wallLayer.getHeight() * tileSize);
	}

	public void displayDeath(EnemyTrait enemy){
		String name = ClassReflection.getSimpleName(enemy.getClass());
		Array<DialogEntry> arr = getEntries((String)objectLayer.getObjects().get(name).getProperties().get("text"));
		Timers.run(10f, () -> ui.dialog.display(arr));
	}

	Array<DialogEntry> getEntries(String raw){
		Array<DialogEntry> entries = new Array<>();
		String[] dialog = raw.split("\n");
		for(String s : dialog){
			if(s.isEmpty()) continue;
			if(s.startsWith(":")){
				entries.add(new DialogEntry(Command.valueOf(s.substring(1))));
				continue;
			}
			String facepic = s.substring(0, s.indexOf(':'));
			String dname = Strings.capitalize(facepic.substring(0, facepic.indexOf(' ')));
			String text = s.substring(s.indexOf(": ") + 3);
			text = text.substring(0, text.length()-1);
			entries.add(new DialogEntry(dname, facepic, text));
		}
		return entries;
	}
	
	@Override
	public void update(){
		light.setPosition(player.x, player.y);

        rays.setAmbientLight(Hue.lightness(0.8f - player.y/3000f));

        if(GameState.is(State.playing)){
        	if(enemy != null){
				if(enemy instanceof Artifact){
					Musics.playTracks("artifact");
				}else if(enemy instanceof CaveBeast){
					Musics.playTracks("tenta");
				}else{
					Musics.playTracks("wraith");
				}
			}else{
				Musics.playTracks("ambient");
			}
        }

		if(Inputs.keyTap("next")){
			ui.dialog.next();
		}

        if(enemy != null && enemy.isDead()){
		    enemy = null;
        }

        int px = (int)(player.x / tileSize), py = (int)(player.y / tileSize);

        for(Trigger trigger : triggers){
			if(enemy == null && (!trigger.x && trigger.pos == px) || (trigger.x && trigger.pos == py)){
			    if(trigger.enemy != null){
			        lastEnemy = trigger.enemy;
			        for(Entity e : Entities.all()){
			            if(e.getClass() == trigger.enemy){
			                enemy = (EnemyTrait) e;
			                limitx = enemy.getX();
			                limity = enemy.getY();
			                break;
                        }
                    }
                    if(debug) Command.wake.run();
                }
				if(!debug) ui.dialog.display(trigger.dialog);
				triggers.removeValue(trigger, true);
				break;
			}
		}

        player.x = Mathf.clamp(player.x, tileSize*2f, tileSize * floorLayer.getWidth() - tileSize*2f);
		player.y = Mathf.clamp(player.y, tileSize*3f, tileSize * floorLayer.getHeight() - tileSize*2f);

		updateShake();

		if(GameState.is(State.playing)){
            if(Mathf.chance(noiseChance * Timers.delta())){
                Tmp.v1.setToRandomDirection().setLength(Mathf.random(10f, 200f));
                Sounds.play(noises[Mathf.random(0, noises.length-1)], Mathf.random(0.6f));
            }

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

        if(enemy != null){
            Tmp.v1.set(player.x, player.y).sub(limitx, limity).limit(limit);
            player.x = limitx + Tmp.v1.x;
            player.y = limity + Tmp.v1.y;
        }

        if(!ui.dialog.isAysa()){
			smoothCamera((int) player.x, (int) player.y, 0.1f);
			limitCamera(5f, (int) player.x, (int) player.y);
		}else{
			smoothCamera(aysa.x, aysa.y, 0.1f);
		}

		float pcx = Core.camera.position.x, pcy = Core.camera.position.y;
		Core.camera.position.x = (int)Core.camera.position.x;
		Core.camera.position.y = (int)Core.camera.position.y;

        clampCamera(tileSize*2f, tileSize*3, tileSize * floorLayer.getWidth() - tileSize*3.5f, tileSize * floorLayer.getHeight() - tileSize*2f);

        drawDefault();
		if(drawLights){
			rays.setCombinedMatrix(Core.camera);
			rays.updateAndRender();
		}
		record();

		Core.camera.position.x = pcx;
		Core.camera.position.y = pcy;
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

				if(!solid[worldx][worldy]){
					Draw.rect("shadow" + cell.getTile().getProperties().get("shadow", 16, Integer.class), worldx * tileSize, worldy * tileSize+2);
				}else if(occluded[worldx][worldy]){

					float t = tileSize / 2f;
					float cx = worldx * tileSize, cy = worldy * tileSize + tileSize / 2f;

					//float ang = Mathf.atan2(player.x - cx, player.y - cy) + 180f;
					//float dst = Mathf.dst(player.x - cx, player.y - cy);
					//float len = 14f + Mathf.clamp(dst/3f, 0, 60);
					//Core.batch.draw(Draw.region("shadow"), cx - 5, cy - 9, 5, 9, len, 18f, 1f, 1f, ang);

					float mv = 20f;
					float sx = 1, sy = 2f;
					float x1 = cx + t, y1 = cy + t, x2 = cx - t, y2 = cy - t,
					x3 = x2 - mv * sx, y3 = y2 + mv * sy, x4 = cx - t - mv * sx, y4 = cy + t + mv * sy, x5 = x1 - mv * sx, y5 = y1 + mv * sy;
					Fill.quad(x1, y1, x2, y2, x3, y3, x4, y4);
					Fill.tri(x1, y1, x4, y4, x5, y5);
				}
			}
		}

		Draw.color(0, 0, 0, shadowOpacity);
		Graphics.flushSurface();
		Draw.color();

		drawFog();

		if(enemy != null){
			Lines.stroke(1f);
			Draw.color(Color.DARK_GRAY, Palette.eikan, Mathf.absin(Timers.time(), 20f, 1f));
			Lines.poly(limitx, limity, 100, limit);
			Draw.reset();
		}

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

		Graphics.shader(Shaders.fog);
		Core.batch.draw(fbo.getColorBufferTexture(), -tileSize/2f, tileSize, fbo.getWidth() * tileSize, fbo.getHeight() * tileSize);
		Graphics.shader();

		//clear draw lines
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i].clear();
		}

		EntityDraw.draw(Entities.defaultGroup(), entity -> getLayer(entity) == Layer.wall);

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

        if(black){
		    Draw.color(Color.BLACK);
		    Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.viewportWidth, Core.camera.viewportHeight);
		    Draw.color();
        }

        if(GameState.is(State.intro)){
			drawIntro();
		}
	}

	@Override
	public void resize(){
		drawLine = new Array[(int)(6 + Core.camera.viewportHeight / tileSize)];
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i] = new Array<>();
		}

		rays.resizeFBO(Gdx.graphics.getWidth()/Core.cameraScale, Gdx.graphics.getHeight()/Core.cameraScale);
		//rays.pixelate();
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
