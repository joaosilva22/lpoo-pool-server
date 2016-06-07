package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.mygdx.game.screens.MainMenuScreen;

public class PoolGameServer extends Game {
	public static final float PPM = 350;
	public static final float VIEWPORT_WIDTH = 2.493f;
	public static final float VIEWPORT_HEIGHT = 1.650f;

	public SpriteBatch batch;
	public BitmapFont font;
	private FreeTypeFontGenerator generator;

	private Music music;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AlexandriaFLF-Bold.ttf"));
		music = Gdx.audio.newMusic(Gdx.files.internal("audio/music.wav"));
		music.setVolume(0.05f);
		music.setLooping(true);
		music.play();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	public BitmapFont generateFont(int size) {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		return generator.generateFont(parameter);
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		music.dispose();
		generator.dispose();
	}
}
