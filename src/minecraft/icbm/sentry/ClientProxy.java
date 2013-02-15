package icbm.sentry;

import icbm.sentry.gui.GuiTerminal;
import icbm.sentry.gui.GuiTurretPlatform;
import icbm.sentry.platform.TileEntityTurretPlatform;
import icbm.sentry.render.BlockRenderingHandler;
import icbm.sentry.render.RenderGunTurret;
import icbm.sentry.render.RenderRailgun;
import icbm.sentry.terminal.TileEntityTerminal;
import icbm.sentry.turret.TileEntityBaseTurret;
import icbm.sentry.turret.TileEntityRailgun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		super.preInit();
		MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();
		MinecraftForgeClient.preloadTexture(ICBMSentry.BLOCK_TEXTURE_PATH);
		MinecraftForgeClient.preloadTexture(ICBMSentry.ITEM_TEXTURE_PATH);

		/**
		 * TileEntities
		 */
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBaseTurret.class, new RenderGunTurret());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailgun.class, new RenderRailgun());

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
					return new GuiTurretPlatform(player.inventory, ((TileEntityTurretPlatform) tileEntity));
				case GUI_CONSOLE_ID:
					return new GuiTerminal(player, ((TileEntityTerminal) tileEntity));
			}
		}

		return null;
	}
}
