package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderMissile extends Render implements IItemRenderer
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

        if (!(missile instanceof ICustomMissileRender) || !((ICustomMissileRender) missile).renderMissileEntity(entity, f, f1))
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

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
    {
        return this.shouldUseRenderHelper(type, item, null);
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
    {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data)
    {
        if (this.shouldUseRenderHelper(type, item, null))
        {
            IMissile missile = MissileModuleBuilder.INSTANCE.buildMissile(item);

            float yaw = 0;
            float pitch = -90;

            switch (type)
            {
                case INVENTORY:
                    GL11.glTranslatef(-0.5f, 0f, 0f);
                    break;
                case EQUIPPED:
                    GL11.glTranslatef(1f, 0.3f, 0.5f);
                    break;
                case EQUIPPED_FIRST_PERSON:
                    GL11.glTranslatef(1.15f, -1f, 0.5f);
                    break;
                case ENTITY:
                    GL11.glTranslatef(-0.6f, 0f, 0f);
                    break;
                default:
                    break;
            }

            GL11.glRotatef(yaw, 0, 1f, 0f);
            GL11.glRotatef(pitch, 0, 0f, 1f);


            if (!(missile instanceof ICustomMissileRender) || !((ICustomMissileRender) missile).renderMissileItem(type, item, data))
            {
                GL11.glScalef(.5f, .5f, .5f);
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.CLASSIC_MISSILE_TEXTURE);
                if (missile == null || missile.getWarhead() != null)
                {
                    Assets.CLASSIC_MISSILE_MODEL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
                }
                Assets.CLASSIC_MISSILE_MODEL.renderAllExcept("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
            }
        }
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