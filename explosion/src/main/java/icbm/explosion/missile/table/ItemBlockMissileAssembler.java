package icbm.explosion.missile.table;

import icbm.explosion.ICBMExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

/** @author DarkGuardsman */
public class ItemBlockMissileAssembler extends ItemBlock
{
    public ItemBlockMissileAssembler(int par1)
    {
        super(par1);
    }

    @Override
    public boolean placeBlockAt(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        ForgeDirection placeSide = ForgeDirection.getOrientation(side);
        byte rot = (byte) (MathHelper.floor_double((entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        if (placeSide != ForgeDirection.UP && placeSide != ForgeDirection.DOWN)
        {
            rot = 3;
        }
        if (BlockMissileAssembler.canPlaceBlockAt(world, x, y, z, placeSide, rot))
        {

            // TODO place object on side of block clicked
            world.setBlock(x, y, z, this.getBlockID(), 0, 3);
            TileEntity entity = world.getBlockTileEntity(x, y, z);
            if (entity instanceof TileMissileAssembler)
            {
                ((TileMissileAssembler) entity).setPlacedSide(placeSide);
                ((TileMissileAssembler) entity).setRotation(rot);
            }
            ICBMExplosion.blockMissileAssembler.onBlockPlacedBy(world, x, y, z, entityPlayer, itemStack);
            return true;
        }
        return false;
    }
}
