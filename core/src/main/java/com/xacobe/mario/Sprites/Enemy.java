package com.xacobe.mario.Sprites;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.xacobe.mario.Screens.PlayScreen;

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;

    public Enemy(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;

        setPosition(x, y);
        defineEnemy();
    }

    protected abstract void defineEnemy();
    public  abstract  void hitOnSword();
    public abstract void onSwordHit();

}
