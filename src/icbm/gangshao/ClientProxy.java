package icbm.gangshao;

import icbm.gangshao.platform.TPaoDaiZhan;
import icbm.gangshao.render.BlockRenderingHandler;
import icbm.gangshao.render.RenderAATurret;
import icbm.gangshao.render.RenderFakeMountable;
import icbm.gangshao.render.RenderGunTurret;
import icbm.gangshao.render.RenderRailgun;
import icbm.gangshao.shimian.GuiPlatformAccess;
import icbm.gangshao.shimian.GuiPlatformSlots;
import icbm.gangshao.shimian.GuiPlatformTerminal;
import icbm.gangshao.turret.mount.EntityFakeMountable;
import icbm.gangshao.turret.mount.TileEntityRailgun;
import icbm.gangshao.turret.sentries.TileEntityAATurret;
import icbm.gangshao.turret.sentries.TileEntityGunTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAATurret.class, new RenderAATurret());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailgun.class, new RenderRailgun());

		RenderingRegistry.registerEntityRenderingHandler(EntityFakeMountable.class, new RenderFakeMountable());
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
					return new GuiPlatformSlots(player.inventory, ((TPaoDaiZhan) tileEntity));
				case GUI_PLATFORM_TERMINAL_ID:
					return new GuiPlatformTerminal(player, ((TPaoDaiZhan) tileEntity));
				case GUI_PLATFORM_ACCESS_ID:
					return new GuiPlatformAccess(player, ((TPaoDaiZhan) tileEntity));
			}
		}

		return null;
	}
}
