package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.config.ConfigBlocks;

import java.util.ArrayList;

/**
 * Recipe for infusing a jar containing a node with a warhead
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/2/2016.
 */
public class InfusionRecipeJar extends InfusionRecipe
{
    public InfusionRecipeJar(String research, int inst, AspectList aspects2, ItemStack[] recipe)
    {
        super(research, new ItemStack(ConfigBlocks.blockJar, 1, 2), inst, aspects2, new ItemStack(ConfigBlocks.blockJar), recipe);
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player)
    {
        if (this.research.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), this.research))
        {
            return false;
        }
        else if (central.getItem() == Item.getItemFromBlock(ConfigBlocks.blockJar) && central.getItemDamage() == 2)
        {
            int count = 0;
            for (ItemStack stack : input)
            {
                if (stack.getItem() == Item.getItemFromBlock(ICBM.blockWarhead))
                {
                    count++;
                    IWarhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(stack);
                    if (warhead.getExplosive() != null)
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
            return count == 1;
        }
        return false;
    }

    @Override
    public Object getRecipeOutput(ItemStack input)
    {
        if (input != null && input.getItem() == Item.getItemFromBlock(ConfigBlocks.blockJar))
        {
            IWarhead warhead = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ExplosiveRegistry.get("ThaumNode"));
            if (warhead.getAdditionalExplosiveData() == null)
            {
                warhead.setExplosive(warhead.getExplosive(), warhead.getExplosiveSize(), new NBTTagCompound());
            }
            warhead.getAdditionalExplosiveData().setTag("nodeJar", input.writeToNBT(new NBTTagCompound()));
            return warhead.toStack();
        }
        return null;
    }
}
