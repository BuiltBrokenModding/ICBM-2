package icbm.explosion.missile.modular;

import icbm.explosion.ICBMExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

/** @author DarkGuardsman */
public class ItemBlockMissileTable extends ItemBlock
{

	public ItemBlockMissileTable(int par1)
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
		if (BlockMissileTable.canPlaceBlockAt(world, x, y, z, placeSide, rot))
		{

			// TODO place object on side of block clicked
			world.setBlock(x, y, z, this.getBlockID(), 0, 3);
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity instanceof TileEntityMissileTable)
			{
				((TileEntityMissileTable) entity).setPlacedSide(placeSide);
				((TileEntityMissileTable) entity).setRotation(rot);
			}
			ICBMExplosion.blockMissileTable.onBlockPlacedBy(world, x, y, z, entityPlayer, itemStack);
			return true;
		}
		return false;
	}
}
