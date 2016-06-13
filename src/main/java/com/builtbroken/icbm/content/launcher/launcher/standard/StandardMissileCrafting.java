package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IByteBufReader;
import com.builtbroken.mc.core.network.IByteBufWriter;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public static final int MAX_ROD_COUNT = 128;
    public static final int ROD_PER_LEVEL_COUNT = 16;
    public static final int MAX_ROD_LEVEL_COUNT = MAX_ROD_COUNT / ROD_PER_LEVEL_COUNT;

    public static final int MAX_PLATE_COUNT = 36;
    public static final int PLATE_PER_LEVEL_COUNT = 4;
    public static final int MAX_PLATE_LEVEL_COUNT = MAX_PLATE_COUNT / PLATE_PER_LEVEL_COUNT;

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

    protected List<ItemStack> itemsUsed = new ArrayList();


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
            if (isRod(stack))
            {
                return rodsContained < MAX_ROD_COUNT;
            }
            else if (frameCompleted)
            {
                if (isPlate(stack))
                {
                    return platesContained < MAX_PLATE_COUNT;
                }
                else
                {
                    Item item = stack.getItem();
                    if (item instanceof IModuleItem)
                    {
                        IModule module = ((IModuleItem) item).getModule(stack);
                        if (module instanceof IRocketEngine)
                        {
                            return rocketEngine == null;
                        }
                        else if (module instanceof IWarhead)
                        {
                            return warhead == null;
                        }
                        else if (module instanceof IGuidance)
                        {
                            return rocketComputer == null;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the item is part of the recipe,
     *
     * @param stack - stack being added,
     *              can be null but will return false
     * @return true if the item can be added.
     */
    public static boolean isPartOfRecipe(final ItemStack stack)
    {
        if (stack != null)
        {
            if (isRod(stack) || isPlate(stack))
            {
                return true;
            }
            else
            {
                Item item = stack.getItem();
                if (item instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) item).getModule(stack);
                    return module instanceof IRocketEngine || module instanceof IWarhead || module instanceof IGuidance;
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

    private static boolean isRod(final ItemStack stack)
    {
        return hasOreName(OreNames.ROD_IRON, stack);
    }

    private static boolean isPlate(final ItemStack stack)
    {
        return hasOreName(OreNames.PLATE_IRON, stack);
    }

    private static boolean hasOreName(final String name, final ItemStack stack)
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
                ItemStack stackAdd = stack.copy();
                storeItem(stackAdd, added);
                stack.stackSize -= added;
                return added > 0;
            }
            else
            {
                Item item = stack.getItem();
                boolean added = false;
                if (item instanceof IModuleItem)
                {
                    IModule module = ((IModuleItem) item).getModule(stack);
                    if (module instanceof IRocketEngine)
                    {
                        if (rocketEngine == null)
                        {
                            rocketEngine = stack.copy();
                            rocketEngine.stackSize = 1;
                            stack.stackSize--;
                            added = true;
                        }
                    }
                    else if (module instanceof IWarhead)
                    {
                        if (warhead == null)
                        {
                            warhead = stack.copy();
                            warhead.stackSize = 1;
                            stack.stackSize--;
                            added = true;
                        }
                    }
                    else if (module instanceof IGuidance)
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
        }
        return false;
    }

    /**
     * Used to add ore dictionary items to the items used list.
     * Mainly just used to load items from save for legacy saves.
     *
     * @param oreName - ore dictionary name, should contain valid ItemStack.
     *                If not item stack is found it will be ignored.
     * @param count   - number of items to add.
     */
    protected void addItems(String oreName, int count)
    {
        int tempR = count;
        ItemStack oreStack = null;
        List<ItemStack> items = OreDictionary.getOres(oreName);
        if (items != null)
        {
            for (ItemStack s : items)
            {
                if (s != null)
                {
                    oreStack = s.copy();
                }
            }
        }
        while (tempR > 0)
        {
            ItemStack stack = oreStack.copy();
            stack.stackSize = Math.min(tempR, oreStack.getMaxStackSize());
            tempR -= stack.stackSize;
            if (stack.stackSize > 0)
            {
                addItem(stack);
            }
        }
    }

    /**
     * Stores items used in crafting so the exact items
     * can be returned or dropped.
     *
     * @param stack
     * @param added
     */
    protected void storeItem(ItemStack stack, int added)
    {
        ItemStack stackAdd = stack.copy();
        stackAdd.stackSize = added;
        if (stackAdd.stackSize > 0)
        {
            for (ItemStack item : itemsUsed)
            {
                int room = item.getMaxStackSize() - item.stackSize;
                if (room > 0 && InventoryUtility.stacksMatch(item, stackAdd))
                {
                    item.stackSize += Math.min(room, stackAdd.stackSize);
                    stackAdd.stackSize -= Math.min(room, stackAdd.stackSize);
                    if (stackAdd.stackSize <= 0)
                    {
                        stackAdd = null;
                        break;
                    }
                }
            }
            //Anything left
            if (stackAdd != null && stackAdd.stackSize > 0)
            {
                itemsUsed.add(stackAdd);
            }
        }
    }

    protected int addRods(int count)
    {
        if (!frameCompleted)
        {
            int addition = Math.min(count, Math.max(MAX_ROD_COUNT - rodsContained, 0));
            if (addition > 0)
            {
                rodsContained += addition;
                frameLevel = rodsContained / ROD_PER_LEVEL_COUNT;
                if (rodsContained >= MAX_ROD_COUNT)
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
                plateLevel = platesContained / PLATE_PER_LEVEL_COUNT;
                if (platesContained >= MAX_PLATE_COUNT)
                {
                    skinCompleted = true;
                }
            }
            //Failed to set guidance
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
            if (rocketComputer != null && !(rocketComputer.getItem() instanceof IModuleItem))
            {
                System.out.println("Error loading standard missile recipe progress. Guidance item is invalid, this will cause issues when constructing the missile.");
            }
        }
        if (nbt.hasKey("rocketEngine"))
        {
            rocketEngine = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("rocketEngine"));
            if (rocketEngine != null && !(rocketEngine.getItem() instanceof IModuleItem))
            {
                System.out.println("Error loading standard missile recipe progress. Engine item is invalid, this will cause issues when constructing the missile.");
            }
        }
        if (nbt.hasKey("warhead"))
        {
            warhead = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("rocketEngine"));
            if (warhead != null && !(warhead.getItem() instanceof IModuleItem))
            {
                System.out.println("Error loading standard missile recipe progress. Warhead item is invalid, this will cause issues when constructing the missile.");
            }
        }
        if (nbt.hasKey("itemsUsed", 10))
        {
            itemsUsed.clear();
            NBTTagList list = nbt.getTagList("itemsUsed", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
                if (stack != null)
                {
                    itemsUsed.add(stack);
                }
                else
                {
                    Engine.error("Failed to load ItemStack from nbt for " + this + "\n" + list.getStringTagAt(i));
                }
            }
        }
        else
        {
            if (rods > 0)
            {
                addItems(OreNames.ROD_IRON, rods);
            }
            if (plates > 0)
            {
                addItems(OreNames.PLATE_IRON, plates);
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
        //Warhead is optional as it can be added later if needed
        if (rocketComputer != null && rocketEngine != null)
        {
            this.gutsCompleted = true;
        }

        if (itemsUsed != null && !itemsUsed.isEmpty())
        {
            NBTTagList list = new NBTTagList();
            for (ItemStack stack : itemsUsed)
            {
                list.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("itemsUsed", list);
        }
        return nbt;
    }

    @Override
    public StandardMissileCrafting readBytes(ByteBuf buf)
    {
        clear();
        addRods(buf.readInt());
        addPlates(buf.readInt());

        ItemStack stack = ByteBufUtils.readItemStack(buf);
        if (stack.getItem() instanceof IModuleItem)
        {
            warhead = stack;
        }
        stack = ByteBufUtils.readItemStack(buf);
        if (stack.getItem() instanceof IModuleItem)
        {
            rocketComputer = stack;
        }
        stack = ByteBufUtils.readItemStack(buf);
        if (stack.getItem() instanceof IModuleItem)
        {
            rocketEngine = stack;
        }
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        buf.writeInt(rodsContained);
        buf.writeInt(platesContained);
        ByteBufUtils.writeItemStack(buf, warhead != null ? warhead : new ItemStack(Blocks.stone));
        ByteBufUtils.writeItemStack(buf, rocketComputer != null ? rocketComputer : new ItemStack(Blocks.stone));
        ByteBufUtils.writeItemStack(buf, rocketEngine != null ? rocketEngine : new ItemStack(Blocks.stone));
        return buf;
    }

    /**
     * Called to drop all stored items on the ground
     *
     * @param location - place to drop the items
     */
    public void dropItems(Location location)
    {
        if (rocketEngine != null)
        {
            InventoryUtility.dropItemStack(location, rocketEngine);
            rocketEngine = null;
        }
        if (rocketComputer != null)
        {
            InventoryUtility.dropItemStack(location, rocketComputer);
            rocketComputer = null;
        }
        if (warhead != null)
        {
            InventoryUtility.dropItemStack(location, warhead);
            warhead = null;
        }
        if (itemsUsed != null & !itemsUsed.isEmpty())
        {
            Iterator<ItemStack> it = itemsUsed.iterator();
            while (it.hasNext())
            {
                ItemStack stack = it.next();
                if (stack != null && stack.stackSize > 0)
                {
                    InventoryUtility.dropItemStack(location, stack);
                }
                it.remove();
            }
        }
        clear();
    }

    protected void clear()
    {
        rodsContained = 0;
        platesContained = 0;
        frameCompleted = false;
        skinCompleted = false;
        gutsCompleted = false;
        frameLevel = 0;
        plateLevel = 0;
    }
}
