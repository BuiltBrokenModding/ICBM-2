package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IMissileModule;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.core.network.IByteBufReader;
import com.builtbroken.mc.core.network.IByteBufWriter;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Recipe to craft a standard missile in silo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/13/2016.
 */
public class StandardMissileCrafting implements ISave, IByteBufWriter, IByteBufReader
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
    /** Count of plates added to recipe */
    protected int platesContained = 0;

    /** Level of frame completion, 8 total */
    protected int frameLevel = 0;
    /** Level of plate completion, 9 total */
    protected int plateLevel = 0;


    /** Is the frame completed */
    protected boolean frameCompleted = false;
    /** Are the guts of the missile added */
    protected boolean gutsCompleted = false;
    /** Is the skin completed */
    protected boolean skinCompleted = false;

    /** Engine of the missile */
    protected ItemStack rocketEngine;
    /** Flight computer or controller */
    protected ItemStack rocketComputer;
    /** Explosive part of the missile */
    protected ItemStack warhead;


    /**
     * Checks if the item is part of the recipe,
     * and is still required by the recipe.
     *
     * @param stack - stack being added,
     *              can be null but will return false
     * @return true if the item can be added.
     */
    public boolean canAddItem(final ItemStack stack)
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
                if (item instanceof IRocketEngine)
                {
                    return ((IMissileModule) item).getMissileSize() == MissileCasings.STANDARD.ordinal();
                }
                else if (item instanceof IWarhead)
                {
                    return true;
                }
                else if (item instanceof IGuidance)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Grabs the output message to display to the user about
     * what item to add to the crafting recipe.
     *
     * @return chat component, not null, should be translated.
     */
    public IChatComponent getCurrentRecipeChat()
    {
        //TODO replace with translation keys
        if (!frameCompleted)
        {
            return new ChatComponentText("Needs " + (MAX_ROD_COUNT - rodsContained) + " more rods.");
        }
        else if (!gutsCompleted)
        {
            if (rocketComputer == null)
            {
                return new ChatComponentText("Needs a guidance computer.");
            }
            else if (rocketEngine == null)
            {
                return new ChatComponentText("Needs a rocket engine.");
            }
        }
        else if (!skinCompleted)
        {
            return new ChatComponentText("Needs " + (MAX_PLATE_COUNT - platesContained) + " more metal sheets.");
        }
        return new ChatComponentText("Done click with wrench to finish");
    }

    private boolean isRod(final ItemStack stack)
    {
        return hasOreName("rodIron", stack);
    }

    private boolean isPlate(final ItemStack stack)
    {
        return hasOreName("rodIron", stack);
    }

    private boolean hasOreName(final String name, final ItemStack stack)
    {
        for (int id : OreDictionary.getOreIDs(stack))
        {
            String oreName = OreDictionary.getOreName(id);
            if (oreName != null && !oreName.isEmpty() && oreName.equalsIgnoreCase(name))
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
     * @param stack - item being added
     * @return true if the item was accepted
     */
    public boolean addItem(final ItemStack stack)
    {
        if (stack != null)
        {
            if (isRod(stack) && !frameCompleted)
            {
                int added = addRods(stack.stackSize);
                stack.stackSize -= added;
                return added > 0;
            }
            else if (isPlate(stack) && !skinCompleted)
            {
                int added = addPlates(stack.stackSize);
                stack.stackSize -= added;
                return added > 0;
            }
            else
            {
                Item item = stack.getItem();
                boolean added = false;
                if (item instanceof IRocketEngine)
                {
                    if (((IMissileModule) item).getMissileSize() == MissileCasings.STANDARD.ordinal())
                    {
                        if (rocketEngine == null)
                        {
                            rocketEngine = stack.copy();
                            rocketEngine.stackSize = 1;
                            stack.stackSize--;
                            added = true;
                        }
                    }
                }
                else if (item instanceof IWarhead)
                {
                    if (warhead == null)
                    {
                        warhead = stack.copy();
                        warhead.stackSize = 1;
                        stack.stackSize--;
                        added = true;
                    }
                }
                else if (item instanceof IGuidance)
                {
                    if (rocketComputer == null)
                    {
                        rocketComputer = stack.copy();
                        rocketComputer.stackSize = 1;
                        stack.stackSize--;
                        added = true;
                    }
                }
                //Warhead is optional as it can be added later if needed
                if (rocketComputer != null && rocketEngine != null)
                {
                    this.gutsCompleted = true;
                }
                return added;
            }
        }
        return false;
    }

    protected int addRods(int count)
    {
        if (!frameCompleted)
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

    /**
     * Is the recipe finished
     *
     * @return true if the recipe is finished
     */
    public boolean isFinished()
    {
        return skinCompleted && frameCompleted && gutsCompleted;
    }

    public ItemStack getMissileAsItem()
    {
        Missile missile = getMissile();
        if (missile != null)
        {
            return missile.toStack();
        }
        return null;
    }

    /**
     * Generates a new missile instance from the recipes
     * items provided(engine, warhead, computer).
     *
     * @return new missile instance
     */
    public Missile getMissile()
    {
        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.STANDARD, null, null, null);
        if (warhead != null)
        {
            missile.setWarhead(MissileModuleBuilder.INSTANCE.buildWarhead(warhead));
            if (missile.getWarhead() == null)
            {
                //Failed to set warhead
                return null;
            }
        }
        if (rocketEngine != null)
        {
            missile.setEngine(MissileModuleBuilder.INSTANCE.buildEngine(rocketEngine));
            if (missile.getEngine() == null)
            {
                //Failed to set engine
                return null;
            }
        }
        if (rocketComputer != null)
        {
            missile.setGuidance(MissileModuleBuilder.INSTANCE.buildGuidance(rocketComputer));
            if (missile.getGuidance() == null)
            {
                //Failed to set guidance
                return null;
            }
        }
        return missile;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        int rods = nbt.getInteger("rodsContained");
        int plates = nbt.getInteger("platesContained");
        if (rods > 0)
        {
            addRods(rods);
        }
        if (plates > 0)
        {
            addPlates(plates);
        }
        if (nbt.hasKey("rocketComputer"))
        {
            rocketComputer = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("rocketComputer"));
            if (rocketComputer != null && !(rocketComputer.getItem() instanceof IGuidance))
            {
                System.out.println("Error loading standard missile recipe progress. Guidance item is invalid, this will cause issues when constructing the missile.");
            }
        }
        if (nbt.hasKey("rocketEngine"))
        {
            rocketEngine = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("rocketEngine"));
            if (rocketEngine != null && !(rocketEngine.getItem() instanceof IRocketEngine))
            {
                System.out.println("Error loading standard missile recipe progress. Engine item is invalid, this will cause issues when constructing the missile.");
            }
        }
        if (nbt.hasKey("warhead"))
        {
            warhead = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("rocketEngine"));
            if (warhead != null && !(warhead.getItem() instanceof IWarhead))
            {
                System.out.println("Error loading standard missile recipe progress. Warhead item is invalid, this will cause issues when constructing the missile.");
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setInteger("rodsContained", rodsContained);
        nbt.setInteger("platesContained", platesContained);
        if (rocketComputer != null)
        {
            NBTTagCompound cTag = new NBTTagCompound();
            rocketComputer.writeToNBT(cTag);
            nbt.setTag("rocketComputer", cTag);
        }
        if (rocketEngine != null)
        {
            NBTTagCompound cTag = new NBTTagCompound();
            rocketEngine.writeToNBT(cTag);
            nbt.setTag("rocketEngine", cTag);
        }
        if (warhead != null)
        {
            NBTTagCompound cTag = new NBTTagCompound();
            warhead.writeToNBT(cTag);
            nbt.setTag("warhead", cTag);
        }
        return nbt;
    }

    @Override
    public StandardMissileCrafting readBytes(ByteBuf buf)
    {
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        return buf;
    }
}
