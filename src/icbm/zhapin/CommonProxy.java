package icbm.zhapin;

import icbm.zhapin.jiqi.TDianCiQi;
import icbm.zhapin.jiqi.TFaSheDi;
import icbm.zhapin.jiqi.TFaSheJia;
import icbm.zhapin.jiqi.TFaSheShiMuo;
import icbm.zhapin.jiqi.TLeiDaTai;
import icbm.zhapin.jiqi.TXiaoFaSheQi;
import icbm.zhapin.jiqi.TYinDaoQi;
import icbm.zhapin.rongqi.CFaShiDi;
import icbm.zhapin.rongqi.CXiaoFaSheQi;
import icbm.zhapin.zhapin.TZhaDan;
import icbm.zhapin.zhapin.daodan.EDaoDan;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * ICBM Explosion Module Common Proxy
 * 
 * @author Calclavia
 * 
 */
public class CommonProxy implements IGuiHandler
{
	public void preInit()
	{
	}

	public void init()
	{
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

		if (tileEntity instanceof TXiaoFaSheQi)
		{
			return new CXiaoFaSheQi(player.inventory, (TXiaoFaSheQi) tileEntity);
		}
		else if (tileEntity instanceof TFaSheDi)
		{
			return new CFaShiDi(player.inventory, (TFaSheDi) tileEntity);
		}

		return null;
	}

	public boolean isGaoQing()
	{
		return false;
	}

	public void spawnParticle(String name, World world, Vector3 position, float scale, double distance)
	{
		this.spawnParticle(name, world, position, 0, 0, 0, scale, distance);
	}

	public void spawnParticle(String name, World world, Vector3 position, double motionX, double motionY, double motionZ, float scale, double distance)
	{
		this.spawnParticle(name, world, position, motionX, motionY, motionZ, 1, 1, 1, scale, distance);
	}

	public void spawnParticle(String name, World world, Vector3 position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance)
	{

	}

	public IUpdatePlayerListBox getDaoDanShengYin(EDaoDan eDaoDan)
	{
		return null;
	}

	public int getParticleSetting()
	{
		return -1;
	}

	public List<Entity> getEntityFXs()
	{
		return null;
	}

}
