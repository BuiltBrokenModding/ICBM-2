package com.builtbroken.icbm.content.fragments;

import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/2016.
 */
@SideOnly(Side.CLIENT)
public final class RenderFragment extends Render
{
    /** Backup texture */
    private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    private final RenderItem renderItem = new RenderItem()
    {
        @Override
        public boolean shouldBob()
        {
            return false;
        }
    };
    private final EntityItem entityItem = new EntityItem(null);

    public void doRender(EntityFragment entity, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_)
    {
        FragmentType type = entity.getFragmentType();

        if (type == FragmentType.BLOCK)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(xx, yy, zz);
            Block block = entity.getFragmentBlock();
            RenderUtility.renderCube(entity.getRenderShape().toAABB(), block == null ? Blocks.stone : block);
            GL11.glPopMatrix();
        }
        else if (type == FragmentType.PROJECTILE)
        {
            //Get item
            ItemStack item = entity.getFragmentMaterial() != null ? entity.getFragmentMaterial() : new ItemStack(Items.stick, 1, 0);

            //Fix missing render manager
            renderItem.setRenderManager(this.renderManager);

            //Update fake item for rendering
            entityItem.worldObj = entity.worldObj;
            entityItem.posX = entity.posX;
            entityItem.posY = entity.posY;
            entityItem.posZ = entity.posZ;
            entityItem.setEntityItemStack(item);
            entityItem.hoverStart = 0.0F;

            //Calc rotation
            float yaw = entity.rotationYaw - 90.0F;
            float pitch = entity.rotationPitch;
            float roll = 0;

            //Adjustment data per item
            float pitchAdjust = 0;
            double xAdjust = 0;
            double zAdjust = 0;

            //Adjust for sword, TODO move to JSON
            if (item.getItem() instanceof ItemSword)
            {
                //yaw = 0;
                //Fix rotation
                pitchAdjust = 135;

                xAdjust = -0.1f;

                roll = -1;
            }

            //Start
            GL11.glPushMatrix();

            //Translate
            GL11.glTranslated(xx, yy, zz);

            //Set rotation values
            GL11.glRotatef(MathHelper.clampAngleTo360(roll), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(MathHelper.clampAngleTo360(yaw), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(MathHelper.clampAngleTo360(pitch + pitchAdjust), 0.0F, 0.0F, 1.0F);


            //Render item using RenderItem class to save time
            renderItem.doRender(entityItem, xAdjust, 0, zAdjust, p_76986_8_, p_76986_9_);

            //End
            GL11.glPopMatrix();
        }
        else if (type == FragmentType.BLAZE)
        {
            doRenderFireBall(entity, xx, yy, zz, p_76986_8_, p_76986_9_, 0.3f);
        }
        else
        {
            this.bindEntityTexture(entity);
            doRenderArrow(entity, xx, yy, zz, p_76986_8_, p_76986_9_);
        }
    }

    /**
     * Renders an item in the same trajectory as an arrow
     *
     * @param entity     - entity being rendered
     * @param icon       - icon to render
     * @param xx         - location
     * @param yy         - location
     * @param zz         - location
     * @param p_76986_8_ - ? unknown
     * @param p_76986_9_ - ? unknown
     */
    @Deprecated //Renders like shit, being replaced with MC render calls for better visuals
    public void doRenderItemLikeArrow(EntityFragment entity, IIcon icon, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();

        //Translate
        GL11.glTranslatef((float) xx, (float) yy, (float) zz);

        //Rotate
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_76986_9_ - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_76986_9_, 0.0F, 0.0F, 1.0F);

        Tessellator tessellator = Tessellator.instance;

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        float scaleFactor = 1F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);

        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;

        //Render Side A
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (0.0F - f9), 0.0D, (double) minU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (0.0F - f9), 0.0D, (double) maxU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (1.0F - f9), 0.0D, (double) maxU, (double) minV);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (1.0F - f9), 0.0D, (double) minU, (double) minV);
        tessellator.draw();

        ///Render Side B
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (0.0F - f9), 0.0D, (double) minU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (0.0F - f9), 0.0D, (double) maxU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (1.0F - f9), 0.0D, (double) maxU, (double) minV);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (1.0F - f9), 0.0D, (double) minU, (double) minV);
        tessellator.draw();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    public void doRenderFireBall(EntityFragment entity, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_, float scale) //TODO abstract to renderIconFacingPlayer()
    {
        GL11.glPushMatrix();
        this.renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);

        GL11.glTranslatef((float) xx, (float) yy, (float) zz);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(scale / 1.0F, scale / 1.0F, scale / 1.0F);

        IIcon iicon = Items.fire_charge.getIconFromDamage(0);
        Tessellator tessellator = Tessellator.instance;

        float minU = iicon.getMinU();
        float maxU = iicon.getMaxU();
        float minV = iicon.getMinV();
        float maxV = iicon.getMaxV();

        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;

        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (0.0F - f9), 0.0D, (double) minU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (0.0F - f9), 0.0D, (double) maxU, (double) maxV);
        tessellator.addVertexWithUV((double) (f7 - f8), (double) (1.0F - f9), 0.0D, (double) maxU, (double) minV);
        tessellator.addVertexWithUV((double) (0.0F - f8), (double) (1.0F - f9), 0.0D, (double) minU, (double) minV);
        tessellator.draw();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void doRenderArrow(EntityFragment entity, double xx, double yy, double zz, float p_76986_8_, float p_76986_9_)
    {
        GL11.glPushMatrix();

        //Translate
        GL11.glTranslatef((float) xx, (float) yy, (float) zz);

        //Rotate
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * p_76986_9_ - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * p_76986_9_, 0.0F, 0.0F, 1.0F);

        Tessellator tessellator = Tessellator.instance;

        byte b0 = 0; //some bit conversion?

        float fin_u = 0.0F;
        float fin_u2 = 0.5F;
        float fin_v = (float) (0 + b0 * 10) / 32.0F;
        float fin_v2 = (float) (5 + b0 * 10) / 32.0F;

        //Calculate UV
        float arrow_u = 0.0F;
        float arrow_u2 = 0.15625F;
        float arrow_v = (float) (5 + b0 * 10) / 32.0F;
        float arrow_v2 = (float) (10 + b0 * 10) / 32.0F;


        float scaleFactor = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);

        //Render Side A
        GL11.glNormal3f(scaleFactor, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) arrow_u, (double) arrow_v);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) arrow_u2, (double) arrow_v);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) arrow_u2, (double) arrow_v2);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) arrow_u, (double) arrow_v2);
        tessellator.draw();

        //Render side B
        GL11.glNormal3f(-scaleFactor, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) arrow_u, (double) arrow_v);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) arrow_u2, (double) arrow_v);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) arrow_u2, (double) arrow_v2);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) arrow_u, (double) arrow_v2);
        tessellator.draw();

        //Render fins
        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, scaleFactor);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double) fin_u, (double) fin_v);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double) fin_u2, (double) fin_v);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double) fin_u2, (double) fin_v2);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double) fin_u, (double) fin_v2);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return arrowTextures;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_)
    {
        if (entity instanceof EntityFragment)
        {
            this.doRender((EntityFragment) entity, x, y, z, p_76986_8_, p_76986_9_);
        }
    }
}
