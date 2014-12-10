package icbm.content.missile;

import cpw.mods.fml.client.FMLClientHandler;
import icbm.Reference;

import java.util.HashMap;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import resonant.api.explosive.IExplosive;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/** @author Calclavia */
public class RenderMissile extends Render implements IItemRenderer
{
    public static final HashMap<IExplosive, IModelCustom> cache = new HashMap<IExplosive, IModelCustom>();

    public static final IModelCustom defaultMissile = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PREFIX + "missile_conventional.tcn"));
    public final static ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "missile_condensed.png");

    public RenderMissile(float f)
    {
        this.shadowSize = f;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float f1)
    {
        EntityMissile entityMissile = (EntityMissile) entity;

        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glTranslated(x, y - 1, z);
        float yaw = interpolateRotation(-entity.prevRotationYaw  + 90, -entity.rotationYaw  + 90, f1);

        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, f1) + 90, 0.0F, 0.0F, 1.0F);

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        defaultMissile.renderAll();
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
        return item.getItem() instanceof ItemMissile;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data)
    {
        if (this.shouldUseRenderHelper(type, item, null))
        {
            IExplosive missile = ((ItemMissile)item.getItem()).getExplosive(item);

            float scale = 0.7f;
            float right = 0f;

            if (type == IItemRenderer.ItemRenderType.INVENTORY)
            {
                scale = 0.4f;
                right = -0.5f;

                GL11.glTranslatef(right, 0f, 0f);
            }

            if (type == IItemRenderer.ItemRenderType.EQUIPPED)
            {
                GL11.glTranslatef(1f, 0.3f, 0.5f);
                GL11.glRotatef(0, 0, 0, 1f);
            }
            else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
            {
                GL11.glTranslatef(1.15f, -1f, 0.5f);
                GL11.glRotatef(0, 0, 0, 1f);
            }
            else
            {
                GL11.glRotatef(-90, 0, 0, 1f);
            }

            if (type == IItemRenderer.ItemRenderType.ENTITY)
            {
                scale = scale / 1.5f;
            }

            GL11.glScalef(scale, scale, scale);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
            defaultMissile.renderAll();
        }
    }

    private float interpolateRotation(float prev, float rotation, float f)
    {
        float f3 = rotation - prev;

        while(f3 < - 180.0F)
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