package icbm.explosion.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import universalelectricity.core.vector.Vector3;
import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXZhenBuo extends EntityFX
{
	public FXZhenBuo(World par1World, Vector3 position, float par8, float par10, float par12, double distance)
	{
		this(par1World, position, par8, par10, par12, 1.0F, distance);
	}

	public FXZhenBuo(World par1World, Vector3 position, float r, float g, float b, float size, double distance)
	{
		super(par1World, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
		this.particleRed = r;
		this.particleGreen = g;
		this.particleBlue = b;
		this.particleScale = size;
		this.particleMaxAge = (int) (10D / (Math.random() * 0.8D + 0.2D));
		this.particleMaxAge = (int) (this.particleMaxAge * size);
		this.renderDistanceWeight = distance;
		this.noClip = false;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(this.posX, this.posY, this.posZ);

		CalclaviaRenderHelper.enableBlending();
		CalclaviaRenderHelper.disableLighting();

		GL11.glColor4f(this.particleRed / 255, this.particleGreen / 255, this.particleBlue / 255, 0.5f);

		Sphere sphere = new Sphere();
		sphere.draw(this.particleScale, 32, 32);

		// Enable Lighting/Glow Off
		CalclaviaRenderHelper.enableLighting();

		// Disable Blending
		CalclaviaRenderHelper.disableBlending();
		GL11.glPopMatrix();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.particleScale++;

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
			return;
		}
	}
}
