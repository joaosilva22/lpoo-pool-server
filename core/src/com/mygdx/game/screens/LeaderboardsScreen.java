package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.PoolGameServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by joaopsilva on 06-06-2016.
 */
public class LeaderboardsScreen implements Screen {
    final PoolGameServer game;

    private Stage stage;
    private Table table;

    public LeaderboardsScreen(final PoolGameServer game) {
        this.game = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Carregar a lista de tempos
        ArrayList<LeaderboardEntry> entries = new ArrayList<LeaderboardEntry>();
        FileHandle fileHandle = Gdx.files.internal("data/leaderboards.txt");
        String leaderboard = fileHandle.readString();
        String lines[] = leaderboard.split("\\r?\\n");
        for (String line : lines) {
            String entry[] = line.split(" - ");
            entries.add(new LeaderboardEntry(entry[0], entry[1]));
        }

        // Ordenar a lista de tempos
        Collections.sort(entries, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
                String times1[] = o1.getTime().split(":");
                int minutes1 = Integer.parseInt(times1[0]);
                int seconds1 = Integer.parseInt(times1[1]);

                String times2[] = o2.getTime().split(":");
                int minutes2 = Integer.parseInt(times2[0]);
                int seconds2 = Integer.parseInt(times2[1]);

                int dt = (minutes1 * 60 + seconds1) - (minutes2 * 60 + seconds2);
                return dt;
            }
        });

        // Tamanhos das fonts ------------------------------
        int fontTitleSize = (int) (Gdx.graphics.getHeight() * 0.06);
        BitmapFont fontTitle = game.generateFont(fontTitleSize);
        int fontMainSize = (int) (Gdx.graphics.getHeight() * 0.03);
        BitmapFont fontMain = game.generateFont(fontMainSize);

        // Styles dos actors ------------------------------
        Label.LabelStyle lblMainStyle = new Label.LabelStyle();
        lblMainStyle.font = fontMain;

        Label.LabelStyle lblTitleStyle = new Label.LabelStyle();
        lblTitleStyle.font = fontTitle;

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("text-button-up.png"))));
        btnStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("text-button-down.png"))));
        btnStyle.checked = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("text-button-down.png"))));
        btnStyle.font = fontMain;

        // Criacao dos actors ------------------------------
        Label lblTitle = new Label("Leaderboards", lblTitleStyle);
        Label[] lblNameEntry = new Label[10];

        for (int i = 0; i < 10; i++) {
            String name = "-------";
            if (i < entries.size()) {
                name = entries.get(i).toString();
            }
            lblNameEntry[i] = new Label(name, lblMainStyle);
        }

        TextButton btnBack = new TextButton("Back to Menu", btnStyle);
        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Layout dos actors ------------------------------
        int spacing = (int) (Gdx.graphics.getHeight() * 0.02);
        int width = (int) (Gdx.graphics.getWidth() * 0.2), height = (int) (Gdx.graphics.getWidth() * 0.04);
        int titleSpacing = (int) (Gdx.graphics.getHeight() * 0.05);

        table.top();
        table.add(lblTitle).colspan(1).padTop(titleSpacing).padBottom(titleSpacing / 2).space(spacing).padBottom(0);

        table.columnDefaults(0).pad(spacing).center();

        for (int i = 0; i < 10; i++) {
            table.row();
            table.add(lblNameEntry[i]);
        }

        table.row();
        table.add(btnBack).colspan(1).width(width);
        //table.setDebug(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private class LeaderboardEntry {
        private String name;
        private String time;

        public LeaderboardEntry(String name, String time) {
            this.name = name;
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public String getTime() {
            return time;
        }

        public String toString() {
            return name + " - " + time;
        }
    }
}
