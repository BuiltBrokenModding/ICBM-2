package icbm.wanyi;

import icbm.core.ShengYin;
import icbm.core.ZhuYao;
import icbm.wanyi.gui.GFrequency;
import icbm.wanyi.gui.GYinGanQi;
import icbm.wanyi.render.RBYinXing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(ShengYin.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();
		RenderingRegistry.registerBlockHandler(new RBYinXing());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null || ID == ZhuYao.GUI_SHENG_BUO)
		{
			switch (ID)
			{
				case ZhuYao.GUI_YIN_GAN_QI:
					return new GYinGanQi((TYinGanQi) tileEntity);
				case ZhuYao.GUI_SHENG_BUO:
					return new GFrequency(entityPlayer.inventory.getCurrentItem());
			}
		}

		return null;
	}
}
