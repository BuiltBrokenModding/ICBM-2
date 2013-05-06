package icbm.wanyi;

import icbm.core.ShengYin;
import icbm.core.ZhuYaoBase;
import icbm.wanyi.b.TYinGanQi;
import icbm.wanyi.gui.GShengBuo;
import icbm.wanyi.gui.GYinGanQi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		super.preInit();
		MinecraftForge.EVENT_BUS.register(ShengYin.INSTANCE);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null || ID == ZhuYaoBase.GUI_SHENG_BUO)
		{
			switch (ID)
			{
				case ZhuYaoBase.GUI_YIN_GAN_QI:
					return new GYinGanQi((TYinGanQi) tileEntity);
				case ZhuYaoBase.GUI_SHENG_BUO:
					return new GShengBuo(entityPlayer.inventory.getCurrentItem());
			}
		}

		return null;
	}
}
