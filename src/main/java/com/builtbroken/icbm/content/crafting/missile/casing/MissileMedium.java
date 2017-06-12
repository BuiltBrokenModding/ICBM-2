package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.missile.RenderMissile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by robert on 12/29/2014.
 */
public class MissileMedium extends Missile implements ICustomMissileRender
{
    public MissileMedium(ItemStack stack)
    {
        super(stack, MissileCasings.MEDIUM);
    }

    @Override
    public double getHeight()
    {
        return 16;
    }

    @Override
    public double getWidth()
    {
        return 1.2;
    }

    @Override
    public float getRenderHeightOffset()
    {
        return 6.6f;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.MEDIUM_MISSILE_TEXTURE);

        //GL11.glScalef(scale, scale, scale);
        if (type == IItemRenderer.ItemRenderType.INVENTORY)
        {
            //GL11.glTranslatef(-100f, -80f, 200); TODO fix as the scale changed
        }
        else if (type == IItemRenderer.ItemRenderType.ENTITY)
        {
            GL11.glTranslatef(-0.5f, 0.5f, 0);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(-2f, -1f, 0f);
            GL11.glRotatef(-50, 1, 0, 0);
            GL11.glRotatef(60, 0, 0, 1);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED)
        {
            GL11.glScaled(2, 2, 2);
            GL11.glRotatef(50, 1, 0, 0);
            GL11.glRotatef(60, 0, 0, 1);
            GL11.glTranslatef(-0.3f, 0.9f, 0.2f);
        }
        Assets.MEDIUM_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override
    public boolean renderMissileInWorld(float yaw, float pitch, float f)
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(pitch, 0.0F, 0.0F, 1.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.MEDIUM_MISSILE_TEXTURE);
        GL11.glScalef(1, 1, 1);
        Assets.MEDIUM_MISSILE_MODEL.renderAll();
        GL11.glEnable(GL11.GL_CULL_FACE);
        return true;
    }

    @Override
    public boolean renderMissileEntity(Entity entity, float f, float f1)
    {
        GL11.glTranslated(0.5, 1f, 0.5);
        float yaw = RenderMissile.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, f1) - 90;

        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderMissile.interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, f1) - 90, 0.0F, 0.0F, 1.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.MEDIUM_MISSILE_TEXTURE);
        GL11.glScalef(1, 1, 1);
        Assets.MEDIUM_MISSILE_MODEL.renderAll();
        return true;
    }
}
