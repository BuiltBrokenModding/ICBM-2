package icbm.client.render;

import icbm.api.ICBM;
import icbm.core.muoxing.ICBMModelBase;
import icbm.explosion.daodan.EDaoDan;
import icbm.explosion.daodan.EDaoDan.XingShi;
import icbm.explosion.muoxing.daodan.MMBingDan;
import icbm.explosion.muoxing.daodan.MMChaoShengBuo;
import icbm.explosion.muoxing.daodan.MMDianCi;
import icbm.explosion.muoxing.daodan.MMDuQi;
import icbm.explosion.muoxing.daodan.MMFanDan;
import icbm.explosion.muoxing.daodan.MMFanWuSu;
import icbm.explosion.muoxing.daodan.MMFenZiDan;
import icbm.explosion.muoxing.daodan.MMGanRanDu;
import icbm.explosion.muoxing.daodan.MMHongSu;
import icbm.explosion.muoxing.daodan.MMHuanYuan;
import icbm.explosion.muoxing.daodan.MMHuo;
import icbm.explosion.muoxing.daodan.MMLa;
import icbm.explosion.muoxing.daodan.MMLiZi;
import icbm.explosion.muoxing.daodan.MMPiaoFu;
import icbm.explosion.muoxing.daodan.MMQunDan;
import icbm.explosion.muoxing.daodan.MMShengBuo;
import icbm.explosion.muoxing.daodan.MMTaiYang;
import icbm.explosion.muoxing.daodan.MMTuPuo;
import icbm.explosion.muoxing.daodan.MMTui;
import icbm.explosion.muoxing.daodan.MMWan;
import icbm.explosion.muoxing.daodan.MMXiaoQunDan;
import icbm.explosion.muoxing.daodan.MMYaSuo;
import icbm.explosion.muoxing.daodan.MMYuanZi;
import icbm.explosion.muoxing.daodan.MMZhen;
import icbm.explosion.muoxing.daodan.MMZhuiZhong;
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