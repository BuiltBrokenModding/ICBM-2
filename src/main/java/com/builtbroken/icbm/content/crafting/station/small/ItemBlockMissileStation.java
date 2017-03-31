package com.builtbroken.icbm.content.crafting.station.small;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2015.
 */
public class ItemBlockMissileStation extends ItemBlock
{
    public ItemBlockMissileStation(Block p_i45328_1_)
    {
        super(p_i45328_1_);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        ForgeDirection placementSide = ForgeDirection.getOrientation(side);
        ForgeDirection rotation = placementSide == ForgeDirection.UP && placementSide == ForgeDirection.DOWN ? ForgeDirection.NORTH : ForgeDirection.UP;
        ForgeDirection rotation2 = null;
        switch(side)
        {
            case 0:
            case 1:
            case 2:
            case 3:
                rotation2 = ForgeDirection.EAST;
                break;
            case 4:
            case 5:
                rotation2 = ForgeDirection.NORTH;
                break;
        }
        boolean one = isPlacementClear(world, new Pos(x, y, z), placementSide, rotation);
        boolean two = one || isPlacementClear(world, new Pos(x, y, z), placementSide, rotation2);
        if(one || two)
        {
            boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
            if (placed)
            {
                world.setBlockMetadataWithNotify(x, y, z, side, 3);
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof TileSmallMissileWorkstation)
                {
                    if(one)
                    {
                        ((TileSmallMissileWorkstation) tile).setFacing(rotation);
                    }
                    else
                    {
                        ((TileSmallMissileWorkstation) tile).setFacing(rotation2);
                    }
                }
                world.markBlockForUpdate(x, y, z);
                return placed;
            }
        }
        else if(!world.isRemote)
        {
            player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocalName("info.icbm:missileStation.placement.blocked")));
        }
        return false;
    }

    protected boolean isPlacementClear(final World world, final Pos center, final ForgeDirection side, final ForgeDirection rotation)
    {
        for (IPos3D p : TileSmallMissileWorkstation.getLayoutOfMultiBlock(rotation, side).keySet())
        {
            Pos pos = center.add(p);
            Block block = world.getBlock((int) pos.x(), (int) pos.y(), (int) pos.z());
            if (!block.isAir(world, (int) pos.x(), (int) pos.y(), (int) pos.z()))
            {
                TileEntity tile = pos.getTileEntity(world);
                if (tile instanceof IMultiTile && (((IMultiTile) tile).getHost() == this || ((IMultiTile) tile).getHost() == null))
                {
                    continue;
                }
                return false;
            }
        }
        return true;
    }
}
