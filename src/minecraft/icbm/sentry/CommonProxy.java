package icbm.sentry;

import icbm.sentry.container.ContainerTurretPlatform;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.turret.TileEntityGunTurret;
import icbm.sentry.turret.TileEntityRailgun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
	/**
	 * GUI IDs
	 */
	public static final int GUI_PLATFORM_ID = 0;
	public static final int GUI_CONSOLE_ID = 1;
	public static final int GUI_RAILGUN_ID = 2;

	public void init()
	{
		GameRegistry.registerTileEntity(TileEntityGunTurret.class, "ICBMGunTurret");
		GameRegistry.registerTileEntity(TileEntityRailgun.class, "ICBMRailgun");
		GameRegistry.registerTileEntity(TileEntityTurretPlatform.class, "ICBMPlatform");
		GameRegistry.registerTileEntity(TileEntityMulti.class, "ICBMMultiblock");
	}

	public void preInit()
	{

	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case GUI_PLATFORM_ID:
					return new ContainerTurretPlatform(player.inventory, ((TileEntityTurretPlatform) tileEntity));
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
