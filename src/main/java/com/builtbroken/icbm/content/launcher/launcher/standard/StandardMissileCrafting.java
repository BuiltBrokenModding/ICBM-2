package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.api.ISave;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Recipe to craft a standard missile in silo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/13/2016.
 */
public class StandardMissileCrafting implements ISave
{
    //TODO add registry for recipe to allow other mods to add missiles to silos

    /** Number of rods to complete the frame */
    private static final int MAX_ROD_COUNT = 128;
    private static final int ROD_PER_LEVEL_COUNT = 16;
    private static final int MAX_ROD_LEVEL_COUNT = 8;

    private static final int MAX_PLATE_COUNT = 36;
    private static final int PLATE_PER_LEVEL_COUNT = 4;
    private static final int MAX_PLATE_LEVEL_COUNT = 9;

    /** Count of rods added to recipe */
    protected int rodsContained = 0;
    /** Level of frame completion, 8 total */
    protected int frameLevel = 0;
    /** Level of plate completion, 9 total */
    protected int plateLevel = 0;
    /** Count of plates added to recipe */
    protected int platesContained = 0;

    /** Is the frame completed */
    protected boolean frameCompleted = false;
    /** Are the guts of the missile added */
    protected boolean gutsCompleted = false;
    /** Is the skin completed */
    protected boolean skinCompleted = false;


    /**
     * Checks if the item is part of the recipe,
     * and is still required by the recipe.
     *
     * @param stack - stack being added,
     *             can be null but will return false
     * @return true if the item can be added.
     */
    public boolean canAddItem(ItemStack stack)
    {
        if (stack != null)
        {
            if (isRod(stack) && !frameCompleted)
            {
                return true;
            }
            else if (isPlate(stack) && !skinCompleted)
            {
                return true;
            }
            else
            {
                Item item = stack.getItem();
                if(item instanceof IRocketEngine)
                {

                }
                else if(item instanceof IWarhead)
                {

                }
                else if(item instanceof IGuidance)
                {

                }
            }
        }
        return false;
    }

    private boolean isRod(ItemStack stack)
    {
        return hasOreName("rodIron", stack);
    }

    private boolean isPlate(ItemStack stack)
    {
        return hasOreName("rodIron", stack);
    }

    private boolean hasOreName(String name, ItemStack stack)
    {
        for (int id : OreDictionary.getOreIDs(stack))
        {
            String oreName = OreDictionary.getOreName(id);
            if (name != null && !name.isEmpty() && name.toLowerCase().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Assumes canAddItem was already called, but
     * still checks for sanity at some level. Will
     * always consume item as it only checks type.
     *
     * @param item
     * @return
     */
    public boolean addItem(ItemStack item)
    {
        return false;
    }

    protected int addRods(int count)
    {
        if (!skinCompleted)
        {
            int addition = Math.min(count, Math.max(MAX_ROD_COUNT - rodsContained, 0));
            if (addition > 0)
            {
                rodsContained += addition;
                frameLevel = rodsContained % ROD_PER_LEVEL_COUNT;
                if (frameLevel >= MAX_ROD_LEVEL_COUNT)
                {
                    frameCompleted = true;
                }
            }
            return addition;
        }
        return 0;
    }

    protected int addPlates(int count)
    {
        if (!skinCompleted)
        {
            int addition = Math.min(count, Math.max(MAX_PLATE_COUNT - platesContained, 0));
            if (addition > 0)
            {
                platesContained += addition;
                plateLevel = platesContained % PLATE_PER_LEVEL_COUNT;
                if (plateLevel >= MAX_PLATE_LEVEL_COUNT)
                {
                    skinCompleted = true;
                }
            }
            return addition;
        }
        return 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        return null;
    }
}
