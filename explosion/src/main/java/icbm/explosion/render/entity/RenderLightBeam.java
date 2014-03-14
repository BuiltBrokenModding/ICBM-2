package icbm.explosion.render.entity;

import icbm.explosion.entities.EntityLightBeam;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLightBeam extends Render
{
    @Override
    public void doRender(Entity par1Entity, double x, double y, double z, float f, float f1)
    {
        if (this.renderManager == null)
        {
            this.setRenderManager(RenderManager.instance);
        }

        EntityLightBeam entity = ((EntityLightBeam) par1Entity);

        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 1);

        double ad[] = new double[80]; // Previously
                                      // both
                                      // were 8
        double ad1[] = new double[80];
        double d3 = 0.0D;
        double d4 = 0.0D;

        for (int j = 7; j >= 0; j--)
        {
            ad[j] = d3;
            ad1[j] = d4;
        }

        for (int i = 0; i < 4; i++)
        {
            for (int ii = 0; ii < 3; ii++)
            {
                int l = 7;
                int i1 = 0;

                if (ii > 0)
                {
                    l = 7 - ii;
                }
                if (ii > 0)
                {
                    i1 = l - 2;
                }

                double d5 = ad[l] - d3;
                double d6 = ad1[l] - d4;

                for (int iii = l; iii >= i1; iii--)
                {
                    double d7 = d5;
                    double d8 = d6;

                    // Weird but cool effects
                    tessellator.startDrawing(5);

                    float f2 = 0.5F;

                    // Set color
                    tessellator.setColorRGBA_F(entity.red, entity.green, entity.blue, 10F);

                    double d9 = 0.10000000000000001D + i * 0.20000000000000001D;

                    if (ii == 0)
                    {
                        d9 *= iii * 0.10000000000000001D + 1.0D;
                    }

                    double d10 = 0.10000000000000001D + i * 0.20000000000000001D;

                    if (ii == 0)
                    {
                        d10 *= (iii - 1) * 0.10000000000000001D + 1.0D;
                    }

                    for (int iiii = 0; iiii < 5; iiii++)
                    {
                        double d11 = (x + 0.5D) - d9;
                        double d12 = (z + 0.5D) - d9;

                        if (iiii == 1 || iiii == 2)
                        {
                            d11 += d9 * 2D;
                        }

                        if (iiii == 2 || iiii == 3)
                        {
                            d12 += d9 * 2D;
                        }

                        double d13 = (x + 0.5D) - d10;
                        double d14 = (z + 0.5D) - d10;

                        if (iiii == 1 || iiii == 2)
                        {
                            d13 += d10 * 2D;
                        }
                        if (iiii == 2 || iiii == 3)
                        {
                            d14 += d10 * 2D;
                        }

                        tessellator.addVertex(d13 + d5, y + iii * 16, d14 + d6);
                        tessellator.addVertex(d11 + d7, y + (iii + 1) * 16, d12 + d8);
                    }

                    tessellator.draw();
                }

            }

        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }

}