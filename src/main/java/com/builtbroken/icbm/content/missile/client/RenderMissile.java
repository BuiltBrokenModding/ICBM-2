package com.builtbroken.icbm.content.missile.client;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.imp.IModelState;
import com.builtbroken.mc.client.json.imp.IRenderState;
import com.builtbroken.mc.client.json.render.RenderData;
import com.builtbroken.mc.lib.json.imp.IJsonGenObject;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderMissile extends Render
{
    public static final RenderMissile INSTANCE = new RenderMissile(0.5F);

    private RenderMissile(float f)
    {
        this.shadowSize = f;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float f1)
    {
        EntityMissile entityMissile = (EntityMissile) entity;
        IMissile missile = entityMissile.getMissile();

        GL11.glPushMatrix();
        GL11.glTranslated(x - 0.5, y - 0.5, z - 0.5);

        boolean rendered = false;

        //TODO remove this in a few updates
        if (missile instanceof ICustomMissileRender)
        {
            rendered = ((ICustomMissileRender) missile).renderMissileEntity(entity, f, f1);
        }

        //JSON render handling
        if(!rendered && missile instanceof IJsonGenObject)
        {
            RenderData data = ClientDataHandler.INSTANCE.getRenderData(((IJsonGenObject) missile).getContentID());
            if(data != null)
            {
                for(String stateID : new String[] {"missile", "entity"})
                {
                    IRenderState state = data.getState(stateID);
                    if(state instanceof IModelState)
                    {
                        rendered = ((IModelState) state).render(false, entity.rotationYaw, -entity.rotationPitch, 0);
                    }
                }
            }
        }

        //Backup render
        if (!rendered)
        {
            //TODO fix
            float yaw = interpolateRotation(-entity.prevRotationYaw + 90, -entity.rotationYaw + 90, f1);

            GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, f1) - 90, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(.5f, .5f, .5f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.CLASSIC_MISSILE_TEXTURE);
            if (missile == null || missile.getWarhead() != null)
            {
                Assets.CLASSIC_MISSILE_MODEL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
            }
            Assets.CLASSIC_MISSILE_MODEL.renderAllExcept("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
        }
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

    public static float interpolateRotation(float prev, float rotation, float f)
    {
        float f3 = rotation - prev;

        while (f3 < -180.0F)
        {
            f3 += 360.0F;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return prev + f * f3;
    }
}