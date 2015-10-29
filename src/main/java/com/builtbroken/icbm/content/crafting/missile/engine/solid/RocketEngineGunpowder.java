package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/28/2015.
 */
public class RocketEngineGunpowder extends RocketEngineSolid implements IPostInit
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

    @Override
    public void initFuel()
    {
        getInventory().setInventorySlotContents(0, new ItemStack(Items.gunpowder, 64));
    }

    @Override
    public void onPostInit()
    {
        if (OreDictionary.getOreID(new ItemStack(Items.gunpowder)) == -1)
        {
            OreDictionary.registerOre("gunpowder", Items.gunpowder);
        }

        //Empty gunpowder engine
        ItemStack engineStack = Engines.GUNPOWDER_ENGINE.newModuleStack();
        RecipeSorter.register(ICBM.PREFIX + "RocketGunpowder", RocketEngineGunpowderRecipe.class, SHAPELESS, "after:minecraft:shaped");
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder", "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder", "gunpowder", "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder"));
        GameRegistry.addRecipe(new RocketEngineGunpowderRecipe(engineStack, engineStack, "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder", "gunpowder"));

        RocketEngineGunpowder engine = (RocketEngineGunpowder) Engines.GUNPOWDER_ENGINE.newModule();
        engine.getInventory().setInventorySlotContents(0, new ItemStack(Items.gunpowder));
        engineStack = engine.toStack().copy();
        GameRegistry.addRecipe(new ShapedOreRecipe(engineStack, "PGP", "PGP", "PRP", 'R', Items.redstone, 'G', "gunpowder", 'P', Items.paper));
    }
}
