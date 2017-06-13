package com.builtbroken.icbm.content.missile.parts.casing;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.missile.client.RenderMissile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Missile object that represents the small missile size
 * Created by robert on 12/29/2014.
 */
public class MissileSmall extends Missile implements ICustomMissileRender
{
    private static final float scale = .0015f;

    public MissileSmall(ItemStack stack)
    {
        super(stack, MissileCasings.SMALL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);

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
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override
    public boolean renderMissileInWorld(float yaw, float pitch, float f)
    {
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(pitch, 0.0F, 0.0F, 1.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        GL11.glScalef(1, 1, 1);
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override
    public boolean renderMissileEntity(Entity entity, float f, float f1)
    {
        GL11.glTranslated(0.5, 1f, 0.5);
        float yaw = RenderMissile.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, f1) - 90;

        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderMissile.interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, f1) - 90, 0.0F, 0.0F, 1.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        GL11.glScalef(1, 1, 1);
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override
    public float getRenderHeightOffset()
    {
        return ((float)getHeight() / 2f);
    }

    @Override
    public double getHeight()
    {
        return 2;
    }

    @Override
    public double getWidth()
    {
        return 0.5;
    }
}
