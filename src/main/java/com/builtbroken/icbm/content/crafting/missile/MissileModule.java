package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.missile.EntityMissile;
import net.minecraft.item.ItemStack;

/**
 * Prefab for any module that can fit on a missile
 * Created by robert on 12/28/2014.
 */
public class MissileModule extends AbstractModule implements IMissileModule
{
    /**
     * Default constructor
     *
     * @param item - item the module is create out of
     * @param name - name of the module
     */
    public MissileModule(ItemStack item, String name)
    {
        super(item, name);
    }

    @Override
    public void update(EntityMissile missile)
    {

    }

    /**
     * Gets the mass of the module in kg
     *
     * @return mass
     */
    public float getMass()
    {
        return 1;
    }

    @Override
    public int getMissileSize()
    {
        return -1;
    }
}
