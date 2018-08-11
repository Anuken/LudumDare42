package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static io.anuke.ld42.Vars.*;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import io.anuke.ld42.GameState.State;
import io.anuke.ld42.entities.Player;
import io.anuke.ld42.io.MapLoader;
import io.anuke.ucore.core.Inputs.Axis;
import io.anuke.ucore.entities.impl.SolidEntity;
import io.anuke.ucore.entities.trait.DrawTrait;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.input.Input;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.KeyBinds;
import io.anuke.ucore.core.Settings;
import io.anuke.ucore.entities.EntityPhysics;
import io.anuke.ucore.entities.EntityDraw;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.util.Atlas;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.Log;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class Control extends RendererModule{
	private Array<Entity>[] drawLine = new Array[0];

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
			"pause", Input.ESCAPE
		);
		
		Settings.loadAll("io.anuke.ld42");

		EntityPhysics.initPhysics();
		EntityPhysics.collisions().setCollider(tileSize, (x, y) -> wallLayer.getCell(x, y) != null && wallLayer.getCell(x, y).getTile().getProperties().containsKey("solid"),
		(x, y, rect) -> {
			rect.setSize(tileSize).setPosition(x*tileSize - tileSize/2f, y*tileSize);
		});

		pixelate();

		player = new Player();
		player.set(50, 50);
		player.add();
		loadMap("map");
	}

	public void reset(){
	    //TODO reset game state
	}

	public void loadMap(String name){
		map = new MapLoader().load("maps/" + name + ".tmx");
		floorLayer = (TiledMapTileLayer) map.getLayers().get("floor");
		wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");

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

		Draw.color(Color.GREEN);
		for(Entity entity : Entities.all()){
			if(!(entity instanceof SolidTrait)) continue;
			SolidTrait solid = (SolidTrait)entity;
			solid.getHitboxTile(Tmp.r1);
			Lines.rect(Tmp.r1);
		}
		Draw.color();
	}

	@Override
	public void resize(){
		drawLine = new Array[(int)(4 + Core.camera.viewportHeight / tileSize)];
		for(int i = 0; i < drawLine.length; i++){
			drawLine[i] = new Array<>();
		}
	}
}
