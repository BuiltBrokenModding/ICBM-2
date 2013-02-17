package icbm.sentry.platform;

import icbm.api.ICBMTab;
import icbm.sentry.CommonProxy;
import icbm.sentry.ICBMSentry;
import icbm.sentry.api.ISpecialAccess;
import icbm.sentry.terminal.AccessLevel;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;

public class BlockTurretPlatform extends BlockMachine
{
	public BlockTurretPlatform(int id)
	{
		super(id, UniversalElectricity.machine);
		this.setBlockName("turretPlatform");
		this.setHardness(50f);
		this.setResistance(100f);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setTextureFile(ICBMSentry.BLOCK_TEXTURE_PATH);
	}

	@Override
	public int getBlockTextureFromSide(int side)
	{
		if (side == 0)
		{
			return this.blockIndexInTexture;
		}
		else if (side == 1)
		{
			return this.blockIndexInTexture + 1;
		}

		return this.blockIndexInTexture + 2;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity)
	{
		if (entity instanceof EntityPlayer && !world.isRemote)
		{
			TileEntity ent = world.getBlockTileEntity(x, y, z);

			if (ent instanceof ISpecialAccess)
			{
				((ISpecialAccess) ent).addUserAccess(((EntityPlayer) entity).username, AccessLevel.OWNER, true);
			}
		}
	}

	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{

		/**
		 * Only allow the platform to be open if there is a turret installed with it.
		 */
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityTurretPlatform)
		{
			if (player.getCurrentEquippedItem() != null && side == ((TileEntityTurretPlatform) tileEntity).deployDirection.ordinal())
			{
				if (player.getCurrentEquippedItem().itemID == ICBMSentry.blockTurret.blockID)
				{
					return false;
				}
			}

			if (((TileEntityTurretPlatform) tileEntity).getTurret() != null)
			{
				if (!world.isRemote)
				{
					player.openGui(ICBMSentry.instance, CommonProxy.GUI_PLATFORM_ID, world, x, y, z);
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityTurretPlatform();
	}
}
