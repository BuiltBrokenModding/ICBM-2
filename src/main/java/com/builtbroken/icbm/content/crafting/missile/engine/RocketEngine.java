package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.crafting.missile.MissileModule;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * Prefab for create new engine types
 * Created by robert on 12/28/2014.
 */
public abstract class RocketEngine extends MissileModule implements IRocketEngine
{
    public Color engineSmokeColor;
    public Color engineFireColor;

    public RocketEngine(ItemStack item, String name)
    {
        super(item, name);
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

    @Override
    public String toString()
    {
        //TODO add hashcode
        return getClass().getSimpleName();
    }
}
