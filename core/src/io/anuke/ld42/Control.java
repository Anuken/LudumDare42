package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import io.anuke.ld42.GameState.State;
import io.anuke.ld42.entities.Enemy;
import io.anuke.ld42.entities.Player;
import io.anuke.ld42.entities.ShadowTrait;
import io.anuke.ld42.io.MapLoader;
import io.anuke.ucore.core.*;
import io.anuke.ucore.core.Inputs.Axis;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.EntityPhysics;
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
import io.anuke.ucore.util.Tmp;

import static io.anuke.ld42.Vars.*;

public class Control extends RendererModule{
	private Array<Entity>[] drawLine = new Array[0];
	private Surface effects;

	public TiledMap map;
	public TiledMapTileLayer wallLayer;
	public TiledMapTileLayer floorLayer;
	
	public Control(){
		Core.cameraScale = 3;
	    Core.batch = new SpriteBatch();
		Core.atlas = new Atlas("sprites.atlas");
	
		KeyBinds.defaults(
			"move_x", new Axis(Input.A, Input.D),
			"move_y", new Axis(Input.S, Input.W),
			"shoot", Input.MOUSE_LEFT,
			"pause", Input.ESCAPE
		);
		
		Settings.loadAll("io.anuke.ld42");

		EntityPhysics.initPhysics();
		EntityPhysics.collisions().setCollider(tileSize,
		(x, y) -> wallLayer.getCell(x, y) != null && wallLayer.getCell(x, y).getTile().getProperties().containsKey("solid"),
		(x, y, rect) -> rect.setSize(tileSize).setPosition(x*tileSize - tileSize/2f, y*tileSize));

		effects = Graphics.createSurface();

		pixelate();

		player = new Player();
		player.add();

		Enemy enemy = new Enemy();
		enemy.set(60, 60);
		enemy.add();

		loadMap("map");
	}

	public void reset(){
	    //TODO reset game state
	}

	public void loadMap(String name){
		map = new MapLoader().load("maps/" + name + ".tmx");
		floorLayer = (TiledMapTileLayer) map.getLayers().get("floor");
		wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");

		player.set(floorLayer.getWidth() * tileSize / 2f, floorLayer.getHeight() * tileSize/2f);

		EntityPhysics.initPhysics(0, 0, wallLayer.getWidth() * tileSize, wallLayer.getHeight() * tileSize);
	}
	
	@Override
	public void update(){
		Entities.update();
		
		//TODO remove
		if(Inputs.keyDown(Input.ESCAPE)){
			Gdx.app.exit();
        }
		
		if(GameState.is(State.playing)){
			Timers.update();
			setCamera(player.x, player.y);
			
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
	}
	
	@Override
	public void draw(){

		//insert entities into draw lines
		for(Entity entity : Entities.defaultGroup().all()){
			float y = entity.getY();
			float scl = (y - Core.camera.position.y);
			int position = (int)(scl / tileSize) + drawLine.length/2;
			if(entity instanceof DrawTrait && position > 0 && position < drawLine.length){
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

		//draw shadows
		Graphics.surface(effects);
		for(Entity entity : Entities.defaultGroup().all()){
			if(!(entity instanceof ShadowTrait)) continue;
			ShadowTrait sh = (ShadowTrait)entity;
			Draw.rect("shadow" + sh.shadowSize(), entity.getX(), entity.getY() + 1);
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
				float sx = 1, sy = 1.5f;
				float x1 = cx + t, y1 = cy + t, x2 = cx - t, y2 = cy - t,
				x3 = x2 - mv*sx, y3 = y2 + mv*sy, x4 = cx - t - mv*sx, y4 = cy + t + mv*sy, x5 = x1 - mv*sx, y5 = y1 + mv*sy;
				Fill.quad(x1, y1, x2, y2, x3, y3, x4, y4);
				Fill.tri(x1, y1, x4, y4, x5, y5);
			}
		}

		Draw.color(0, 0, 0, 0.2f);
		Graphics.flushSurface();
		Draw.color();

		//draw walls
		for(int y = drawLine.length/2 - 1; y >= -drawLine.length/2; y --){
			Array<Entity> line = drawLine[y + drawLine.length/2];

			Sort.instance().sort(line, (a, b) -> -Float.compare(a.getX(), b.getY()));
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

		//drawDebug();
	}

	@Override
	public void resize(){
		drawLine = new Array[(int)(4 + Core.camera.viewportHeight / tileSize)];
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i] = new Array<>();
		}
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
