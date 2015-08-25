package com.builtbroken.icbm.content.launcher.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.items.IWorldPosItem;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.prefab.items.ItemWorldPos;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Basic item that can stored the location it right clicks
 * Created by Dark on 6/2/2015.
 */
public class ItemGPSFlag extends ItemWorldPos implements IWorldPosItem, IPostInit
{
    IIcon linked_icon;

    public ItemGPSFlag()
    {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallLauncher), " I ", "BCB", "ICI", 'I', Items.iron_ingot, 'B', Blocks.wooden_button, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(ICBM.PREFIX + "gpsflag.unlinked");
        this.linked_icon = reg.registerIcon(ICBM.PREFIX + "gpsflag.linked");
    }

    @Override
    public IIcon getIconFromDamage(int meta)
    {
        if (meta == 1)
            return this.linked_icon;
        return this.itemIcon;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (player.isSneaking())
        {
            stack.setTagCompound(null);
            stack.setItemDamage(0);
            LanguageUtility.addChatToPlayer(player, "gps.cleared");
            player.inventoryContainer.detectAndSendChanges();
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if (world.isRemote)
            return true;

        Location location = new Location(world, x, y, z);
        TileEntity tile = location.getTileEntity();

        if (player.isSneaking())
        {
            setLocation(stack, location);
            LanguageUtility.addChatToPlayer(player, "gps.pos.set");
            stack.setItemDamage(1);
            player.inventoryContainer.detectAndSendChanges();
            return true;
        } else
        {
            Location storedLocation = getLocation(stack);
            if (storedLocation == null || !storedLocation.isAboveBedrock())
            {
                LanguageUtility.addChatToPlayer(player, "gps.error.pos.invalid");
                return true;
            } else if (tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).setTarget(storedLocation.toPos());
                LanguageUtility.addChatToPlayer(player, "gps.data.transferred");
                return true;
            }
        }
        return false;
    }
}
