package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.blast.troll.ExFirework;
import com.builtbroken.mc.framework.recipe.item.RecipeShapelessOre;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Handles creating firework explosive
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/31/2017.
 */
public class RecipeFireworkEx extends RecipeShapelessOre
{
    public RecipeFireworkEx()
    {
        super(new ItemStack(ICBM_API.itemExplosive, 1, ItemExplosive.ExplosiveItems.FIREWORK.ordinal()),
                new ItemStack(ICBM_API.itemExplosive, 1, ItemExplosive.ExplosiveItems.FIREWORK.ordinal()),
                new ItemStack(Items.fireworks));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        return getCraftingResult(inv) != null; //Lazy :P
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack core = null;
        ItemStack firework = null;

        //Find items
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack slotStack = inv.getStackInSlot(i);
            if (slotStack != null)
            {
                //Check core
                if (core == null && slotStack.getItem() == ICBM_API.itemExplosive
                        && slotStack.getItemDamage() == ItemExplosive.ExplosiveItems.FIREWORK.ordinal()
                        && (slotStack.getTagCompound() == null || !slotStack.getTagCompound().hasKey(ExFirework.NBT_KEY)))
                {
                    core = slotStack;
                }
                //Check firework
                else if (firework == null && slotStack.getItem() == Items.fireworks)
                {
                    firework = slotStack;
                }
                //Anthing else, then invalid
                else
                {
                    return null;
                }
            }
        }

        //If both core and firework exist, create result
        if (core != null && firework != null)
        {
            //Copy stack to prevent duplication issues
            ItemStack result = core.copy();

            //Set stack to size 1
            result.stackSize = 1;

            //Setup NBT
            if (result.getTagCompound() == null)
            {
                result.setTagCompound(new NBTTagCompound());
            }

            //Write firework to NBT
            ((ItemExplosive)ICBM_API.itemExplosive)
                    .getAdditionalExplosiveData(result)
                    .setTag(ExFirework.NBT_KEY, firework.writeToNBT(new NBTTagCompound()));


            //Return
            return result;
        }

        return null;
    }
}
