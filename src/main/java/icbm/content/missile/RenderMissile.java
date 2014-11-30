package icbm.content.missile;

import cpw.mods.fml.client.FMLClientHandler;
import icbm.Reference;
import icbm.content.missile.EntityMissile.MissileType;

import java.util.HashMap;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import icbm.api.explosion.IExplosive;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import resonant.engine.References;

@SideOnly(Side.CLIENT)
/** @author Calclavia */
public class RenderMissile extends Render implements IItemRenderer
{
    public static final HashMap<IExplosive, IModelCustom> cache = new HashMap<IExplosive, IModelCustom>();

    public static final IModelCustom defaultMissile = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PREFIX + "missile_conventional.tcn"));
    public final static ResourceLocation TEXTURE = new ResourceLocation(References.DOMAIN, Reference.MODEL_TEXTURE_PATH + "missile_condensed.png");

    public RenderMissile(float f)
    {
        this.shadowSize = f;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float f1)
    {
        EntityMissile entityMissile = (EntityMissile) entity;
        IExplosive e = entityMissile.getExplosive();


        GL11.glPushMatrix();
        GL11.glTranslated(x, y - 1, z);
        GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * f1 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * f1 - 90, 0.0F, 0.0F, 1.0F);

        if (entityMissile.missileType == MissileType.DUMMY)
        {
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            GL11.glTranslated(-2, 0, 0);
        }

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

                //if (missile.getTier() == 2 || !missile.hasBlockForm())
                //{
                //    scale = scale / 1.5f;
                //}
                //else if (missile.getTier() == 3)
                //{
                //    scale = scale / 1.7f;
                //    right = -0.65f;
                //}
                //else if (missile.getTier() == 4)
                //{
                //    scale = scale / 1.4f;
                //    right = -0.45f;
                //}

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
}