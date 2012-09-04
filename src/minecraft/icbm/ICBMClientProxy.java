package icbm;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ICBMClientProxy extends ICBMCommonProxy
{
	public static final Configuration CONFIGURATION = new Configuration(new File(Minecraft.getMinecraftDir(), "config/UniversalElectricity/ICBM.cfg"));
	
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(ICBM.ITEM_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ICBM.BLOCK_TEXTURE_FILE);
		
		MinecraftForge.EVENT_BUS.register(ICBMSound.INSTANCE);
	}
	
	@Override
	public void init()
	{		
		RenderingRegistry.registerEntityRenderingHandler(EntityExplosive.class, new RenderExplosive());
        RenderingRegistry.registerEntityRenderingHandler(EntityMissile.class, new RenderMissile(0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityProceduralExplosion.class, new RenderProceduralExplosion());
        RenderingRegistry.registerEntityRenderingHandler(EntityGravityBlock.class, new RenderGravityBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityLightBeam.class, new RenderLightBeam());
        RenderingRegistry.registerEntityRenderingHandler(EntityFragment.class, new RenderFragment());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderGrenade());
        RenderingRegistry.registerEntityRenderingHandler(EntityRailgun.class, new RenderInvisible());

        ClientRegistry.registerTileEntity(TileEntityRailgun.class, "ICBMRailgun", new RenderRailgun());
        ClientRegistry.registerTileEntity(TileEntityCruiseLauncher.class, "ICBMCruiseLauncher", new RenderCruiseLauncher());
        ClientRegistry.registerTileEntity(TileEntityLauncherBase.class, "ICBMLauncherBase", new RenderLauncherBase());
        ClientRegistry.registerTileEntity(TileEntityLauncherScreen.class, "ICBMLauncherScreen", new RenderLauncherScreen());
        ClientRegistry.registerTileEntity(TileEntityLauncherFrame.class, "ICBMTileEntityLauncherFrame", new RenderLauncherFrame());
        ClientRegistry.registerTileEntity(TileEntityRadarStation.class, "ICBMRadar", new RenderRadarStation());
        ClientRegistry.registerTileEntity(TileEntityEMPTower.class, "ICBMEMPTower", new RenderEMPTower());
	
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
				
		if(tileEntity != null || ID == ICBMCommonProxy.GUI_FREQUENCY)
        {
			switch(ID)
			{
				case ICBMCommonProxy.GUI_RAIL_GUN: return new GuiRailgun((TileEntityRailgun)tileEntity, entityPlayer);
				case ICBMCommonProxy.GUI_CRUISE_LAUNCHER: return new GuiCruiseLauncher(entityPlayer.inventory, (TileEntityCruiseLauncher)tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_SCREEN: return new GuiLauncherScreen(((TileEntityLauncherScreen)tileEntity));
				case ICBMCommonProxy.GUI_RADAR_STATION: return new GuiRadarStation(((TileEntityRadarStation)tileEntity));
				case ICBMCommonProxy.GUI_DETECTOR: return new GuiDetector((TileEntityDetector) tileEntity);
				case ICBMCommonProxy.GUI_FREQUENCY: return new GuiFrequency(entityPlayer.inventory.getCurrentItem());
				case ICBMCommonProxy.GUI_EMP_TOWER: return new GuiEMPTower((TileEntityEMPTower) tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_BASE: return new GuiLauncherBase(entityPlayer.inventory, (TileEntityLauncherBase) tileEntity);
			}
        }
		
		return null;
	}
	
	@Override
	public World getWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}
}
