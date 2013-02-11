package icbm.explosion;

import icbm.core.ShengYin;
import icbm.core.ZhuYao;
import icbm.explosion.cart.EChe;
import icbm.explosion.daodan.EDaoDan;
import icbm.explosion.fx.FXLeiShe;
import icbm.explosion.gui.GCiGuiPao;
import icbm.explosion.gui.GDianCiQi;
import icbm.explosion.gui.GFaSheDi;
import icbm.explosion.gui.GFaSheShiMuo;
import icbm.explosion.gui.GLeiDaTai;
import icbm.explosion.gui.GXiaoFaSheQi;
import icbm.explosion.jiqi.EFake;
import icbm.explosion.jiqi.TCiGuiPao;
import icbm.explosion.jiqi.TDianCiQi;
import icbm.explosion.jiqi.TFaSheDi;
import icbm.explosion.jiqi.TFaSheJia;
import icbm.explosion.jiqi.TFaSheShiMuo;
import icbm.explosion.jiqi.TLeiDaTai;
import icbm.explosion.jiqi.TXiaoFaSheQi;
import icbm.explosion.jiqi.TYinDaoQi;
import icbm.explosion.render.RBYinXing;
import icbm.explosion.render.RChe;
import icbm.explosion.render.RCiGuiPao;
import icbm.explosion.render.RDaoDan;
import icbm.explosion.render.RDianCiQi;
import icbm.explosion.render.REZhaDan;
import icbm.explosion.render.RFaSheDi;
import icbm.explosion.render.RFaSheJia;
import icbm.explosion.render.RFaSheShiMuo;
import icbm.explosion.render.RFake;
import icbm.explosion.render.RFeiBlock;
import icbm.explosion.render.RGuangBang;
import icbm.explosion.render.RHJiQi;
import icbm.explosion.render.RHZhaPin;
import icbm.explosion.render.RItDaoDan;
import icbm.explosion.render.RItFaSheQi;
import icbm.explosion.render.RLeiDaTai;
import icbm.explosion.render.RShouLiuDan;
import icbm.explosion.render.RSuiPian;
import icbm.explosion.render.RXiaoFaSheQi;
import icbm.explosion.render.RYinDaoQi;
import icbm.explosion.render.RZhaDan;
import icbm.explosion.render.RZhaPin;
import icbm.explosion.zhapin.EShouLiuDan;
import icbm.explosion.zhapin.EZhaDan;
import icbm.explosion.zhapin.EZhaPin;
import icbm.explosion.zhapin.TZhaDan;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
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

		MinecraftForgeClient.registerItemRenderer(ZhuYaoExplosion.itFaSheQi.itemID, new RItFaSheQi());
		MinecraftForgeClient.registerItemRenderer(ZhuYaoExplosion.itDaoDan.itemID, new RItDaoDan());
		MinecraftForgeClient.registerItemRenderer(ZhuYaoExplosion.itTeBieDaoDan.itemID, new RItDaoDan());

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

		ClientRegistry.bindTileEntitySpecialRenderer(TCiGuiPao.class, new RCiGuiPao());
		ClientRegistry.bindTileEntitySpecialRenderer(TXiaoFaSheQi.class, new RXiaoFaSheQi());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheDi.class, new RFaSheDi());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheShiMuo.class, new RFaSheShiMuo());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheJia.class, new RFaSheJia());
		ClientRegistry.bindTileEntitySpecialRenderer(TLeiDaTai.class, new RLeiDaTai());
		ClientRegistry.bindTileEntitySpecialRenderer(TDianCiQi.class, new RDianCiQi());
		ClientRegistry.bindTileEntitySpecialRenderer(TZhaDan.class, new RZhaDan());
		ClientRegistry.bindTileEntitySpecialRenderer(TYinDaoQi.class, new RYinDaoQi());

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null || ID == ZhuYao.GUI_FREQUENCY)
		{
			switch (ID)
			{
				case ZhuYao.GUI_RAIL_GUN:
					return new GCiGuiPao((TCiGuiPao) tileEntity, entityPlayer);
				case ZhuYao.GUI_CRUISE_LAUNCHER:
					return new GXiaoFaSheQi(entityPlayer.inventory, (TXiaoFaSheQi) tileEntity);
				case ZhuYao.GUI_LAUNCHER_SCREEN:
					return new GFaSheShiMuo(((TFaSheShiMuo) tileEntity));
				case ZhuYao.GUI_RADAR_STATION:
					return new GLeiDaTai(((TLeiDaTai) tileEntity));
				case ZhuYao.GUI_EMP_TOWER:
					return new GDianCiQi((TDianCiQi) tileEntity);
				case ZhuYao.GUI_LAUNCHER_BASE:
					return new GFaSheDi(entityPlayer.inventory, (TFaSheDi) tileEntity);
			}
		}

		return null;
	}

	@Override
	public boolean isGaoQing()
	{
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
	}

	@Override
	public void leiShe(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
	{
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXLeiShe(world, position, target, red, green, blue, age));
	}
}
