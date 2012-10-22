package icbm;

import icbm.api.ICBM;
import icbm.cart.EChe;
import icbm.daodan.EDaoDan;
import icbm.gui.GCiGuiPao;
import icbm.gui.GDianCiQi;
import icbm.gui.GFaSheDi;
import icbm.gui.GFaSheShiMuo;
import icbm.gui.GFrequency;
import icbm.gui.GLeiDaTai;
import icbm.gui.GXiaoFaSheQi;
import icbm.gui.GYinGanQi;
import icbm.jiqi.EFake;
import icbm.jiqi.TCiGuiPao;
import icbm.jiqi.TDianCiQi;
import icbm.jiqi.TFaSheDi;
import icbm.jiqi.TFaSheJia;
import icbm.jiqi.TFaSheShiMuo;
import icbm.jiqi.TLeiDaTai;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.jiqi.TYinGanQi;
import icbm.renders.RChe;
import icbm.renders.RCiGuiPao;
import icbm.renders.RDaoDan;
import icbm.renders.RDianCiQi;
import icbm.renders.RFaSheDi;
import icbm.renders.RFaSheJia;
import icbm.renders.RFake;
import icbm.renders.RFasheShiMuo;
import icbm.renders.RFeiBlock;
import icbm.renders.RGuangBang;
import icbm.renders.RShouLiuDan;
import icbm.renders.RShuiPian;
import icbm.renders.RXiaoFaSheQi;
import icbm.renders.RZhaDan;
import icbm.renders.RZhaPin;
import icbm.renders.RenderRadarStation;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.EZhaDan;
import icbm.zhapin.EZhaPin;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
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
		MinecraftForgeClient.preloadTexture(ICBM.TRACKER_TEXTURE_FILE);
		
		MinecraftForge.EVENT_BUS.register(ICBMSound.INSTANCE);
	}
	
	@Override
	public void init()
	{		
		RenderingRegistry.registerEntityRenderingHandler(EZhaDan.class, new RZhaDan());
        RenderingRegistry.registerEntityRenderingHandler(EDaoDan.class, new RDaoDan(0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EZhaPin.class, new RZhaPin());
        RenderingRegistry.registerEntityRenderingHandler(EFeiBlock.class, new RFeiBlock());
        RenderingRegistry.registerEntityRenderingHandler(EGuang.class, new RGuangBang());
        RenderingRegistry.registerEntityRenderingHandler(ESuiPian.class, new RShuiPian());
        RenderingRegistry.registerEntityRenderingHandler(EShouLiuDan.class, new RShouLiuDan());
        RenderingRegistry.registerEntityRenderingHandler(EFake.class, new RFake());
        RenderingRegistry.registerEntityRenderingHandler(EChe.class, new RChe());
                
        TextureFXManager.instance().addAnimation(new TGenZhongQiFX(FMLClientHandler.instance().getClient()));
        
        ClientRegistry.registerTileEntity(TCiGuiPao.class, "ICBMRailgun", new RCiGuiPao());
        ClientRegistry.registerTileEntity(TXiaoFaSheQi.class, "ICBMCruiseLauncher", new RXiaoFaSheQi());
        ClientRegistry.registerTileEntity(TFaSheDi.class, "ICBMLauncherBase", new RFaSheDi());
        ClientRegistry.registerTileEntity(TFaSheShiMuo.class, "ICBMLauncherScreen", new RFasheShiMuo());
        ClientRegistry.registerTileEntity(TFaSheJia.class, "ICBMTileEntityLauncherFrame", new RFaSheJia());
        ClientRegistry.registerTileEntity(TLeiDaTai.class, "ICBMRadar", new RenderRadarStation());
        ClientRegistry.registerTileEntity(TDianCiQi.class, "ICBMEMPTower", new RDianCiQi());
	
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
				case ICBMCommonProxy.GUI_RADAR_STATION: return new GLeiDaTai(((TLeiDaTai)tileEntity));
				case ICBMCommonProxy.GUI_DETECTOR: return new GYinGanQi((TYinGanQi) tileEntity);
				case ICBMCommonProxy.GUI_FREQUENCY: return new GFrequency(entityPlayer.inventory.getCurrentItem());
				case ICBMCommonProxy.GUI_EMP_TOWER: return new GDianCiQi((TDianCiQi) tileEntity);
				case ICBMCommonProxy.GUI_LAUNCHER_BASE: return new GFaSheDi(entityPlayer.inventory, (TFaSheDi) tileEntity);
			}
        }
		
		return null;
	}
}
