package com.builtbroken.icbm.content.cow;

import com.builtbroken.icbm.client.Assets;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/1/2017.
 */
public class RenderEntityGaintCow extends RenderLiving
{
    private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");

    public RenderEntityGaintCow()
    {
        super(new ModelGaintCow(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return cowTextures;
    }

    @Override
    public void doRender(Entity entity, double renderX, double renderY, double renderZ, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();
        //renderOffsetAABB(entity.boundingBox, renderX - entity.lastTickPosX, renderY - entity.lastTickPosY, renderZ - entity.lastTickPosZ);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        super.doRender(entity, renderX, renderY, renderZ, p_76986_8_, p_76986_9_);
        GL11.glPopMatrix();
        //TODO render launcher

        try
        {
            GL11.glPushMatrix();
            GL11.glTranslated(renderX, renderY + entity.height, renderZ);

            bindTexture(Assets.SMALL_SILO_TEXTURE);
            Assets.SMALL_SILO_MODEL.renderAll();


            GL11.glPopMatrix();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
