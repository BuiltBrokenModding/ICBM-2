package icbm.wanyi;

import icbm.core.ShengYin;
import icbm.core.ZhuYao;
import icbm.wanyi.gui.GFrequency;
import icbm.wanyi.gui.GYinGanQi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(ZhuYao.ITEM_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ZhuYao.BLOCK_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ZhuYao.TRACKER_TEXTURE_FILE);

		MinecraftForge.EVENT_BUS.register(ShengYin.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();
		TextureFXManager.instance().addAnimation(new FXGenZhongQi(FMLClientHandler.instance().getClient()));
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
