package com.baru.survivor.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.baru.survivor.Survivor;
import com.baru.survivor.backend.State;

public class MenuUI {

	private Skin skin;
	private Stage stage = new Stage();;
	private Table table;
	
	private Label seedName, tribesNum, villagersPerTribe, foodNum, foodDur, lakesNum, lakesDur;
	private TextField seedNameField, tribesNumField, villagersPerTribeField, foodNumField, foodDurField, lakesNumField, lakesDurField;
	private List files;
	private TextButton loadFile, generateFile;
	
	
	public void create() {    
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		table = new Table();
		stage.addActor(table);
		
	    seedName = new Label("Seed name", skin);
	    tribesNum = new Label("Tribes quantity", skin);
	    villagersPerTribe = new Label("Villagers per tribe", skin);
	    foodNum = new Label("Food repositories quantity", skin);
	    foodDur = new Label("Food repositories uses", skin);
	    lakesNum = new Label("Water repositories quantity", skin);
	    lakesDur = new Label("Water repositories uses", skin);
	    
	    seedNameField = new TextField("Map", skin);
	    tribesNumField = new TextField("2", skin);
	    villagersPerTribeField = new TextField("2", skin);
	    foodNumField = new TextField("5", skin);
	    foodDurField = new TextField("10", skin);
	    lakesNumField = new TextField("5", skin);
	    lakesDurField = new TextField("10", skin);
	    
	    files = new List(skin);
	    
	    loadFile = new TextButton("Load selected seed", skin);
	    generateFile = new TextButton("Create seed", skin);
	    
	    generateFile.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				int tribesNumInt = Integer.valueOf(tribesNumField.getText());
			    int villagersPerTribeInt = Integer.valueOf(villagersPerTribeField.getText());
			    int foodNumInt = Integer.valueOf(foodNumField.getText());
			    int foodDurInt = Integer.valueOf(foodDurField.getText());
			    int lakesNumInt = Integer.valueOf(lakesNumField.getText());
			    int lakesDurInt = Integer.valueOf(lakesDurField.getText());
				State generatedSeed = new State(tribesNumInt, villagersPerTribeInt, foodNumInt, foodDurInt, lakesNumInt, lakesDurInt);
				try{
					FileOutputStream fileOut = new FileOutputStream("./maps/"+seedNameField.getText());
			        ObjectOutputStream out = new ObjectOutputStream(fileOut);
			        out.writeObject(generatedSeed);
			        out.close();
			        fileOut.close();
			    }catch(IOException i){
			          i.printStackTrace();
			    }
			}
		});
	    
	    loadFile.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				try{
				   FileInputStream fileIn = new FileInputStream("./maps/"+files.getSelected());
				   ObjectInputStream in = new ObjectInputStream(fileIn);
			       State loadedState = (State) in.readObject();
			       in.close();
			       fileIn.close();
			       Survivor.setState(loadedState);
			    }catch(IOException i){
			       i.printStackTrace();
			       return;
			    }catch(ClassNotFoundException c){
			       c.printStackTrace();
			       return;
			    }
			}
		});
	    
	    table.add(files).colspan(2).height(200).fillX().pad(10);
	    table.row();
	    table.add(loadFile).colspan(2).pad(10).fillX();
	    table.row();
	    table.add(seedName).right().pad(10);
	    table.add(seedNameField).width(200).pad(10);
	    table.row();
	    table.add(tribesNum).right().pad(10);
	    table.add(tribesNumField).width(200).pad(10);
	    table.row();
	    table.add(villagersPerTribe).right().pad(10);
	    table.add(villagersPerTribeField).width(200).pad(10);
	    table.row();
	    table.add(foodNum).right().pad(10);
	    table.add(foodNumField).width(200).pad(10);
	    table.row();
	    table.add(foodDur).right().pad(10);
	    table.add(foodDurField).width(200).pad(10);
	    table.row();
	    table.add(lakesNum).right().pad(10);
	    table.add(lakesNumField).width(200).pad(10);
	    table.row();
	    table.add(lakesDur).right().pad(10);
	    table.add(lakesDurField).width(200).pad(10);
	    table.row();
	    table.add(generateFile).colspan(2).fillX().pad(10);
	    
	    //table.debug();
	    table.setFillParent(true);
	    Gdx.input.setInputProcessor(stage);
	}
	
	public void render() {
		File folder = new File("./maps/");
	    String[] listOfFiles = folder.list();
	    files.setItems(listOfFiles);
	    
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
	}
}