package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.crafting.missile.MissileModule;
import net.minecraft.item.ItemStack;

/**
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
        return 100f;
    }
}
