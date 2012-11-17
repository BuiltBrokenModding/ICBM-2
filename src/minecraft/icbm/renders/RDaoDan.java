/*package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class ICBMRenderMissile extends RenderLiving
{
    public ICBMRenderMissile(ModelBase modelbase, float f)
    {
        super(modelbase, f);
    }

    public void func_177_a(ICBMEntityMissile ICBMEntityMissile, double d, double d1, double d2,
            float f, float f1)
    {
        super.doRenderLiving(ICBMEntityMissile, d, d1, d2, f, f1);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2,
            float f, float f1)
    {
        func_177_a((ICBMEntityMissile)entityliving, d, d1, d2, f, f1);
    }

    public void doRender(Entity entity, double d, double d1, double d2,
            float f, float f1)
    {
        func_177_a((ICBMEntityMissile)entity, d, d1, d2, f, f1);
    }
}
 */
package icbm.renders;

import icbm.api.ICBM;
import icbm.daodan.EDaoDan;
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
import icbm.models.MMLiZi;
import icbm.models.MMPiaoFu;
import icbm.models.MMQunDan;
import icbm.models.MMShengBuo;
import icbm.models.MMTaiYang;
import icbm.models.MMTuPuo;
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
	private final ModelBase[] models =
	{ new MMYaSuo(), new MMXiaoQunDan(), new MMHuo(), new MMDuQi(), new MMZhen(), new MMZhen(), new MMZhen(),

	new MMQunDan(), new MMGanRanDu(), new MMShengBuo(), new MMTuPuo(), new MMHuanYuan(), new MMLiZi(),

	new MMYuanZi(), new MMDianCi(), new MMTaiYang(), new MMBingDan(), new MMPiaoFu(), new MMWan(),

	new MMFanWuSu(), new MMHongSu() };

	private final MMFanDan mFanDan = new MMFanDan();
	private final MMFenZiDan mFenZiDan = new MMFenZiDan();
	private final MMZhuiZhong mZhuiZhong = new MMZhuiZhong();

	public RDaoDan(float f)
	{
		this.shadowSize = f;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1)
	{
		EDaoDan entityMissile = (EDaoDan) entity;

		// Use the correct model & texture for the
		// specified missile metadata
		switch (entityMissile.explosiveID)
		{
			default:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileConventional.png");
				break;
			case 1:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileShrapnel.png");
				break;
			case 2:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileIncendiary.png");
				break;
			case 3:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileChemical.png");
				break;
			case 4:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileAnvil.png");
				break;
			case 5:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileAnvil.png");
				break;
			case 6:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileAnvil.png");
				break;

			case 7:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileFragmentation.png");
				break;
			case 8:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileContagious.png");
				break;
			case 9:
				loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileSonic.png");
				break;
			case 10:
				loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileBreaching.png");
				break;
			case 11:
				loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileRejuvenation.png");
				break;
			case 12:
				loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileIon.png");
				break;

			case 13:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileNuclear.png");
				break;
			case 14:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileEMP.png");
				break;
			case 15:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileConflagration.png");
				break;
			case 16:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileEndothermic.png");
				break;
			case 17:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileAntiGravity.png");
				break;
			case 18:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileEnder.png");
				break;

			case 19:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileAntimatter.png");
				break;
			case 20:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileRedMatter.png");
				break;

			case 101:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileAntiBallistic.png");
				break;
			case 102:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileCluster.png");
				break;
			case 103:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileCluster.png");
				break;
			case 104:
				this.loadTexture(ICBM.TEXTURE_FILE_PATH + "MissileHoming.png");
				break;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * f1 + 90.0F, 0.0F, 0.0F, 1.0F);

		if (entityMissile.isCruise)
			GL11.glScalef(0.5f, 0.5f, 0.5f);

		if (entityMissile.explosiveID == 101)
		{
			mFanDan.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}
		else if (entityMissile.explosiveID == 102 || entityMissile.explosiveID == 103)
		{
			mFenZiDan.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}
		else if (entityMissile.explosiveID == 104)
		{
			mZhuiZhong.render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}
		else
		{
			models[entityMissile.explosiveID].render(entityMissile, (float) x, (float) y, (float) z, f, f1, 0.0625F);
		}

		GL11.glPopMatrix();
	}
}