package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstationClient;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
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
    public void doRender(final Entity entity, final double xx, final double yy, final double zz, final float p_76986_8_, final float delta)
    {
        final EntityCart cart = (EntityCart) entity;
        float f5 = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * delta;

        double x2 = MathHelper.lerp(cart.lastRenderX, xx, delta);
        double y2 = MathHelper.lerp(cart.lastRenderY, yy, delta);
        double z2 = MathHelper.lerp(cart.lastRenderZ, zz, delta);

        GL11.glPushMatrix();
        GL11.glTranslated(x2, y2, z2);
        GL11.glRotatef(180.0F - delta, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f5, 0.0F, 0.0F, 1.0F);

        cart.lastRenderX = x2;
        cart.lastRenderY = y2;
        cart.lastRenderZ = z2;

        if (cart.getType() == CartTypes.SMALL)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 0.05f, 0f);
            GL11.glRotated(90, 0, 1, 0);

            GL11.glRotated(cart.rotationYaw, 0, 1, 0);

            //Renders the cart
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_WORKSTATION_TEXTURE2);
            Assets.CART1x3.renderAll();

            if (cart.getCargoMissile() != null)
            {
                //Render missile
                GL11.glRotated(-90, 0, 1, 0);
                renderMissile(cart.getCargoMissile(), cart.railSide, cart.facingDirection);
            }

            GL11.glPopMatrix();
        }
        else if (cart.getType() == CartTypes.MICRO)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, .32f, 0f);
            GL11.glRotated(90, 0, 1, 0);

            //Renders the cart
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
            Assets.CART1x1.renderAll();

            if (cart.getCargoMissile() != null)
            {
                GL11.glTranslated(0, -0.44, 0);
                if (cart.getCargoMissile() instanceof ICustomMissileRender)
                {
                    GL11.glTranslatef(0f, ((ICustomMissileRender) cart.getCargoMissile()).getRenderHeightOffset(), 0f);
                }
                renderMissile(cart.getCargoMissile(), ForgeDirection.EAST, ForgeDirection.UP);
            }

            GL11.glPopMatrix();
        }
        else if (cart.getType() == CartTypes.ThreeByThree)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, -0.05f, 0f);
            GL11.glRotated(90, 0, 1, 0);

            //Renders the cart
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
            Assets.CART3x3.renderAll();

            if (cart.getCargoMissile() != null)
            {
                GL11.glTranslated(0, -0.3, 0);
                if (cart.getCargoMissile() instanceof ICustomMissileRender)
                {
                    GL11.glTranslatef(0f, ((ICustomMissileRender) cart.getCargoMissile()).getRenderHeightOffset(), 0f);
                }
                renderMissile(cart.getCargoMissile(), ForgeDirection.EAST, ForgeDirection.UP);
            }

            GL11.glPopMatrix();
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

            if (cart.getCargoMissile() != null)
            {
                GL11.glTranslated(0, -0.1, 0);
                if (cart.getCargoMissile() instanceof ICustomMissileRender)
                {
                    GL11.glTranslatef(0f, ((ICustomMissileRender) cart.getCargoMissile()).getRenderHeightOffset(), 0f);
                }
                renderMissile(cart.getCargoMissile(), ForgeDirection.EAST, ForgeDirection.UP);
            }
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();

        if (Engine.runningAsDev)
        {
            // drawBounds(cart, xx, yy, zz);
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

    /**
     * Handles rendering of the missile
     */
    public static void renderMissile(IMissile missile, ForgeDirection connectedBlockSide, ForgeDirection direction)
    {
        ///Center render view to tile center
        GL11.glTranslatef(0f, 0.4f, 0f);

        //Handles setting the rotation based on the side
        switch (connectedBlockSide)
        {
            case UP:
            case DOWN:
                TileSmallMissileWorkstationClient.handleMissileRotationUD(direction);
                break;
            case EAST:
            case WEST:
                TileSmallMissileWorkstationClient.handleMissileRotationEW(direction);
                break;
            case SOUTH:
            case NORTH:
                TileSmallMissileWorkstationClient.handleMissileRotationNS(direction);
                break;
        }
        if (missile instanceof ICustomMissileRender)
        {
            ((ICustomMissileRender) missile).renderMissileInWorld(0, 0, 0);
        }
        else
        {
            //Bind texture
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
            //Group_001 body
            //Component_1_001 - 4 Body Fins
            if (missile.getWarhead() != null)
            {
                //Group_004 nose of warhead
                //Group_005 warhead
                Assets.SMALL_MISSILE_MODEL.renderOnly("Group_005");
                if (missile.getWarhead().getExplosive() != null)
                {
                    Assets.SMALL_MISSILE_MODEL.renderOnly("Group_004");
                }
            }
            if (missile.getEngine() != null)
            {
                //Group_002 - Engine thruster
                //Group_003 - Engine case
                //Component_3_001 - 8 Engine lower segments
                //Component_2_001 - 4 Engine fins
                Assets.SMALL_MISSILE_MODEL.renderOnly("Group_002", "Group_003");
                for (int i = 1; i < 9; i++)
                {
                    Assets.SMALL_MISSILE_MODEL.renderOnly("Component_3_00" + i);
                }
                for (int i = 1; i < 5; i++)
                {
                    Assets.SMALL_MISSILE_MODEL.renderOnly("Component_2_00" + i);
                }
            }
            if (missile.getGuidance() != null)
            {
                //TODO add model indication showing no guidance added
            }
            //Render body and fins
            Assets.SMALL_MISSILE_MODEL.renderOnly("Group_001", "Component_1_001", "Component_1_002", "Component_1_003", "Component_1_004");
        }
    }
}
