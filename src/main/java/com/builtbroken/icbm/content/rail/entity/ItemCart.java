package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.rail.BlockRail;
import com.builtbroken.icbm.content.rail.IMissileRail;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Handles placement and inventory movement of the missile rail cart
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class ItemCart extends Item
{
    public ItemCart()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName(ICBM.PREFIX + "missileRailCart");
        this.setTextureName(ICBM.PREFIX + "missileCartItem");
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            if (placeCart(world, x, y, z, stack.getItemDamage()) != null && !player.capabilities.isCreativeMode)
            {
                stack.stackSize--;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xf, float yf, float zf)
    {
        return true;
    }

    /**
     * Places the cart on top of the rail
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type  - type of the cart @see {@link CartTypes}
     * @return true if the entity was placed into the world
     */
    public static EntityCart placeCart(World world, int x, int y, int z, int type)
    {
        final Block block = world.getBlock(x, y, z);
        final TileEntity tile = world.getTileEntity(x, y, z);

        if (block instanceof BlockRail)
        {
            BlockRail.RailDirections railType = BlockRail.RailDirections.get(world.getBlockMetadata(x, y, z));
            EntityCart cart = getCart(world, type);
            cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
            mountEntity(cart, railType.side, railType.facing, block.getBlockBoundsMaxY());
            return cart;
        }
        else if (tile instanceof IMissileRail)
        {
            EntityCart cart = getCart(world, type);
            cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
            mountEntity(cart, ((IMissileRail) tile).getAttachedDirection(), ((IMissileRail) tile).getFacingDirection(), ((IMissileRail) tile).getRailHeight());
            return cart;
        }
        return null;
    }


    /**
     * Sets the cart onto the rail and spawns it into the world
     *
     * @param cart
     * @param side
     * @param facing
     * @param railHeight
     */
    public static void mountEntity(final EntityCart cart, final ForgeDirection side, final ForgeDirection facing, double railHeight)
    {
        cart.railSide = side;
        cart.recenterCartOnRail(side, facing, railHeight);
        cart.worldObj.spawnEntityInWorld(cart);
    }

    /**
     * Gets the cart for the damage of the item
     *
     * @param world
     * @param meta
     * @return
     */
    public static EntityCart getCart(final World world, int meta)
    {
        //More types will be added later
        final EntityCart cart = new EntityCart(world);
        cart.setType(CartTypes.values()[meta]);
        return cart;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        for (int i = 0; i < CartTypes.values().length; i++)
        {
            p_150895_3_.add(new ItemStack(p_150895_1_, 1, i));
        }
    }
}
