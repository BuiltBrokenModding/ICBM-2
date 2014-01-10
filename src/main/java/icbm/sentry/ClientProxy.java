package icbm.sentry;

import icbm.core.prefab.EmptyRenderer;
import icbm.sentry.gui.GuiPlatformAccess;
import icbm.sentry.gui.GuiPlatformSlots;
import icbm.sentry.gui.GuiPlatformTerminal;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.render.BlockRenderingHandler;
import icbm.sentry.render.FXBeam;
import icbm.sentry.render.RenderAAGun;
import icbm.sentry.render.RenderGunTurret;
import icbm.sentry.render.RenderLaserTurret;
import icbm.sentry.render.RenderRailGun;
import icbm.sentry.turret.mount.EntityMountPoint;
import icbm.sentry.turret.mount.TileEntityRailGun;
import icbm.sentry.turret.sentries.TileEntityAAGun;
import icbm.sentry.turret.sentries.TileEntityGunTurret;
import icbm.sentry.turret.sentries.TileEntityLaserGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
    }

    @Override
    public void init()
    {
        super.init();

        /** TileEntities */
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGunTurret.class, new RenderGunTurret());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAAGun.class, new RenderAAGun());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailGun.class, new RenderRailGun());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserGun.class, new RenderLaserTurret());

        RenderingRegistry.registerEntityRenderingHandler(EntityMountPoint.class, new EmptyRenderer());
        RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null)
        {
            switch (ID)
            {
                case GUI_PLATFORM_ID:
                    return new GuiPlatformSlots(player.inventory, ((TileEntityTurretPlatform) tileEntity));
                case GUI_PLATFORM_TERMINAL_ID:
                    return new GuiPlatformTerminal(player.inventory, ((TileEntityTurretPlatform) tileEntity));
                case GUI_PLATFORM_ACCESS_ID:
                    return new GuiPlatformAccess(player.inventory, ((TileEntityTurretPlatform) tileEntity));
            }
        }

        return null;
    }

    @Override
    public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, red, green, blue, age));
    }
}
