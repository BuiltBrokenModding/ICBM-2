package icbm.client;

import icbm.client.render.RBYinXing;
import icbm.client.render.RChe;
import icbm.client.render.RCiGuiPao;
import icbm.client.render.RDaoDan;
import icbm.client.render.RDianCiQi;
import icbm.client.render.REZhaDan;
import icbm.client.render.RFaSheDi;
import icbm.client.render.RFaSheJia;
import icbm.client.render.RFaSheShiMuo;
import icbm.client.render.RFake;
import icbm.client.render.RFeiBlock;
import icbm.client.render.RGuangBang;
import icbm.client.render.RHJiQi;
import icbm.client.render.RHZhaPin;
import icbm.client.render.RItDaoDan;
import icbm.client.render.RItFaSheQi;
import icbm.client.render.RLeiDaTai;
import icbm.client.render.RShouLiuDan;
import icbm.client.render.RSuiPian;
import icbm.client.render.RXiaoFaSheQi;
import icbm.client.render.RZhaDan;
import icbm.client.render.RZhaPin;
import icbm.common.CommonProxy;
import icbm.common.EFeiBlock;
import icbm.common.EGuang;
import icbm.common.ESuiPian;
import icbm.common.ShengYin;
import icbm.common.TGenZhongQiFX;
import icbm.common.ZhuYao;
import icbm.common.cart.EChe;
import icbm.common.daodan.EDaoDan;
import icbm.common.gui.GCiGuiPao;
import icbm.common.gui.GDianCiQi;
import icbm.common.gui.GFaSheDi;
import icbm.common.gui.GFaSheShiMuo;
import icbm.common.gui.GFrequency;
import icbm.common.gui.GLeiDaTai;
import icbm.common.gui.GXiaoFaSheQi;
import icbm.common.gui.GYinGanQi;
import icbm.common.jiqi.EFake;
import icbm.common.jiqi.TCiGuiPao;
import icbm.common.jiqi.TDianCiQi;
import icbm.common.jiqi.TFaSheDi;
import icbm.common.jiqi.TFaSheJia;
import icbm.common.jiqi.TFaSheShiMuo;
import icbm.common.jiqi.TLeiDaTai;
import icbm.common.jiqi.TXiaoFaSheQi;
import icbm.common.jiqi.TYinGanQi;
import icbm.common.zhapin.EShouLiuDan;
import icbm.common.zhapin.EZhaDan;
import icbm.common.zhapin.EZhaPin;
import icbm.common.zhapin.TZhaDan;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		MinecraftForgeClient.preloadTexture(ZhuYao.ITEM_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ZhuYao.BLOCK_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(ZhuYao.TRACKER_TEXTURE_FILE);

		MinecraftForge.EVENT_BUS.register(ShengYin.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();

		MinecraftForgeClient.registerItemRenderer(ZhuYao.itFaSheQi.shiftedIndex, new RItFaSheQi());
		MinecraftForgeClient.registerItemRenderer(ZhuYao.itDaoDan.shiftedIndex, new RItDaoDan());
		MinecraftForgeClient.registerItemRenderer(ZhuYao.itTeBieDaoDan.shiftedIndex, new RItDaoDan());

		RenderingRegistry.registerBlockHandler(new RHZhaPin());
		RenderingRegistry.registerBlockHandler(new RBYinXing());
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
		ClientRegistry.bindTileEntitySpecialRenderer(TLeiDaTai.class, new RLeiDaTai());
		ClientRegistry.bindTileEntitySpecialRenderer(TDianCiQi.class, new RDianCiQi());
		ClientRegistry.bindTileEntitySpecialRenderer(TZhaDan.class, new RZhaDan());

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null || ID == CommonProxy.GUI_FREQUENCY)
		{
			switch (ID)
			{
				case CommonProxy.GUI_RAIL_GUN:
					return new GCiGuiPao((TCiGuiPao) tileEntity, entityPlayer);
				case CommonProxy.GUI_CRUISE_LAUNCHER:
					return new GXiaoFaSheQi(entityPlayer.inventory, (TXiaoFaSheQi) tileEntity);
				case CommonProxy.GUI_LAUNCHER_SCREEN:
					return new GFaSheShiMuo(((TFaSheShiMuo) tileEntity));
				case CommonProxy.GUI_RADAR_STATION:
					return new GLeiDaTai(((TLeiDaTai) tileEntity));
				case CommonProxy.GUI_DETECTOR:
					return new GYinGanQi((TYinGanQi) tileEntity);
				case CommonProxy.GUI_FREQUENCY:
					return new GFrequency(entityPlayer.inventory.getCurrentItem());
				case CommonProxy.GUI_EMP_TOWER:
					return new GDianCiQi((TDianCiQi) tileEntity);
				case CommonProxy.GUI_LAUNCHER_BASE:
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
