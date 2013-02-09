package icbm.explosion;

import icbm.core.ZhuYao;
import icbm.explosion.jiqi.TCiGuiPao;
import icbm.explosion.jiqi.TDianCiQi;
import icbm.explosion.jiqi.TFaSheDi;
import icbm.explosion.jiqi.TFaSheJia;
import icbm.explosion.jiqi.TFaSheShiMuo;
import icbm.explosion.jiqi.TLeiDaTai;
import icbm.explosion.jiqi.TXiaoFaSheQi;
import icbm.explosion.jiqi.TYinDaoQi;
import icbm.explosion.rongqi.CCiGuiPao;
import icbm.explosion.rongqi.CFaShiDi;
import icbm.explosion.rongqi.CXiaoFaSheQi;
import icbm.explosion.zhapin.TZhaDan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
	public void preInit()
	{
	}

	public void init()
	{
		GameRegistry.registerTileEntity(TCiGuiPao.class, "ICBMCiGuiPao");
		GameRegistry.registerTileEntity(TXiaoFaSheQi.class, "ICBMXiaoFaSheQi");
		GameRegistry.registerTileEntity(TFaSheDi.class, "ICBMFaSheDi");
		GameRegistry.registerTileEntity(TFaSheShiMuo.class, "ICBMFaSheShiMuo");
		GameRegistry.registerTileEntity(TFaSheJia.class, "ICBMFaSheJia");
		GameRegistry.registerTileEntity(TLeiDaTai.class, "ICBMLeiDaTai");
		GameRegistry.registerTileEntity(TDianCiQi.class, "ICBMDianCiQi");
		GameRegistry.registerTileEntity(TYinDaoQi.class, "ICBMYinDaoQi");
		GameRegistry.registerTileEntity(TZhaDan.class, "ICBMZhaDan");
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case ZhuYao.GUI_RAIL_GUN:
					return new CCiGuiPao(player.inventory, (TCiGuiPao) tileEntity);
				case ZhuYao.GUI_CRUISE_LAUNCHER:
					return new CXiaoFaSheQi(player.inventory, (TXiaoFaSheQi) tileEntity);
				case ZhuYao.GUI_LAUNCHER_BASE:
					return new CFaShiDi(player.inventory, (TFaSheDi) tileEntity);
			}
		}

		return null;
	}

	public String getMinecraftDir()
	{
		return "";
	}

	public boolean isGaoQing()
	{
		return false;
	}

	public void leiShe(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
	{

	}
}
