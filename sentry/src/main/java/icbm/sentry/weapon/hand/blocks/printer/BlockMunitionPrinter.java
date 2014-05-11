package icbm.sentry.weapon.hand.blocks.printer;

import icbm.core.prefab.BlockICBM;
import icbm.explosion.ICBMExplosion;
import icbm.sentry.ICBMSentry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.prefab.tile.IRedstoneReceptor;
import calclavia.lib.prefab.tile.IRotatable;

public class BlockMunitionPrinter extends BlockICBM {

	public BlockMunitionPrinter(int id) {
		super(3879, "munitionPrinter");
	}

	@Override
	public void onBlockAdded(World par1World, int x, int y, int z) {
		this.isBeingPowered(par1World, x, y, z);
	}

	/** Called when the block is placed in the world. */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase par5EntityLiving, ItemStack itemStack) {
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRotatable) {
			IRotatable rotatableEntity = ((IRotatable) tileEntity);

			switch (angle) {
				case 0:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(3));
					break;
				case 1:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(4));
					break;
				case 2:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(2));
					break;
				case 3:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(5));
					break;
			}
		}
	}

	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.inventory.getCurrentItem() != null) {
			if (player.inventory.getCurrentItem().itemID == ICBMExplosion.itemLaserDesignator.itemID) {
				return false;
			} else if (player.inventory.getCurrentItem().itemID == ICBMExplosion.itemRadarGun.itemID) { return false; }
		}

		if (player.isSneaking()) {
			return false;
		}
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		System.out.println(tile);
		
		player.openGui(ICBMSentry.INSTANCE, 2, world, x, y, z);
		return true;
	}

	public static boolean canBePlacedAt(World world, int x, int y, int z, int metadata, int direction) {
		switch (metadata) {
			default: {
				return world.getBlockMaterial(x, y - 1, z).isSolid();
			}
			case 0: {
				// Launcher Base
				if (direction == 0 || direction == 2) {
					return world.getBlockId(x, y, z) == 0 &&
					// Left
							world.getBlockId(x + 1, y, z) == 0 && world.getBlockId(x + 1, y + 1, z) == 0 && world.getBlockId(x + 1, y + 2, z) == 0 &&
							// Right
							world.getBlockId(x - 1, y, z) == 0 && world.getBlockId(x - 1, y + 1, z) == 0 && world.getBlockId(x - 1, y + 2, z) == 0;
				} else if (direction == 1 || direction == 3) { return world.getBlockId(x, y, z) == 0 &&
				// Front
						world.getBlockId(x, y, z + 1) == 0 && world.getBlockId(x, y + 1, z + 1) == 0 && world.getBlockId(x, y + 2, z + 1) == 0 &&
						// Back
						world.getBlockId(x, y, z - 1) == 0 && world.getBlockId(x, y + 1, z - 1) == 0 && world.getBlockId(x, y + 2, z - 1) == 0; }
			}
			case 2: {
				// Launcher Frame
				return world.getBlockMaterial(x, y - 1, z).isSolid() && world.getBlockId(x, y, z) == 0 && world.getBlockId(x, y + 1, z) == 0 && world.getBlockId(x, y + 2, z) == 0;
			}
			case 4: {
				return world.getBlockId(x, y, z) == 0 && world.getBlockId(x, y + 1, z) == 0;
			}
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		int direction = 0;
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getDirection().ordinal();
		}

		return canBePlacedAt(world, x, y, z, world.getBlockMetadata(x, y, z), direction);
	}

	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int par5) {
		this.isBeingPowered(par1World, x, y, z);
	}

	/** Checks of this block is being powered by redstone */
	public void isBeingPowered(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRedstoneReceptor) {
			if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
				// Send signal to tile entity
				((IRedstoneReceptor) tileEntity).onPowerOn();
			} else {
				((IRedstoneReceptor) tileEntity).onPowerOff();
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileMunitionPrinter();
	}
}
