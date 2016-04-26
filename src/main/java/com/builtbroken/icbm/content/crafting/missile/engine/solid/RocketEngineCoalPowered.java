package com.builtbroken.icbm.content.crafting.missile.engine.solid;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.engine.Engines;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

/**
 * Engine that runs off of burnable fuel items. Very ineffective but works at short range.
 * Created by robert on 12/28/2014.
 */
public class RocketEngineCoalPowered extends RocketEngineSolid implements IPostInit
{
    /** Value of coal based items */
    public static float VALUE_OF_COAL = 15f;
    /** Value of coal based items */
    public static float SPEED_OF_COAL = 0.5f;
    /** Map of fuels to distance value */
    public static final HashMap<ItemStackWrapper, Float> FUEL_DISTANCE_VALUE = new HashMap();
    /** Map of fuels to speed value */
    public static final HashMap<ItemStackWrapper, Float> FUEL_SPEED_VALUE = new HashMap();

    public RocketEngineCoalPowered(ItemStack item)
    {
        super(item, "engine.coal");
        this.engineSmokeColor = new Color(54, 56, 58);
    }

    @Override
    public float getMaxDistance(IMissile missile)
    {
        if (fuelStack() != null)
        {
            ItemStackWrapper wrapper = new ItemStackWrapper(fuelStack());
            if (FUEL_DISTANCE_VALUE.containsKey(wrapper))
            {
                return FUEL_DISTANCE_VALUE.get(wrapper) * fuelStack().stackSize;
            }
        }
        return 0;
    }

    @Override
    public float getSpeed(IMissile missile)
    {
        if (fuelStack() != null)
        {
            ItemStackWrapper wrapper = new ItemStackWrapper(fuelStack());
            if (FUEL_SPEED_VALUE.containsKey(wrapper))
            {
                return FUEL_SPEED_VALUE.get(wrapper);
            }
            return SPEED_OF_COAL;
        }
        return 0f;
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

    @Override
    public void initFuel()
    {
        getInventory().setInventorySlotContents(0, new ItemStack(Items.coal, 64));
    }

    @Override
    public void onPostInit()
    {
        //Empty coal engine
        RocketEngineCoalPowered engine = (RocketEngineCoalPowered) Engines.COAL_ENGINE.newModule();
        ItemStack engineStack = engine.toStack();

        if (Engine.itemSheetMetal != null)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(engineStack, " F ", "LRC", 'R', OreNames.REDSTONE, 'F', Blocks.furnace, 'L', OreNames.FLINT, 'C', ItemSheetMetal.SheetMetal.CONE_SMALL.stack()));
        }
        else
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(engineStack, "c", "f", "h", 'c', UniversalRecipe.CIRCUIT_T1.get(), 'f', Blocks.furnace, 'h', Blocks.hopper));
        }

        //Coal fuel
        engineStack = Engines.COAL_ENGINE.newModuleStack();
        RecipeSorter.register(ICBM.PREFIX + "RocketCoalPowered", RocketEngineCoalRecipe.class, SHAPELESS, "after:minecraft:shaped");

        loadFuelValues();

        //TODO maybe find a better way to handle this
        for (ItemStackWrapper wrapper : FUEL_DISTANCE_VALUE.keySet())
        {
            ItemStack s = wrapper.itemStack;
            if (s != null && s.stackSize > 0)
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


    }

    public static final void loadFuelValues()
    {
        //clear in case we are reloading values after normal MC loading phase
        FUEL_DISTANCE_VALUE.clear();
        FUEL_SPEED_VALUE.clear();

        //Add fuel types to map
        FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(new ItemStack(Items.coal)), VALUE_OF_COAL);
        FUEL_SPEED_VALUE.put(new ItemStackWrapper(new ItemStack(Items.coal)), SPEED_OF_COAL);

        List<ItemStack> oreStacks = OreDictionary.getOres("dustCoal");
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), VALUE_OF_COAL);
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 1.05f); // 5% gain
        }

        oreStacks = OreDictionary.getOres(OreNames.LEAVES);
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), 1f);
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 0.65f); // 35% loss
        }

        oreStacks = OreDictionary.getOres(OreNames.SAPLING);
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), 1f);
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 0.65f); // 35% loss
        }

        oreStacks = OreDictionary.getOres(OreNames.LOG);
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), VALUE_OF_COAL * 0.85f); // 15% loss
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 0.65f); // 35% loss
        }

        oreStacks = OreDictionary.getOres(OreNames.WOOD);
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), VALUE_OF_COAL * 0.20f); // 80% loss
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 0.65f); // 35% loss
        }

        oreStacks = OreDictionary.getOres(OreNames.WOOD_STICK);
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), VALUE_OF_COAL * 0.05f); // 95% loss
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 0.65f); // 35% loss
        }

        oreStacks = OreDictionary.getOres("fuelCoke");
        for (ItemStack stack : oreStacks)
        {
            FUEL_DISTANCE_VALUE.put(new ItemStackWrapper(stack), VALUE_OF_COAL * 2f); // 100% gain
            FUEL_SPEED_VALUE.put(new ItemStackWrapper(stack), SPEED_OF_COAL * 1.10f); // 10% gain
        }
    }

    @Override
    public String toString()
    {
        //TODO add hashcode
        return "CoalEngine[" + fuelStack() + "]";
    }
}
