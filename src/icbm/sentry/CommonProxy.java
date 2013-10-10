package icbm.sentry;

import icbm.sentry.container.ContainerTurretPlatform;
import icbm.sentry.platform.TPaoTaiZhan;
import icbm.sentry.turret.mount.TCiGuiPao;
import icbm.sentry.turret.sentries.TFanKong;
import icbm.sentry.turret.sentries.TLeiShe;
import icbm.sentry.turret.sentries.TQiang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
	/** GUI IDs */
	public static final int GUI_PLATFORM_ID = 0;
	public static final int GUI_PLATFORM_TERMINAL_ID = 1;
	public static final int GUI_PLATFORM_ACCESS_ID = 2;

	public void init()
	{
		GameRegistry.registerTileEntity(TQiang.class, "ICBMGunTurret");
		GameRegistry.registerTileEntity(TFanKong.class, "ICBMAATurret");
		GameRegistry.registerTileEntity(TCiGuiPao.class, "ICBMRailgun");
		GameRegistry.registerTileEntity(TLeiShe.class, "ICBMLeiSheF");
		GameRegistry.registerTileEntity(TPaoTaiZhan.class, "ICBMPlatform");
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
					return new ContainerTurretPlatform(player.inventory, ((TPaoTaiZhan) tileEntity));
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	/**
	 * Renders a bullet tracer from one spot to another will later be replaced with start and degree
	 */
	public void renderTracer(World world, Vector3 position, Vector3 target)
	{

	}

	public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
	{

	}
}
