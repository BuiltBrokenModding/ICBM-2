package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.crafting.missile.MissileModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Prefab for create new engine types
 * Created by robert on 12/28/2014.
 */
public abstract class RocketEngine extends MissileModule implements IRocketEngine
{
    public Color engineSmokeColor;
    public Color engineFireColor;

    protected boolean isRunning = false;
    protected boolean engineOn = false;

    public RocketEngine(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    protected final String getSaveID()
    {
        return MissileModuleBuilder.INSTANCE.getID(this);
    }

    /**
     * Called each tick the missile exists
     *
     * @param missile - missile entity
     */
    public void update(EntityMissile missile)
    {
        engineOn = hasFuel();
    }

    /**
     * Do we still have fuel to run the engine
     *
     * @return true if yes
     */
    protected boolean hasFuel()
    {
        return true;
    }

    @Override
    public float getSpeed(IMissile missile)
    {
        return 0.5f;
    }

    @Override
    public float getMaxDistance(IMissile missile)
    {
        return 1000f;
    }


    @Override
    public void onLaunch(IMissileEntity missile, IMissile missileModule)
    {
        startEngine(missileModule);
    }

    /**
     * Called to start the engine
     *
     * @param missileModule
     */
    public void startEngine(IMissile missileModule)
    {
        isRunning = true;
    }

    @Override
    public boolean onDestroyed(IMissileEntity missile, IMissile missileModule)
    {
        return false;
    }

    @Override
    public boolean generatesFire(IMissileEntity missile, IMissile missileModule)
    {
        return true;
    }

    /**
     * Loads max fuel, used for creative tab or spawned in items
     */
    public void initFuel()
    {

    }

    @Override
    public int getMissileSize()
    {
        return -1;
    }

    public void doEngineEffects(IMissile missile, Entity entity)
    {
        Pos motion = new Pos(entity.motionX, entity.motionY, entity.motionZ).normalize();
        Pos vel = new Pos((entity.worldObj.rand.nextFloat() - 0.5f) / 8f, (entity.worldObj.rand.nextFloat() - 0.5f) / 8f, (entity.worldObj.rand.nextFloat() - 0.5f) / 8f);
        vel = vel.multiply(motion);

        doEngineFlames(missile, vel, motion);
        doEngineSmoke(missile, vel, motion);
    }

    public void doEngineEffects(IMissile missile, World world, float yaw, float pitch, Pos missileCenter)
    {
        Pos angle = new EulerAngle(yaw, pitch).toPos();
        Pos vel = angle.multiply(0.1 + world.rand.nextFloat() * 0.2);

        doEngineFlames(missile, missileCenter.add(angle), vel);
        doEngineSmoke(missile, missileCenter.add(angle), vel);
    }

    @Override
    public Color getEngineFireColor(IMissileEntity missile, IMissile missileModule)
    {
        return engineFireColor;
    }

    @Override
    public Color getEngineSmokeColor(IMissileEntity missile, IMissile missileModule)
    {
        return engineSmokeColor;
    }

    /**
     * Called to render engine flames, keep in mind this is called server side
     *
     * @param missile - missile object
     * @param pos     - location to render, should be engine port
     * @param -       vel of the missile, or expect vel to render
     */
    public void doEngineFlames(IMissile missile, Pos pos, Pos vel)
    {
        //TODO spawn particles
    }

    /**
     * Called to render engine smoke, keep in mind this is called server side
     *
     * @param missile - missile object
     * @param pos     - location to render, should be engine port
     * @param -       vel of the missile, or expect vel to render
     */
    public void doEngineSmoke(IMissile missile, Pos pos, Pos vel)
    {
        //TODO spawn particles
    }


    @Override
    public String toString()
    {
        //TODO add hashcode
        return getClass().getSimpleName();
    }
}
