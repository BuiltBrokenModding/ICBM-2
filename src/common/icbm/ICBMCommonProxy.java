package icbm;

import icbm.machines.TileEntityCruiseLauncher;
import icbm.machines.TileEntityEMPTower;
import icbm.machines.TileEntityLauncherBase;
import icbm.machines.TileEntityLauncherFrame;
import icbm.machines.TileEntityLauncherScreen;
import icbm.machines.TileEntityRadarStation;
import icbm.machines.TileEntityRailgun;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.extend.CommonProxy;
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
		GameRegistry.registerTileEntity(TileEntityRailgun.class, "ICBMRailgun");
		GameRegistry.registerTileEntity(TileEntityCruiseLauncher.class, "ICBMCruiseLauncher");
		GameRegistry.registerTileEntity(TileEntityLauncherBase.class, "ICBMLauncherBase");
		GameRegistry.registerTileEntity(TileEntityLauncherScreen.class, "ICBMLauncherScreen");
		GameRegistry.registerTileEntity(TileEntityLauncherFrame.class, "ICBMTileEntityLauncherFrame");
		GameRegistry.registerTileEntity(TileEntityRadarStation.class, "ICBMRadar");
		GameRegistry.registerTileEntity(TileEntityEMPTower.class, "ICBMEMPTower");
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
				case ICBMCommonProxy.GUI_RAIL_GUN: return new ContainerRailgun(player.inventory, (TileEntityRailgun) tileEntity);
				case ICBMCommonProxy.GUI_CRUISE_LAUNCHER: return new ContainerCruiseLauncher(player.inventory, (TileEntityCruiseLauncher) tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_BASE: return new ContainerLauncherBase(player.inventory, (TileEntityLauncherBase) tileEntity);
			}
        }
		
		return null;
	}
		
	public World getWorld()
	{
		return null;
	}
}
