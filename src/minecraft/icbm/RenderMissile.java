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
package icbm;

import icbm.EntityMissile;
import icbm.models.ModelMissileAntiBallistic;
import icbm.models.ModelMissileAntimatter;
import icbm.models.ModelMissileBreaching;
import icbm.models.ModelMissileChemical;
import icbm.models.ModelMissileCluster;
import icbm.models.ModelMissileConflagration;
import icbm.models.ModelMissileContagious;
import icbm.models.ModelMissileConventional;
import icbm.models.ModelMissileEMP;
import icbm.models.ModelMissileEndothermic;
import icbm.models.ModelMissileFragmentation;
import icbm.models.ModelMissileIncendiary;
import icbm.models.ModelMissileNuke;
import icbm.models.ModelMissileRedMatter;
import icbm.models.ModelMissileShrapnel;
import icbm.models.ModelMissileSonic;
import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.Render;

import org.lwjgl.opengl.GL11;

public class RenderMissile extends Render
{
	private final ModelBase[] missileModel = {
			new ModelMissileConventional(),
			new ModelMissileShrapnel(),
			new ModelMissileIncendiary(),
			new ModelMissileChemical(),
			new ModelMissileFragmentation(),
			new ModelMissileContagious(),
			new ModelMissileSonic(),
			new ModelMissileBreaching(),
			new ModelMissileBreaching(), //CHANGE THIS
			new ModelMissileNuke(),
			new ModelMissileEMP(),
			new ModelMissileConflagration(),
			new ModelMissileEndothermic(),
			new ModelMissileEndothermic(), //CHANGE THIS
			new ModelMissileAntimatter(),
			new ModelMissileRedMatter()};
	
	private final ModelMissileAntiBallistic modelAntiBallistic = new ModelMissileAntiBallistic();
	private final ModelMissileCluster modelCluster = new ModelMissileCluster();
	
    public RenderMissile(float f)
    {
    	this.shadowSize = f;
    }

    public void renderMissile(EntityMissile entityMissile, double x, double y, double z, float f, float f1)
    {
    	//Use the correct model & texture for the specified missile metadata
    	switch(entityMissile.missileID)
    	{
    		default: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileConventional.png"); break;
    		case 1: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileShrapnel.png"); break;
    		case 2: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileIncendiary.png"); break;
    		case 3: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileChemical.png"); break;
    		case 4: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileFragmental.png"); break;
    		case 5: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileContagious.png"); break;
    		case 6: loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileSonic.png"); break;
    		case 7: loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileBreaching.png"); break;
    		case 8: loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileBreaching.png"); break;
    		case 9: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileNuclear.png"); break;
    		case 10: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileEMP.png"); break;
    		case 11: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileConflagration.png"); break;
    		case 12: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileEndothermic.png"); break;
    		case 13: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileEndothermic.png"); break;
    		case 14: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileAntimatter.png"); break;
    		case 15: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileRedMatter.png"); break;
    		
    		case 101: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileAntiBallistic.png"); break;
    		case 102: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileCluster.png"); break;
    		case 103: this.loadTexture(ICBM.TEXTURE_FILE_PATH+"MissileCluster.png"); break;
    	}

    	GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * f1 + 90.0F, 0.0F, 0.0F, 1.0F);
        
        if(entityMissile.isCruise) GL11.glScalef(0.5f, 0.5f, 0.5f);
        
        if(entityMissile.missileID == 101)
        {
        	modelAntiBallistic.render(entityMissile, (float)x, (float)y, (float)z, f, f1, 0.0625F);
        }
        else if(entityMissile.missileID == 102 || entityMissile.missileID == 103)
        {
        	modelCluster.render(entityMissile, (float)x, (float)y, (float)z, f, f1, 0.0625F);
        }
        else
        {
        	missileModel[entityMissile.missileID].render(entityMissile, (float)x, (float)y, (float)z, f, f1, 0.0625F);
        }
        
        GL11.glPopMatrix();
    }

    @Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        renderMissile((EntityMissile)entity, d, d1, d2, f, f1);
    }
}