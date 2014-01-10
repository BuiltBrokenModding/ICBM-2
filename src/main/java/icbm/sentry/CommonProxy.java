package icbm.sentry;

import icbm.sentry.platform.ContainerTurretPlatform;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.turret.mount.TileEntityRailGun;
import icbm.sentry.turret.sentries.TileEntityAAGun;
import icbm.sentry.turret.sentries.TileEntityGunTurret;
import icbm.sentry.turret.sentries.TileEntityLaserGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
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
        GameRegistry.registerTileEntity(TileEntityGunTurret.class, "ICBMGunTurret");
        GameRegistry.registerTileEntity(TileEntityAAGun.class, "ICBMAATurret");
        GameRegistry.registerTileEntity(TileEntityRailGun.class, "ICBMRailgun");
        GameRegistry.registerTileEntity(TileEntityLaserGun.class, "ICBMLeiSheF");
        GameRegistry.registerTileEntity(TileTurretPlatform.class, "ICBMPlatform");
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
                    return new ContainerTurretPlatform(player.inventory, ((TileTurretPlatform) tileEntity));
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    /** Renders a bullet tracer from one spot to another will later be replaced with start and degree */
    public void renderTracer(World world, Vector3 position, Vector3 target)
    {

    }

    public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
    {

    }
}
