package com.builtbroken.icbm.content.blast.entity.slime;

import com.builtbroken.icbm.ICBM;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/2016.
 */
public class RenderSlimeRain extends RenderLiving
{
    private static final ResourceLocation texture = new ResourceLocation(ICBM.DOMAIN, "textures/entity/slime/slime.png");
    private ModelBase scaleAmount;

    public RenderSlimeRain()
    {
        super(new ModelSlime(16), 0.25F);
        scaleAmount = new ModelSlime(0);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntitySlimeRain p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        if (p_77032_1_.isInvisible())
        {
            return 0;
        }
        else if (p_77032_2_ == 0)
        {
            this.setRenderPassModel(this.scaleAmount);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }
        else
        {
            if (p_77032_2_ == 1)
            {
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            return -1;
        }
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntitySlimeRain slime, float partialTickTime)
    {
        float f1 = (float) slime.getSlimeSize();
        float f2 = (slime.prevSquishFactor + (slime.squishFactor - slime.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GL11.glScalef(f3 * f1, 1.0F / f3 * f1, f3 * f1);
        GL11.glColor3f(slime.color.getRed() / 255F, slime.color.getBlue() / 255F, slime.color.getBlue() / 255F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySlimeRain p_110775_1_)
    {
        return texture;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
    {
        this.preRenderCallback((EntitySlimeRain) p_77041_1_, p_77041_2_);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        return this.shouldRenderPass((EntitySlimeRain) p_77032_1_, p_77032_2_, p_77032_3_);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.getEntityTexture((EntitySlimeRain) p_110775_1_);
    }
}
