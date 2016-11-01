package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.rail.BlockRail;
import com.builtbroken.icbm.content.rail.IMissileRail;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            final Block block = world.getBlock(x, y, z);
            final TileEntity tile = world.getTileEntity(x, y, z);

            if (block instanceof BlockRail)
            {
                BlockRail.RailDirections railType = BlockRail.RailDirections.get(world.getBlockMetadata(x, y, z));
                EntityCart cart = getCart(world, stack.getItemDamage());
                cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
                mountEntity(cart, railType.side, railType.facing, block.getBlockBoundsMaxY());
            }
            else if (tile instanceof IMissileRail)
            {
                mountEntity(getCart(world, stack.getItemDamage()), ((IMissileRail) tile).getAttachedDirection(), ((IMissileRail) tile).getFacingDirection(), ((IMissileRail) tile).railHeight());
            }
            if (!player.capabilities.isCreativeMode)
            {
                stack.stackSize--;
            }
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xf, float yf, float zf)
    {
        return true;
    }

    protected void mountEntity(EntityCart cart, ForgeDirection side, ForgeDirection facing, double railHeight)
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
    protected EntityCart getCart(World world, int meta)
    {
        //More types will be added later
        return new EntityCart(world);
    }
}
