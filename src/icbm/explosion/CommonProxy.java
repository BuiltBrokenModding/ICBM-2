package icbm.explosion;

import icbm.explosion.machines.TFaSheDi;
import icbm.explosion.machines.TFaSheJia;
import icbm.explosion.machines.TFaSheShiMuo;
import icbm.explosion.machines.TileEntityRadarStation;
import icbm.explosion.machines.TileEntityMissileCoordinator;
import icbm.explosion.machines.TileEntityEmpTower;
import icbm.explosion.machines.TileEntityCruiseLauncher;
import icbm.explosion.rongqi.CFaShiDi;
import icbm.explosion.rongqi.CXiaoFaSheQi;
import icbm.explosion.rongqi.CYinDaoQi;
import icbm.explosion.zhapin.TileEntityExplosive;
import icbm.explosion.zhapin.daodan.EDaoDan;

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
		GameRegistry.registerTileEntity(TileEntityCruiseLauncher.class, "ICBMXiaoFaSheQi");
		GameRegistry.registerTileEntity(TFaSheDi.class, "ICBMFaSheDi");
		GameRegistry.registerTileEntity(TFaSheShiMuo.class, "ICBMFaSheShiMuo");
		GameRegistry.registerTileEntity(TFaSheJia.class, "ICBMFaSheJia");
		GameRegistry.registerTileEntity(TileEntityRadarStation.class, "ICBMLeiDaTai");
		GameRegistry.registerTileEntity(TileEntityEmpTower.class, "ICBMDianCiQi");
		GameRegistry.registerTileEntity(TileEntityMissileCoordinator.class, "ICBMYinDaoQi");
		GameRegistry.registerTileEntity(TileEntityExplosive.class, "ICBMZhaDan");
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

		if (tileEntity instanceof TileEntityCruiseLauncher)
		{
			return new CXiaoFaSheQi(player.inventory, (TileEntityCruiseLauncher) tileEntity);
		}
		else if (tileEntity instanceof TFaSheDi)
		{
			return new CFaShiDi(player.inventory, (TFaSheDi) tileEntity);
		}
		else if (tileEntity instanceof TileEntityMissileCoordinator)
		{
			return new CYinDaoQi(player.inventory, (TileEntityMissileCoordinator) tileEntity);
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

	public void spawnShock(World world, Vector3 position, Vector3 target)
	{

	}

	public void spawnShock(World world, Vector3 startVec, Vector3 targetVec, int duration)
	{
		// TODO Auto-generated method stub

	}

}
