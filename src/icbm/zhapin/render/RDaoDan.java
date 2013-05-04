package icbm.zhapin.render;

import icbm.core.ZhuYaoBase;
import icbm.core.di.MICBM;
import icbm.zhapin.daodan.DaoDan;
import icbm.zhapin.daodan.EDaoDan;
import icbm.zhapin.daodan.EDaoDan.XingShi;
import icbm.zhapin.muoxing.daodan.MMBingDan;
import icbm.zhapin.muoxing.daodan.MMChaoShengBuo;
import icbm.zhapin.muoxing.daodan.MMDianCi;
import icbm.zhapin.muoxing.daodan.MMDuQi;
import icbm.zhapin.muoxing.daodan.MMFanDan;
import icbm.zhapin.muoxing.daodan.MMFanWuSu;
import icbm.zhapin.muoxing.daodan.MMFenZiDan;
import icbm.zhapin.muoxing.daodan.MMGanRanDu;
import icbm.zhapin.muoxing.daodan.MMHongSu;
import icbm.zhapin.muoxing.daodan.MMHuanYuan;
import icbm.zhapin.muoxing.daodan.MMHuo;
import icbm.zhapin.muoxing.daodan.MMLa;
import icbm.zhapin.muoxing.daodan.MMLiZi;
import icbm.zhapin.muoxing.daodan.MMPiaoFu;
import icbm.zhapin.muoxing.daodan.MMQunDan;
import icbm.zhapin.muoxing.daodan.MMShengBuo;
import icbm.zhapin.muoxing.daodan.MMTaiYang;
import icbm.zhapin.muoxing.daodan.MMTuPuo;
import icbm.zhapin.muoxing.daodan.MMTui;
import icbm.zhapin.muoxing.daodan.MMWan;
import icbm.zhapin.muoxing.daodan.MMXiaoQunDan;
import icbm.zhapin.muoxing.daodan.MMYaSuo;
import icbm.zhapin.muoxing.daodan.MMYuanZi;
import icbm.zhapin.muoxing.daodan.MMZhen;
import icbm.zhapin.muoxing.daodan.MMZhuiZhong;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RDaoDan extends Render
{
	public static final MICBM[] MODELS = { new MMYaSuo(), new MMXiaoQunDan(), new MMHuo(), new MMYaSuo(), new MMDuQi(), new MMZhen(), new MMTui(), new MMLa()

	, new MMQunDan(), new MMGanRanDu(), new MMShengBuo(), new MMTuPuo(), new MMHuanYuan(), new MMLiZi(),

	new MMYuanZi(), new MMDianCi(), new MMTaiYang(), new MMBingDan(), new MMPiaoFu(), new MMWan(), new MMChaoShengBuo(),

	new MMFanWuSu(), new MMHongSu() };

	public static MICBM[] SPECIAL_MODELS = { new MMYaSuo(), new MMZhuiZhong(), new MMFanDan(), new MMFenZiDan(), new MMFenZiDan() };

	public RDaoDan(float f)
	{
		this.shadowSize = f;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1)
	{
		EDaoDan entityMissile = (EDaoDan) entity;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * f1 + 90.0F, 0.0F, 0.0F, 1.0F);

		if (entityMissile.xingShi == XingShi.XIAO_DAN)
		{
			GL11.glScalef(0.5f, 0.5f, 0.5f);
		}

		if (entityMissile.haoMa >= 100)
		{
			this.loadTexture(ZhuYaoBase.MODEL_PATH + "missile_" + DaoDan.list[entityMissile.haoMa].getUnlocalizedName() + ".png");
			RDaoDan.SPECIAL_MODELS[entityMissile.haoMa - 100].render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}
		else
		{
			this.loadTexture(ZhuYaoBase.MODEL_PATH + "missile_" + ZhaPin.list[entityMissile.haoMa].getUnlocalizedName() + ".png");
			RDaoDan.MODELS[entityMissile.haoMa].render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}

		GL11.glPopMatrix();
	}
}