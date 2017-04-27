package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.prefab.items.ItemWorldPos;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.itemGPSTool), " I ", "BCB", "ICI", 'I', OreNames.INGOT_IRON, 'B', Blocks.wooden_button, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        String localization = LanguageUtility.getLocal(getUnlocalizedName() + ".info");
        if (localization != null && !localization.isEmpty())
        {
            String[] split = localization.split(",");
            for (String line : split)
            {
                lines.add(line.trim());
            }
        }
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
        {
            return this.linked_icon;
        }
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
        {
            return true;
        }

        Location location = new Location(world, x, y, z);
        TileEntity tile = location.getTileEntity();
        if (tile instanceof IMultiTile)
        {
            IMultiTileHost host = ((IMultiTile) tile).getHost();
            if (host instanceof TileEntity)
            {
                tile = (TileEntity) host;
            }
        }

        if (player.isSneaking())
        {
            setLocation(stack, location);
            LanguageUtility.addChatToPlayer(player, "gps.pos.set");
            stack.setItemDamage(1);
            player.inventoryContainer.detectAndSendChanges();
            return true;
        }
        else
        {
            Location storedLocation = getLocation(stack);
            if (storedLocation == null || !storedLocation.isAboveBedrock())
            {
                LanguageUtility.addChatToPlayer(player, "gps.error.pos.invalid");
                return true;
            }
            else if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) ((ITileNodeHost) tile).getTileNode()).setTarget(storedLocation.toPos());
                LanguageUtility.addChatToPlayer(player, "gps.data.transferred");
                return true;
            }
        }
        return false;
    }
}
