package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.api.IMissile;
import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.crafting.missile.MissileModule;
import net.minecraft.item.ItemStack;

/**
 * Prefab for create new engine types
 * Created by robert on 12/28/2014.
 */
public abstract class RocketEngine extends MissileModule implements IRocketEngine
{
    public RocketEngine(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    public float getSpeed(IMissileModule missile)
    {
        return 0.5f;
    }

    @Override
    public float getMaxDistance(IMissileModule missile)
    {
        return 1000f;
    }


    @Override
    public void onLaunch(IMissile missile, IMissileModule missileModule)
    {

    }

    @Override
    public void onDestroyed(IMissile missile, IMissileModule missileModule)
    {

    }

    /**
     * Loads max fuel, used for creative tab or spawned in items
     */
    public void initFuel()
    {

    }

}
