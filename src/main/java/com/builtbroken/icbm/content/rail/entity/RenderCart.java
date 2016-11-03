package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstationClient;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/31/2016.
 */
public class RenderCart extends Render
{
    public RenderCart()
    {
        this.shadowSize = 0.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return Assets.SMALL_WORKSTATION_TEXTURE;
    }

    @Override
    public void doRender(Entity entity, double xx, double yy, double zz, float deltaTime, float p_76986_9_)
    {
        final EntityCart cart = (EntityCart) entity;
        if (cart.getType() == CartTypes.SMALL)
        {
            TileSmallMissileWorkstationClient.renderDynamic(new Pos(xx - .5, yy, zz - .5), new Pos(1, 0, 2), cart.railSide.getOpposite(), cart.facingDirection, cart.getCargo(), 0, 0);
        }
        else
        {
            float halfWidth = cart.width / 2.0F;
            float halfLength = cart.length / 2.0F;
            float yaw = (float) Math.abs(MathUtility.clampAngleTo180(cart.rotationYaw));
            if (yaw >= 45 && yaw <= 135)
            {
                halfWidth = cart.length / 2.0F;
                halfLength = cart.width / 2.0F;
            }
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(
                    -(double) halfWidth,
                    0,
                    -(double) halfLength,

                    +(double) halfWidth,
                    0.3,
                    +(double) halfLength);

            GL11.glPushMatrix();
            GL11.glTranslated(xx, yy, zz);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
            RenderUtility.renderCube(bounds, Blocks.iron_block, Blocks.iron_block.getIcon(0, 0));
            GL11.glPopMatrix();
        }

        if (Engine.runningAsDev)
        {
            drawBounds(cart, xx, yy, zz);
        }
    }

    /**
     * Renders the bounding box around the cart
     *
     * @param cart
     * @param xx
     * @param yy
     * @param zz
     */
    protected void drawBounds(EntityCart cart, double xx, double yy, double zz)
    {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);

        float halfWidth = cart.width / 2.0F;
        float halfLength = cart.length / 2.0F;
        float yaw = (float) Math.abs(MathUtility.clampAngleTo180(cart.rotationYaw));
        if (yaw >= 45 && yaw <= 135)
        {
            halfWidth = cart.length / 2.0F;
            halfLength = cart.width / 2.0F;
        }

        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(xx - halfWidth, yy, zz - halfLength, xx + halfWidth, yy + (double) cart.height, zz + halfLength);
        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb, 16777215);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
}
