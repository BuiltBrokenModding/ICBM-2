package icbm.sentry;

import icbm.core.prefab.EmptyRenderer;
import icbm.sentry.render.BlockRenderingHandler;
import icbm.sentry.render.FXBeam;
import icbm.sentry.render.RenderAAGun;
import icbm.sentry.render.RenderGunTurret;
import icbm.sentry.render.RenderLaserTurret;
import icbm.sentry.render.RenderRailGun;
import icbm.sentry.turret.mount.EntityMountPoint;
import icbm.sentry.turret.mount.TileRailGun;
import icbm.sentry.turret.sentries.TileEntityAAGun;
import icbm.sentry.turret.sentries.TileEntityGunTurret;
import icbm.sentry.turret.sentries.TileEntityLaserGun;
import net.minecraft.entity.player.EntityPlayer;
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
        ClientRegistry.bindTileEntitySpecialRenderer(TileRailGun.class, new RenderRailGun());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserGun.class, new RenderLaserTurret());

        RenderingRegistry.registerEntityRenderingHandler(EntityMountPoint.class, new EmptyRenderer());
        RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {

        return null;
    }

    @Override
    public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, red, green, blue, age));
    }
}
