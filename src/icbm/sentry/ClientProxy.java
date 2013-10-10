package icbm.sentry;

import icbm.core.base.REJia;
import icbm.sentry.platform.TPaoTaiZhan;
import icbm.sentry.render.BlockRenderingHandler;
import icbm.sentry.render.FXBeam;
import icbm.sentry.render.RCiGuiPao;
import icbm.sentry.render.RFanKong;
import icbm.sentry.render.RLeiShe;
import icbm.sentry.render.RQiang;
import icbm.sentry.shimian.GuiPlatformAccess;
import icbm.sentry.shimian.GuiPlatformSlots;
import icbm.sentry.shimian.GuiPlatformTerminal;
import icbm.sentry.turret.mount.EJia;
import icbm.sentry.turret.mount.TCiGuiPao;
import icbm.sentry.turret.sentries.TFanKong;
import icbm.sentry.turret.sentries.TLeiShe;
import icbm.sentry.turret.sentries.TQiang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TQiang.class, new RQiang());
		ClientRegistry.bindTileEntitySpecialRenderer(TFanKong.class, new RFanKong());
		ClientRegistry.bindTileEntitySpecialRenderer(TCiGuiPao.class, new RCiGuiPao());
		ClientRegistry.bindTileEntitySpecialRenderer(TLeiShe.class, new RLeiShe());

		RenderingRegistry.registerEntityRenderingHandler(EJia.class, new REJia());
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
					return new GuiPlatformSlots(player.inventory, ((TPaoTaiZhan) tileEntity));
				case GUI_PLATFORM_TERMINAL_ID:
					return new GuiPlatformTerminal(player, ((TPaoTaiZhan) tileEntity));
				case GUI_PLATFORM_ACCESS_ID:
					return new GuiPlatformAccess(player, ((TPaoTaiZhan) tileEntity));
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
