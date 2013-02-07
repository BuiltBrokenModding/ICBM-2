package icbm.client.render;

import icbm.api.ICBM;
import icbm.client.models.ICBMModelBase;
import icbm.client.models.MMBingDan;
import icbm.client.models.MMChaoShengBuo;
import icbm.client.models.MMDianCi;
import icbm.client.models.MMDuQi;
import icbm.client.models.MMFanDan;
import icbm.client.models.MMFanWuSu;
import icbm.client.models.MMFenZiDan;
import icbm.client.models.MMGanRanDu;
import icbm.client.models.MMHongSu;
import icbm.client.models.MMHuanYuan;
import icbm.client.models.MMHuo;
import icbm.client.models.MMLa;
import icbm.client.models.MMLiZi;
import icbm.client.models.MMPiaoFu;
import icbm.client.models.MMQunDan;
import icbm.client.models.MMShengBuo;
import icbm.client.models.MMTaiYang;
import icbm.client.models.MMTuPuo;
import icbm.client.models.MMTui;
import icbm.client.models.MMWan;
import icbm.client.models.MMXiaoQunDan;
import icbm.client.models.MMYaSuo;
import icbm.client.models.MMYuanZi;
import icbm.client.models.MMZhen;
import icbm.client.models.MMZhuiZhong;
import icbm.common.daodan.EDaoDan;
import icbm.common.daodan.EDaoDan.XingShi;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RDaoDan extends Render
{
	public static class RData
	{
		public final ICBMModelBase model;
		public final String texture;

		public RData(ICBMModelBase model, String texture)
		{
			this.model = model;
			this.texture = texture;
		}
	}

	public static final RData[] models = { new RData(new MMYaSuo(), "MissileConventional"), new RData(new MMXiaoQunDan(), "MissileShrapnel"), new RData(new MMHuo(), "MissileIncendiary"), new RData(new MMDuQi(), "MissileChemical"), new RData(new MMZhen(), "MissileAnvil"), new RData(new MMTui(), "MissileRepulsive"), new RData(new MMLa(), "MissileAttractive")

	, new RData(new MMQunDan(), "MissileFragmentation"), new RData(new MMGanRanDu(), "MissileContagious"), new RData(new MMShengBuo(), "MissileSonic"), new RData(new MMTuPuo(), "MissileBreaching"), new RData(new MMHuanYuan(), "MissileRejuvenation"), new RData(new MMLiZi(), "MissileIon"),

	new RData(new MMYuanZi(), "MissileNuclear"), new RData(new MMDianCi(), "MissileEMP"), new RData(new MMTaiYang(), "MissileConflagration"), new RData(new MMBingDan(), "MissileEndothermic"), new RData(new MMPiaoFu(), "MissileAntiGravity"), new RData(new MMWan(), "MissileEnder"), new RData(new MMChaoShengBuo(), "MissileHypersonic"),

	new RData(new MMFanWuSu(), "MissileAntimatter"), new RData(new MMHongSu(), "MissileRedMatter") };

	public static final RData[] specialModels = { new RData(new MMFanDan(), "MissileAntiBallistic"), new RData(new MMFenZiDan(), "MissileCluster"), new RData(new MMFenZiDan(), "MissileCluster"), new RData(new MMZhuiZhong(), "MissileHoming") };

	public RDaoDan(float f)
	{
		this.shadowSize = f;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1)
	{
		EDaoDan entityMissile = (EDaoDan) entity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * f1 + 90.0F, 0.0F, 0.0F, 1.0F);

		if (entityMissile.xingShi == XingShi.XIAO_DAN)
		{
			GL11.glScalef(0.5f, 0.5f, 0.5f);
		}

		if (entityMissile.haoMa > 100)
		{
			this.loadTexture(ICBM.TEXTURE_FILE_PATH + this.specialModels[entityMissile.haoMa - 101].texture + ".png");
			this.specialModels[entityMissile.haoMa - 101].model.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}
		else
		{
			this.loadTexture(ICBM.TEXTURE_FILE_PATH + this.models[entityMissile.haoMa].texture + ".png");
			this.models[entityMissile.haoMa].model.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}

		GL11.glPopMatrix();
	}
}