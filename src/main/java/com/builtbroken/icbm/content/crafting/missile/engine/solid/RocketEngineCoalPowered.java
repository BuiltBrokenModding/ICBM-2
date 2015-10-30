package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

/**
 * Engine that runs off of burnable fuel items. Very ineffective but works at short range.
 * Created by robert on 12/28/2014.
 */
public class RocketEngineCoalPowered extends RocketEngineSolid implements IPostInit
{
    public static float VALUE_OF_COAL = 15f;

    public RocketEngineCoalPowered(ItemStack item)
    {
        super(item, "engine.coal");
    }

    @Override
    public float getMaxDistance(IMissileModule missile)
    {
        return getCoalItemCount() * VALUE_OF_COAL;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            if (stack.getItem() == Items.coal || stack.getItem() == Item.getItemFromBlock(Blocks.coal_block))
            {
                return true;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if (id.equalsIgnoreCase("blockCoal") || id.equalsIgnoreCase("coal") || id.equalsIgnoreCase("charcoal"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private int getCoalItemCount()
    {
        ItemStack stack = getInventory().getStackInSlot(0);
        if (stack != null)
        {
            if (stack.getItem() == Items.coal)
            {
                return stack.stackSize;
            }
            else if (stack.getItem() == Item.getItemFromBlock(Blocks.coal_block))
            {
                return stack.stackSize * 10;
            }
            int[] ids = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < ids.length; i++)
            {
                String id = OreDictionary.getOreName(i);
                if (id.equalsIgnoreCase("blockCoal"))
                {
                    return stack.stackSize * 10;
                }
                else if (id.equalsIgnoreCase("coal") || id.equalsIgnoreCase("charcoal"))
                {
                    return stack.stackSize;
                }
            }
        }
        return 0;
    }

    @Override
    public void initFuel()
    {
        getInventory().setInventorySlotContents(0, new ItemStack(Items.coal, 64));
    }

    @Override
    public void onPostInit()
    {
        if (Engine.itemSheetMetal != null)
        {
            RocketEngineCoalPowered engine = (RocketEngineCoalPowered) Engines.COAL_ENGINE.newModule();
            //Empty coal engine
            ItemStack engineStack = engine.toStack();
            GameRegistry.addRecipe(new ShapedOreRecipe(engineStack, " F ", "LRC", 'R', Items.redstone, 'F', Blocks.furnace, 'L', Items.flint_and_steel, 'C', ItemSheetMetal.SheetMetal.CONE_SMALL.stack()));

            //Coal fuel
            engineStack = Engines.COAL_ENGINE.newModuleStack();
            RecipeSorter.register(ICBM.PREFIX + "RocketCoalPowered", RocketEngineCoalPowered.class, SHAPELESS, "after:minecraft:shaped");

            for (Object s : new Object[]{"blockCoal", new ItemStack(Items.coal, 1, 1), new ItemStack(Items.coal, 1, 0)})
            {
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s, s, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s, s, s, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s, s, s, s, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s, s, s, s, s, s));
                GameRegistry.addRecipe(new RocketEngineCoalRecipe(engineStack, engineStack, s, s, s, s, s, s, s, s));
            }

        }
        else
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(Engines.COAL_ENGINE.newModuleStack(), "c", "f", "h", 'c', UniversalRecipe.CIRCUIT_T1.get(), 'f', Blocks.furnace, 'h', Blocks.hopper));
        }
    }
}
