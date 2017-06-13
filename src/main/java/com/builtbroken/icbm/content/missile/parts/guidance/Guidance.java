package com.builtbroken.icbm.content.missile.parts.guidance;

import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.parts.MissileModule;
import net.minecraft.item.ItemStack;

/**
 * Prefab for creating new chips to help the missile get to the target
 * Created by robert on 12/28/2014.
 */
public abstract class Guidance extends MissileModule implements IGuidance
{
    public Guidance(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    public float getFallOffRange(IMissile missile)
    {
        return 10f;
    }

    @Override
    public float getChanceToFail(IMissile missile)
    {
        return 0.5f;
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
