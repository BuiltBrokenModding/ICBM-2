package icbm;

import icbm.jiqi.TCiGuiPao;
import icbm.jiqi.TDianCiQi;
import icbm.jiqi.TFaSheDi;
import icbm.jiqi.TFaSheJia;
import icbm.jiqi.TFaSheShiMuo;
import icbm.jiqi.TLeiDa;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.rongqi.CCiGuiPao;
import icbm.rongqi.CFaShiDi;
import icbm.rongqi.CXiaoFaSheQi;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.CommonProxy;
import cpw.mods.fml.common.registry.GameRegistry;

public class ICBMCommonProxy extends CommonProxy
{
	//GUI IDs
	public static final int GUI_RAIL_GUN = 0;
	public static final int GUI_CRUISE_LAUNCHER = 1;
	public static final int GUI_LAUNCHER_SCREEN = 2;
	public static final int GUI_RADAR_STATION = 3;
	public static final int GUI_DETECTOR = 4;
	public static final int GUI_FREQUENCY = 5;
	public static final int GUI_EMP_TOWER = 6;
	public static final int GUI_LAUNCHER_BASE = 7;

	@Override
	public void preInit() { }
	
	@Override
	public void init()
	{
		GameRegistry.registerTileEntity(TCiGuiPao.class, "ICBMRailgun");
		GameRegistry.registerTileEntity(TXiaoFaSheQi.class, "ICBMCruiseLauncher");
		GameRegistry.registerTileEntity(TFaSheDi.class, "ICBMLauncherBase");
		GameRegistry.registerTileEntity(TFaSheShiMuo.class, "ICBMLauncherScreen");
		GameRegistry.registerTileEntity(TFaSheJia.class, "ICBMTileEntityLauncherFrame");
		GameRegistry.registerTileEntity(TLeiDa.class, "ICBMRadar");
		GameRegistry.registerTileEntity(TDianCiQi.class, "ICBMEMPTower");
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
			switch(ID)
			{
				case ICBMCommonProxy.GUI_RAIL_GUN: return new CCiGuiPao(player.inventory, (TCiGuiPao) tileEntity);
				case ICBMCommonProxy.GUI_CRUISE_LAUNCHER: return new CXiaoFaSheQi(player.inventory, (TXiaoFaSheQi) tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_BASE: return new CFaShiDi(player.inventory, (TFaSheDi) tileEntity);
			}
        }
		
		return null;
	}
		
	public World getWorld()
	{
		return null;
	}
}
