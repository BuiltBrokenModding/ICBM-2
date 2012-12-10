package icbm;

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
import icbm.renders.RBZhaDan;
import icbm.renders.RChe;
import icbm.renders.RCiGuiPao;
import icbm.renders.RDaoDan;
import icbm.renders.RDianCiQi;
import icbm.renders.REZhaDan;
import icbm.renders.RFaSheDi;
import icbm.renders.RFaSheJia;
import icbm.renders.RFake;
import icbm.renders.RFaSheShiMuo;
import icbm.renders.RFeiBlock;
import icbm.renders.RGuangBang;
import icbm.renders.RHJiQi;
import icbm.renders.RHYinXing;
import icbm.renders.RHZhaPin;
import icbm.renders.RShouLiuDan;
import icbm.renders.RSuiPian;
import icbm.renders.RXiaoFaSheQi;
import icbm.renders.RZhaPin;
import icbm.renders.RenderRadarStation;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.EZhaDan;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.TZhaDan;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ICBMClient extends ICBMCommon
{
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(ZhuYao.ITEM_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ZhuYao.BLOCK_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ZhuYao.TRACKER_TEXTURE_FILE);

		MinecraftForge.EVENT_BUS.register(ICBMSound.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();

		RenderingRegistry.registerBlockHandler(new RHZhaPin());
		RenderingRegistry.registerBlockHandler(new RHYinXing());
		RenderingRegistry.registerBlockHandler(new RHJiQi());

		RenderingRegistry.registerEntityRenderingHandler(EZhaDan.class, new REZhaDan());
		RenderingRegistry.registerEntityRenderingHandler(EDaoDan.class, new RDaoDan(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EZhaPin.class, new RZhaPin());
		RenderingRegistry.registerEntityRenderingHandler(EFeiBlock.class, new RFeiBlock());
		RenderingRegistry.registerEntityRenderingHandler(EGuang.class, new RGuangBang());
		RenderingRegistry.registerEntityRenderingHandler(ESuiPian.class, new RSuiPian());
		RenderingRegistry.registerEntityRenderingHandler(EShouLiuDan.class, new RShouLiuDan());
		RenderingRegistry.registerEntityRenderingHandler(EFake.class, new RFake());
		RenderingRegistry.registerEntityRenderingHandler(EChe.class, new RChe());

		TextureFXManager.instance().addAnimation(new TGenZhongQiFX(FMLClientHandler.instance().getClient()));

		ClientRegistry.bindTileEntitySpecialRenderer(TCiGuiPao.class, new RCiGuiPao());
		ClientRegistry.bindTileEntitySpecialRenderer(TXiaoFaSheQi.class, new RXiaoFaSheQi());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheDi.class, new RFaSheDi());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheShiMuo.class, new RFaSheShiMuo());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheJia.class, new RFaSheJia());
		ClientRegistry.bindTileEntitySpecialRenderer(TLeiDaTai.class, new RenderRadarStation());
		ClientRegistry.bindTileEntitySpecialRenderer(TDianCiQi.class, new RDianCiQi());
		ClientRegistry.bindTileEntitySpecialRenderer(TZhaDan.class, new RBZhaDan());

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null || ID == ICBMCommon.GUI_FREQUENCY)
		{
			switch (ID)
			{
				case ICBMCommon.GUI_RAIL_GUN:
					return new GCiGuiPao((TCiGuiPao) tileEntity, entityPlayer);
				case ICBMCommon.GUI_CRUISE_LAUNCHER:
					return new GXiaoFaSheQi(entityPlayer.inventory, (TXiaoFaSheQi) tileEntity);
				case ICBMCommon.GUI_LAUNCHER_SCREEN:
					return new GFaSheShiMuo(((TFaSheShiMuo) tileEntity));
				case ICBMCommon.GUI_RADAR_STATION:
					return new GLeiDaTai(((TLeiDaTai) tileEntity));
				case ICBMCommon.GUI_DETECTOR:
					return new GYinGanQi((TYinGanQi) tileEntity);
				case ICBMCommon.GUI_FREQUENCY:
					return new GFrequency(entityPlayer.inventory.getCurrentItem());
				case ICBMCommon.GUI_EMP_TOWER:
					return new GDianCiQi((TDianCiQi) tileEntity);
				case ICBMCommon.GUI_LAUNCHER_BASE:
					return new GFaSheDi(entityPlayer.inventory, (TFaSheDi) tileEntity);
			}
		}

		return null;
	}

	@Override
	public String getMinecraftDir()
	{
		return Minecraft.getMinecraftDir().toString();
	}

	public boolean isGaoQing()
	{
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
	}
}
