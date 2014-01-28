package icbm.sentry;

import icbm.core.prefab.EmptyRenderer;
import icbm.sentry.render.FXBeam;
import icbm.sentry.render.RenderGunTurret;
import icbm.sentry.turret.sentryhandler.EntitySentryFake;
import icbm.sentry.turret.tiles.TileSentry;
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
        RenderingRegistry.registerEntityRenderingHandler(EntitySentryFake.class, new EmptyRenderer());
        //RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSentry.class, new RenderGunTurret());
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
