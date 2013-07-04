package icbm.gangshao;

import icbm.gangshao.platform.TPaoDaiZhan;
import icbm.gangshao.render.BlockRenderingHandler;
import icbm.gangshao.render.FXBeam;
import icbm.gangshao.render.RCiGuiPao;
import icbm.gangshao.render.REJia;
import icbm.gangshao.render.RenderAATurret;
import icbm.gangshao.render.RenderGunTurret;
import icbm.gangshao.shimian.GuiPlatformAccess;
import icbm.gangshao.shimian.GuiPlatformSlots;
import icbm.gangshao.shimian.GuiPlatformTerminal;
import icbm.gangshao.turret.mount.EJia;
import icbm.gangshao.turret.mount.TCiGuiPao;
import icbm.gangshao.turret.sentries.TFanKong;
import icbm.gangshao.turret.sentries.TQiang;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TQiang.class, new RenderGunTurret());
		ClientRegistry.bindTileEntitySpecialRenderer(TFanKong.class, new RenderAATurret());
		ClientRegistry.bindTileEntitySpecialRenderer(TCiGuiPao.class, new RCiGuiPao());

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
					return new GuiPlatformSlots(player.inventory, ((TPaoDaiZhan) tileEntity));
				case GUI_PLATFORM_TERMINAL_ID:
					return new GuiPlatformTerminal(player, ((TPaoDaiZhan) tileEntity));
				case GUI_PLATFORM_ACCESS_ID:
					return new GuiPlatformAccess(player, ((TPaoDaiZhan) tileEntity));
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
