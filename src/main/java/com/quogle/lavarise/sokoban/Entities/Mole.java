package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.client.sokoban.Animations.StateMachine;
import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Entities.enums.MoleState;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import net.minecraft.resources.ResourceLocation;

public class Mole extends Entity {

    Tile previousTile;

    public Mole(int x, int y, AnimationManager animManager, EntityType entityType, Level level) {
        super(x, y, entityType, animManager);
        this.stateMachine = new StateMachine(MoleState.IDLE);

        Tile startTile = level.getTile(getX(), getY());
        this.previousTile = startTile;
        startTile.setMole(this);

        initAnimations();
    }

    private void initAnimations() {
        getAnimationManager().add("idle", AnimationAssets.MOLE_IDLE, 40, true);
        getAnimationManager().add("block", AnimationAssets.MOLE_BLOCK, AnimationAssets.BUMP_DURATIONS, false);
    }

    @Override
    public EntityType getType() {
        return EntityType.MOLE;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isStackable() {
        return true; // other entities can share tiles with moles
    }

    @Override
    public int getRenderLayer() {
        return 0;   // draw underneath everything else
    }

    public void onBlock() {
        System.out.println("MOLE BLOCK TRIGGERED at tile: " + getX() + "," + getY());
        stateMachine.setState(MoleState.BLOCK);
        var anim = getAnimationManager().get(stateMachine.getState().getAnimKey());
        if (anim != null) anim.reset();
    }

    @Override
    public void update(Level level) {
        AnimationManager anims = getAnimationManager();
        MoleState current = (MoleState) stateMachine.getState(); // cast to MoleState

        if (current.getNextState() != null) {
            Animation anim = anims.get(current.getAnimKey());
            if (anim != null && anim.isFinished()) {
                stateMachine.setState((MoleState) current.getNextState()); // cast here too
                anims.get(((MoleState) current.getNextState()).getAnimKey()).reset();
            }
        }
    }

    public void activateSolid() {
        setSolidOverride(true);
    }
    public void removeSolid() {
        setSolidOverride(false);
    }

    public Tile getPreviousTile() { return previousTile; }
    public void setPreviousTile(Tile newTile) { this.previousTile = newTile; }
}
