package icbm;

import icbm.daodan.EDaoDan;
import icbm.jiqi.ECiGuiPao;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.jiqi.TYinGanQi;
import icbm.jiqi.TDianCiQi;
import icbm.jiqi.TFaSheDi;
import icbm.jiqi.TFaSheJia;
import icbm.jiqi.TFaSheShiMuo;
import icbm.jiqi.TLeiDa;
import icbm.jiqi.TCiGuiPao;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.EZhaDan;
import icbm.zhapin.EZhaPin;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ICBMClientProxy extends ICBMCommonProxy
{	
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
		RenderingRegistry.registerEntityRenderingHandler(EZhaDan.class, new RenderExplosive());
        RenderingRegistry.registerEntityRenderingHandler(EDaoDan.class, new RenderMissile(0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EZhaPin.class, new RenderProceduralExplosion());
        RenderingRegistry.registerEntityRenderingHandler(EntityGravityBlock.class, new RenderGravityBlock());
        RenderingRegistry.registerEntityRenderingHandler(EGuang.class, new RenderLightBeam());
        RenderingRegistry.registerEntityRenderingHandler(ESuiPian.class, new RenderFragment());
        RenderingRegistry.registerEntityRenderingHandler(EShouLiuDan.class, new RenderGrenade());
        RenderingRegistry.registerEntityRenderingHandler(ECiGuiPao.class, new RenderInvisible());
        
        TextureFXManager.instance().addAnimation(new TGenZhongQiFX(FMLClientHandler.instance().getClient()));
        
        ClientRegistry.registerTileEntity(TCiGuiPao.class, "ICBMRailgun", new RenderRailgun());
        ClientRegistry.registerTileEntity(TXiaoFaSheQi.class, "ICBMCruiseLauncher", new RenderCruiseLauncher());
        ClientRegistry.registerTileEntity(TFaSheDi.class, "ICBMLauncherBase", new RenderLauncherBase());
        ClientRegistry.registerTileEntity(TFaSheShiMuo.class, "ICBMLauncherScreen", new RenderLauncherScreen());
        ClientRegistry.registerTileEntity(TFaSheJia.class, "ICBMTileEntityLauncherFrame", new RenderLauncherFrame());
        ClientRegistry.registerTileEntity(TLeiDa.class, "ICBMRadar", new RenderRadarStation());
        ClientRegistry.registerTileEntity(TDianCiQi.class, "ICBMEMPTower", new RenderEMPTower());
	
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
				
		if(tileEntity != null || ID == ICBMCommonProxy.GUI_FREQUENCY)
        {
			switch(ID)
			{
				case ICBMCommonProxy.GUI_RAIL_GUN: return new GCiGuiPao((TCiGuiPao)tileEntity, entityPlayer);
				case ICBMCommonProxy.GUI_CRUISE_LAUNCHER: return new GXiaoFaSheQi(entityPlayer.inventory, (TXiaoFaSheQi)tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_SCREEN: return new GFaSheShiMuo(((TFaSheShiMuo)tileEntity));
				case ICBMCommonProxy.GUI_RADAR_STATION: return new GuiLeiDaTai(((TLeiDa)tileEntity));
				case ICBMCommonProxy.GUI_DETECTOR: return new GuiYinGanQi((TYinGanQi) tileEntity);
				case ICBMCommonProxy.GUI_FREQUENCY: return new GFrequency(entityPlayer.inventory.getCurrentItem());
				case ICBMCommonProxy.GUI_EMP_TOWER: return new GEMPTower((TDianCiQi) tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_BASE: return new GFaSheDi(entityPlayer.inventory, (TFaSheDi) tileEntity);
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
