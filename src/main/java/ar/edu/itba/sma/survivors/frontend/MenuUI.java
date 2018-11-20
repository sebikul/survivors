package ar.edu.itba.sma.survivors.frontend;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.gson.Gson;

import java.io.*;

public class MenuUI {

    private Stage stage = new Stage();

    private TextField seedNameField, tribesNumField, villagersPerTribeField, foodNumField, foodDurField, lakesNumField, lakesDurField;
    private List<Object> files;

    private File folder;
    private File logs;
    private Plotter plotter = new Plotter();

    public void create() {
        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        Table table = new Table();
        Table container = new Table();
        ScrollPane scrollPane = new ScrollPane(container, skin);
        scrollPane.setFadeScrollBars(true);
        stage.addActor(table);

        folder = new File("./maps/");
        if (!folder.exists() && !folder.mkdir()) {
            System.out.println("Could not create maps folder!");
        }
        logs = new File("./logs/");
        if (!logs.exists() && !logs.mkdir()) {
            System.out.println("Could not create logs folder!");
        }

        Label seedName = new Label("Seed name", skin);
        Label tribesNum = new Label("Tribes quantity", skin);
        Label villagersPerTribe = new Label("Villagers per tribe", skin);
        Label foodNum = new Label("Food repositories quantity", skin);
        Label foodDur = new Label("Food repositories uses", skin);
        Label lakesNum = new Label("Water repositories quantity", skin);
        Label lakesDur = new Label("Water repositories uses", skin);

        seedNameField = new TextField("Map", skin);
        tribesNumField = new TextField("1", skin);
        villagersPerTribeField = new TextField("4", skin);
        foodNumField = new TextField("10", skin);
        foodDurField = new TextField("50", skin);
        lakesNumField = new TextField("10", skin);
        lakesDurField = new TextField("50", skin);

        files = new List<>(skin);

        TextButton loadFile = new TextButton("Load selected seed", skin);
        TextButton generateFile = new TextButton("Create seed", skin);

        generateFile.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                int tribesNumInt = Integer.valueOf(tribesNumField.getText());
                int villagersPerTribeInt = Integer.valueOf(villagersPerTribeField.getText());
                int foodNumInt = Integer.valueOf(foodNumField.getText());
                int foodDurInt = Integer.valueOf(foodDurField.getText());
                int lakesNumInt = Integer.valueOf(lakesNumField.getText());
                int lakesDurInt = Integer.valueOf(lakesDurField.getText());
                State generatedSeed = new State(tribesNumInt, villagersPerTribeInt, foodNumInt, foodDurInt, lakesNumInt, lakesDurInt);
                //plotter.run(tribesNumInt, villagersPerTribeInt, foodNumInt*foodDurInt, lakesNumInt*lakesDurInt);
                try {
                    FileOutputStream fileOut = new FileOutputStream("./maps/" + seedNameField.getText());
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    // Gson gson = new Gson();
                    // String json = gson.toJson(generatedSeed);
                    out.writeObject(generatedSeed);
                    out.close();
                    fileOut.close();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        });

        loadFile.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    FileInputStream fileIn = new FileInputStream("./maps/" + files.getSelected());
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    // Gson gson = new Gson();
                    // String json = gson.fromJson(in.)
                    State loadedState = (State) in.readObject();
                    plotter.run(loadedState.getResourceManager().getTotals(), loadedState.getTribeManager().getVillages());
                    in.close();
                    fileIn.close();
                    loadedState.resetPheromones();
                    loadedState.resetLog();
                    Survivor.setState(loadedState);
                } catch (IOException | ClassNotFoundException i) {
                    i.printStackTrace();
                }
            }
        });

        container.add(files).width(420);
        table.add(scrollPane).colspan(2).pad(10).height(200).fillX();
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

        String[] listOfFiles = folder.list((file, s) -> !s.equals(".gitignore"));
        assert listOfFiles != null;
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
