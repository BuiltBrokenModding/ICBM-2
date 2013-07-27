package icbm.zhapin.fx;

import icbm.core.ZhuYaoICBM;

import java.util.Iterator;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.client.FMLClientHandler;

public class FXDian extends EntityFX
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.TEXTURE_PATH + "fadedSphere.png");

	private int type = 0;

	private float width = 0.03F;
	private FXDianCommon main;

	public FXDian(World world, Vector3 jammervec, Vector3 targetvec, long seed)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, jammervec, targetvec, seed);
		setupFromMain();
	}

	public FXDian(World world, Entity detonator, Entity target, long seed)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, detonator, target, seed);
		setupFromMain();
	}

	public FXDian(World world, Entity detonator, Entity target, long seed, int speed)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, detonator, target, seed, speed);
		setupFromMain();
	}

	public FXDian(World world, TileEntity detonator, Entity target, long seed)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, detonator, target, seed);
		setupFromMain();
	}

	public FXDian(World world, double x1, double y1, double z1, double x, double y, double z, long seed, int duration, float multi)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, x1, y1, z1, x, y, z, seed, duration, multi);
		setupFromMain();
	}

	public FXDian(World world, double x1, double y1, double z1, double x, double y, double z, long seed, int duration, float multi, int speed)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, x1, y1, z1, x, y, z, seed, duration, multi, speed);
		setupFromMain();
	}

	public FXDian(World world, double x1, double y1, double z1, double x, double y, double z, long seed, int duration)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, x1, y1, z1, x, y, z, seed, duration, 1.0F);
		setupFromMain();
	}

	public FXDian(World world, TileEntity detonator, double x, double y, double z, long seed)
	{
		super(world, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		this.main = new FXDianCommon(world, detonator, x, y, z, seed);
		setupFromMain();
	}

	private void setupFromMain()
	{
		this.particleAge = this.main.particleMaxAge;
		setPosition(this.main.start.x, this.main.start.y, this.main.start.z);
		setVelocity(0.0D, 0.0D, 0.0D);
	}

	public void defaultFractal()
	{
		this.main.defaultFractal();
	}

	public void fractal(int splits, float amount, float splitchance, float splitlength, float splitangle)
	{
		this.main.fractal(splits, amount, splitchance, splitlength, splitangle);
	}

	public void finalizeBolt()
	{
		this.main.finalizeBolt();
		ModLoader.getMinecraftInstance().effectRenderer.addEffect(this);
	}

	public void setType(int type)
	{
		this.type = type;
		this.main.type = type;
	}

	public void setMultiplier(float m)
	{
		this.main.multiplier = m;
	}

	public void setWidth(float m)
	{
		this.width = m;
	}

	@Override
	public void onUpdate()
	{
		this.main.onUpdate();
		if (this.main.particleAge >= this.main.particleMaxAge)
			setDead();
	}

	private static Vector3 getRelativeViewVector(Vector3 pos)
	{
		EntityPlayer renderentity = ModLoader.getMinecraftInstance().thePlayer;
		return new Vector3((float) renderentity.posX - pos.x, (float) renderentity.posY - pos.y, (float) renderentity.posZ - pos.z);
	}

	private void renderBolt(Tessellator tessellator, float partialframe, float cosyaw, float cospitch, float sinyaw, float cossinpitch, int pass)
	{
		Vector3 playervec = new Vector3(sinyaw * -cospitch, -cossinpitch / cosyaw, cosyaw * cospitch);
		float boltage = this.main.particleAge >= 0 ? this.main.particleAge / this.main.particleMaxAge : 0.0F;
		float mainalpha = 1.0F;
		if (pass == 0)
			mainalpha = (1.0F - boltage) * 0.4F;
		else
			mainalpha = 1.0F - boltage * 0.5F;
		int renderlength = (int) ((this.main.particleAge + partialframe + (int) (this.main.length * 3.0F)) / (int) (this.main.length * 3.0F) * this.main.numsegments0);
		for (Iterator iterator = this.main.segments.iterator(); iterator.hasNext();)
		{
			FXDianCommon.Segment rendersegment = (FXDianCommon.Segment) iterator.next();
			if (rendersegment.segmentno <= renderlength)
			{
				float width = (float) (this.width * (getRelativeViewVector(rendersegment.startpoint.point).getMagnitude() / 5.0F + 1.0F) * (1.0F + rendersegment.light) * 0.5F);
				Vector3 diff1 = playervec.crossProduct(rendersegment.prevdiff).scale(width / rendersegment.sinprev);
				Vector3 diff2 = playervec.crossProduct(rendersegment.nextdiff).scale(width / rendersegment.sinnext);
				Vector3 startvec = rendersegment.startpoint.point;
				Vector3 endvec = rendersegment.endpoint.point;
				float rx1 = (float) (startvec.x - interpPosX);
				float ry1 = (float) (startvec.y - interpPosY);
				float rz1 = (float) (startvec.z - interpPosZ);
				float rx2 = (float) (endvec.x - interpPosX);
				float ry2 = (float) (endvec.y - interpPosY);
				float rz2 = (float) (endvec.z - interpPosZ);
				tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, mainalpha * rendersegment.light);
				tessellator.addVertexWithUV(rx2 - diff2.x, ry2 - diff2.y, rz2 - diff2.z, 0.5D, 0.0D);
				tessellator.addVertexWithUV(rx1 - diff1.x, ry1 - diff1.y, rz1 - diff1.z, 0.5D, 0.0D);
				tessellator.addVertexWithUV(rx1 + diff1.x, ry1 + diff1.y, rz1 + diff1.z, 0.5D, 1.0D);
				tessellator.addVertexWithUV(rx2 + diff2.x, ry2 + diff2.y, rz2 + diff2.z, 0.5D, 1.0D);
				if (rendersegment.next == null)
				{
					Vector3 roundend = rendersegment.endpoint.point.clone().add(rendersegment.diff.clone().normalize().scale(width));
					float rx3 = (float) (roundend.x - interpPosX);
					float ry3 = (float) (roundend.y - interpPosY);
					float rz3 = (float) (roundend.z - interpPosZ);
					tessellator.addVertexWithUV(rx3 - diff2.x, ry3 - diff2.y, rz3 - diff2.z, 0.0D, 0.0D);
					tessellator.addVertexWithUV(rx2 - diff2.x, ry2 - diff2.y, rz2 - diff2.z, 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx2 + diff2.x, ry2 + diff2.y, rz2 + diff2.z, 0.5D, 1.0D);
					tessellator.addVertexWithUV(rx3 + diff2.x, ry3 + diff2.y, rz3 + diff2.z, 0.0D, 1.0D);
				}
				if (rendersegment.prev == null)
				{
					Vector3 roundend = rendersegment.startpoint.point.clone().subtract(rendersegment.diff.clone().normalize().scale(width));
					float rx3 = (float) (roundend.x - interpPosX);
					float ry3 = (float) (roundend.y - interpPosY);
					float rz3 = (float) (roundend.z - interpPosZ);
					tessellator.addVertexWithUV(rx1 - diff1.x, ry1 - diff1.y, rz1 - diff1.z, 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx3 - diff1.x, ry3 - diff1.y, rz3 - diff1.z, 0.0D, 0.0D);
					tessellator.addVertexWithUV(rx3 + diff1.x, ry3 + diff1.y, rz3 + diff1.z, 0.0D, 1.0D);
					tessellator.addVertexWithUV(rx1 + diff1.x, ry1 + diff1.y, rz1 + diff1.z, 0.5D, 1.0D);
				}
			}
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialframe, float cosyaw, float cospitch, float sinyaw, float sinsinpitch, float cossinpitch)
	{
		EntityPlayer renderentity = ModLoader.getMinecraftInstance().thePlayer;
		int visibleDistance = 100;
		if (!ModLoader.getMinecraftInstance().gameSettings.fancyGraphics)
			visibleDistance = 50;
		if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance)
			return;

		tessellator.draw();
		GL11.glPushMatrix();

		GL11.glDepthMask(false);
		GL11.glEnable(3042);

		this.particleRed = (this.particleGreen = this.particleBlue = 1.0F);

		switch (this.type)
		{
			case 0:
				this.particleRed = 0.6F;
				this.particleGreen = 0.3F;
				this.particleBlue = 0.6F;
				GL11.glBlendFunc(770, 1);
				break;
			case 1:
				this.particleRed = 0.6F;
				this.particleGreen = 0.6F;
				this.particleBlue = 0.1F;
				GL11.glBlendFunc(770, 1);
				break;
			case 2:
				this.particleRed = 0.1F;
				this.particleGreen = 0.1F;
				this.particleBlue = 0.6F;
				GL11.glBlendFunc(770, 1);
				break;
			case 3:
				this.particleRed = 0.1F;
				this.particleGreen = 1.0F;
				this.particleBlue = 0.1F;
				GL11.glBlendFunc(770, 1);
				break;
			case 4:
				this.particleRed = 0.6F;
				this.particleGreen = 0.1F;
				this.particleBlue = 0.1F;
				GL11.glBlendFunc(770, 1);
				break;
			case 5:
				this.particleRed = 0.6F;
				this.particleGreen = 0.2F;
				this.particleBlue = 0.6F;
				GL11.glBlendFunc(770, 771);
		}

		FMLClientHandler.instance().getClient().renderEngine.func_110577_a(TEXTURE);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		renderBolt(tessellator, partialframe, cosyaw, cospitch, sinyaw, cossinpitch, 0);
		tessellator.draw();

		switch (this.type)
		{
			case 0:
				this.particleRed = 1.0F;
				this.particleGreen = 0.6F;
				this.particleBlue = 1.0F;
				break;
			case 1:
				this.particleRed = 1.0F;
				this.particleGreen = 1.0F;
				this.particleBlue = 0.1F;
				break;
			case 2:
				this.particleRed = 0.1F;
				this.particleGreen = 0.1F;
				this.particleBlue = 1.0F;
				break;
			case 3:
				this.particleRed = 0.1F;
				this.particleGreen = 0.6F;
				this.particleBlue = 0.1F;
				break;
			case 4:
				this.particleRed = 1.0F;
				this.particleGreen = 0.1F;
				this.particleBlue = 0.1F;
				break;
			case 5:
				this.particleRed = 0.0F;
				this.particleGreen = 0.0F;
				this.particleBlue = 0.0F;
				GL11.glBlendFunc(770, 771);
		}

		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		renderBolt(tessellator, partialframe, cosyaw, cospitch, sinyaw, cossinpitch, 1);
		tessellator.draw();

		GL11.glDisable(3042);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();

		FMLClientHandler.instance().getClient().renderEngine.func_110577_a(CalclaviaRenderHelper.PARTICLE_RESOURCE);

		tessellator.startDrawingQuads();
	}

	public int getRenderPass()
	{
		return 2;
	}
}