package icbm.explosion.fx;

import icbm.Reference;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import universalelectricity.api.vector.Vector3;
import calclavia.lib.render.RenderUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXAntimatterPartical extends EntityFX
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.TEXTURE_PATH + "antimatter.png");

    float antimatterParticleScale;

    public FXAntimatterPartical(World par1World, Vector3 position, double par8, double par10, double par12, double distance)
    {
        this(par1World, position, par8, par10, par12, 1.0F, distance);
    }

    public FXAntimatterPartical(World par1World, Vector3 position, double par8, double par10, double par12, float par14, double distance)
    {
        super(par1World, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += par8;
        this.motionY += par10;
        this.motionZ += par12;
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.30000001192092896D);
        this.particleScale *= 0.75F;
        this.particleScale *= par14;
        this.antimatterParticleScale = this.particleScale;
        this.particleMaxAge = (int) (10D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) (this.particleMaxAge * par14);
        this.renderDistanceWeight = distance;
        this.noClip = false;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float agescale = 0.0F;
        agescale = this.particleAge / (this.particleMaxAge / 2);
        if (agescale > 1.0F)
            agescale = 2.0F - agescale;

        this.particleScale = (this.antimatterParticleScale * agescale);

        tessellator.draw();
        GL11.glPushMatrix();

        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);

        float f10 = 0.5F * this.particleScale;
        float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * par2 - interpPosX);
        float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * par2 - interpPosY);
        float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * par2 - interpPosZ);

        tessellator.startDrawingQuads();

        tessellator.setBrightness(240);

        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.5F);
        tessellator.addVertexWithUV(f11 - par3 * f10 - par5 * f10, f12 - par4 * f10, f13 - par5 * f10 - par7 * f10, 0.0D, 1.0D);
        tessellator.addVertexWithUV(f11 - par3 * f10 + par5 * f10, f12 + par4 * f10, f13 - par5 * f10 + par7 * f10, 1.0D, 1.0D);
        tessellator.addVertexWithUV(f11 + par3 * f10 + par5 * f10, f12 + par4 * f10, f13 + par5 * f10 + par7 * f10, 1.0D, 0.0D);
        tessellator.addVertexWithUV(f11 + par3 * f10 - par5 * f10, f12 - par4 * f10, f13 + par5 * f10 - par7 * f10, 0.0D, 0.0D);

        tessellator.draw();

        GL11.glDisable(3042);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderUtility.PARTICLE_RESOURCE);

        tessellator.startDrawingQuads();
    }

    /** Called to update the entity's position/logic. */
    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
            return;
        }
    }
}
