package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.api.modules.IMissileModule;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class RocketEngineGunpowder extends RocketEngineSolid
{
    public static float VALUE_OF_GUNPOWDER = 8f;

    public RocketEngineGunpowder(ItemStack item)
    {
        super(item, "engine.gunpowder");
    }

    @Override
    public float getSpeed(IMissileModule missile)
    {
        return 5f;
    }

    @Override
    public float getMaxDistance(IMissileModule missile)
    {
        ItemStack stack = getInventory().getStackInSlot(0);
        if (stack != null)
        {
            if (stack.getItem() == Items.gunpowder)
            {
                return stack.stackSize * VALUE_OF_GUNPOWDER;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if (id.equalsIgnoreCase("gunpowder"))
                {
                    return stack.stackSize * VALUE_OF_GUNPOWDER;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            if (stack.getItem() == Items.gunpowder)
            {
                return true;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if (id.equalsIgnoreCase("gunpowder"))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
