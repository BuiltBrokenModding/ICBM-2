package icbm.renders;

import icbm.ZhuYao;
import icbm.daodan.EDaoDan;
import icbm.daodan.EDaoDan.XingShi;
import icbm.models.MMBingDan;
import icbm.models.MMDianCi;
import icbm.models.MMDuQi;
import icbm.models.MMFanDan;
import icbm.models.MMFanWuSu;
import icbm.models.MMFenZiDan;
import icbm.models.MMGanRanDu;
import icbm.models.MMHongSu;
import icbm.models.MMHuanYuan;
import icbm.models.MMHuo;
import icbm.models.MMLa;
import icbm.models.MMLiZi;
import icbm.models.MMPiaoFu;
import icbm.models.MMQunDan;
import icbm.models.MMShengBuo;
import icbm.models.MMTaiYang;
import icbm.models.MMTuPuo;
import icbm.models.MMTui;
import icbm.models.MMWan;
import icbm.models.MMXiaoQunDan;
import icbm.models.MMYaSuo;
import icbm.models.MMYuanZi;
import icbm.models.MMZhen;
import icbm.models.MMZhuiZhong;
import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.Render;

import org.lwjgl.opengl.GL11;

public class RDaoDan extends Render
{
	private class RData
	{
		public final ModelBase model;
		public final String texture;

		public RData(ModelBase model, String texture)
		{
			this.model = model;
			this.texture = texture;
		}
	}

	private final RData[] models = { new RData(new MMYaSuo(), "MissileConventional"), new RData(new MMXiaoQunDan(), "MissileShrapnel"), new RData(new MMHuo(), "MissileIncendiary"), new RData(new MMDuQi(), "MissileChemical"), new RData(new MMZhen(), "MissileAnvil"), new RData(new MMTui(), "MissileRepulsive"), new RData(new MMLa(), "MissileAttractive")

	, new RData(new MMQunDan(), "MissileFragmentation"), new RData(new MMGanRanDu(), "MissileContagious"), new RData(new MMShengBuo(), "MissileSonic"), new RData(new MMTuPuo(), "MissileBreaching"), new RData(new MMHuanYuan(), "MissileRejuvenation"), new RData(new MMLiZi(), "MissileIon"),

	new RData(new MMYuanZi(), "MissileNuclear"), new RData(new MMDianCi(), "MissileEMP"), new RData(new MMTaiYang(), "MissileConflagration"), new RData(new MMBingDan(), "MissileEndothermic"), new RData(new MMPiaoFu(), "MissileAntiGravity"), new RData(new MMWan(), "MissileEnder"),

	new RData(new MMFanWuSu(), "MissileAntimatter"), new RData(new MMHongSu(), "MissileRedMatter") };

	private final RData[] specialModels = { new RData(new MMFanDan(), "MissileAntiBallistic"), new RData(new MMFenZiDan(), "MissileCluster"), new RData(new MMFenZiDan(), "MissileCluster"), new RData(new MMZhuiZhong(), "MissileHoming") };

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
			this.loadTexture(ZhuYao.TEXTURE_FILE_PATH + this.specialModels[entityMissile.haoMa - 101].texture + ".png");
			this.specialModels[entityMissile.haoMa - 101].model.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}
		else
		{
			this.loadTexture(ZhuYao.TEXTURE_FILE_PATH + this.models[entityMissile.haoMa].texture + ".png");
			this.models[entityMissile.haoMa].model.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}

		GL11.glPopMatrix();
	}
}