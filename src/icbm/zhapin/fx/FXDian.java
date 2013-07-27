package icbm.zhapin.fx;

import icbm.core.ZhuYaoICBM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * An effect that renders a electrical bolt from one position to another. Inspired by Azanor's
 * lightning wand.
 * 
 * @author Calclavia
 * 
 */
@SideOnly(Side.CLIENT)
public class FXDian extends EntityFX
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ZhuYaoICBM.DOMAIN, ZhuYaoICBM.TEXTURE_PATH + "noise.png");

	/** The width of the electrical bolt. */
	private float boltWidth = 0.03f;

	/**
	 * Electric Bolt's start and end positions;
	 */
	private Vector3 start;
	private Vector3 end;

	/**
	 * An array of the segments of the bolt.
	 */
	private ArrayList<Segment> segments = new ArrayList<Segment>();

	private HashMap<Integer, Integer> splitparents = new HashMap<Integer, Integer>();
	public float multiplier;
	public double length;
	public int segmentCount;
	public int increment;
	public int type = 0;
	public boolean nonLethal = false;
	private int numsplits;
	private boolean finalized;
	private Random rand;
	public long seed;
	public int particleAge;
	public int particleMaxAge;

	public FXDian(World world, Vector3 startVec, Vector3 targetVec, long seed)
	{
		super(world, startVec.x, startVec.y, startVec.z, 0.0D, 0.0D, 0.0D);
		this.seed = seed;
		this.rand = new Random(this.seed);
		this.start = startVec;
		this.end = targetVec;
		this.particleAge = (3 + this.rand.nextInt(3) - 1);
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.segmentCount = 1;
		this.increment = 1;
		this.length = this.end.clone().subtract(this.start).getMagnitude();
		this.particleMaxAge = 10 * (3 + this.rand.nextInt(3) - 1);
		this.multiplier = 1.0F;

		/**
		 * Calculate all required segments of the entire bolt.
		 */
		this.segments.add(new Segment(this.start, this.end));
		calculateCollisionAndDiffs();
		this.defaultFractal();
		this.finalizeBolt();
	}

	public FXDian setMultiplier(float m)
	{
		this.multiplier = m;
		return this;
	}

	public FXDian setWidth(float m)
	{
		this.boltWidth = m;
		return this;
	}

	public FXDian setColor(float r, float g, float b)
	{
		this.particleRed = r;
		this.particleGreen = g;
		this.particleBlue = b;
		return this;
	}

	/**
	 * Create some default fractals.
	 */
	public void defaultFractal()
	{
		fractal(2, this.length * this.multiplier / 8.0F, 0.7F, 0.1F, 45.0F);
		fractal(2, this.length * this.multiplier / 12.0F, 0.5F, 0.1F, 50.0F);
		fractal(2, this.length * this.multiplier / 17.0F, 0.5F, 0.1F, 55.0F);
		fractal(2, this.length * this.multiplier / 23.0F, 0.5F, 0.1F, 60.0F);
		fractal(2, this.length * this.multiplier / 30.0F, 0.0F, 0.0F, 0.0F);
		fractal(2, this.length * this.multiplier / 34.0F, 0.0F, 0.0F, 0.0F);
		fractal(2, this.length * this.multiplier / 40.0F, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Slits a large segment into multiple smaller ones.
	 * 
	 * @param splitAmount - The amount of splits
	 * @param scale - The multiplier scale for the offset.
	 * @param splitChance - The chance of creating a split.
	 * @param splitLength - The length of each split.
	 * @param splitAngle - The angle of the split.
	 */
	public void fractal(int splitAmount, double scale, float splitChance, float splitLength, float splitAngle)
	{
		if (!this.finalized)
		{
			/** Temporarily store old segments in a new array */
			ArrayList<Segment> oldSegments = this.segments;
			this.segments = new ArrayList();
			/** Previous segment */
			Segment prev = null;

			for (Segment segment : oldSegments)
			{
				prev = segment.prev;
				Vector3 subsegment = segment.diff.clone().scale(1.0F / splitAmount);

				/**
				 * Creates an array of new bolt points. The first and last points of the bolts are
				 * the respected start and end points of the current segment.
				 */
				BoltPoint[] newPoints = new BoltPoint[splitAmount + 1];
				Vector3 startPoint = segment.startpoint.point;
				newPoints[0] = segment.startpoint;
				newPoints[splitAmount] = segment.endpoint;

				for (int i = 1; i < splitAmount; i++)
				{
					Vector3 randoff = segment.diff.getPerpendicular().rotate(this.rand.nextFloat() * 360.0F, segment.diff);
					randoff.scale((this.rand.nextFloat() - 0.5F) * scale);
					Vector3 basepoint = startPoint.clone().translate(subsegment.clone().scale(i));
					newPoints[i] = new BoltPoint(basepoint, randoff);
				}

				for (int i = 0; i < splitAmount; i++)
				{
					Segment next = new Segment(newPoints[i], newPoints[(i + 1)], segment.weight, segment.segmentno * splitAmount + i, segment.splitno);
					next.prev = prev;

					if (prev != null)
					{
						prev.next = next;
					}

					if ((i != 0) && (this.rand.nextFloat() < splitChance))
					{
						Vector3 splitrot = next.diff.xCrossProduct().rotate(this.rand.nextFloat() * 360.0F, next.diff);
						Vector3 diff = next.diff.clone().rotate((this.rand.nextFloat() * 0.66F + 0.33F) * splitAngle, splitrot).scale(splitLength);
						this.numsplits += 1;
						this.splitparents.put(this.numsplits, next.splitno);
						Segment split = new Segment(newPoints[i], new BoltPoint(newPoints[(i + 1)].basepoint, newPoints[(i + 1)].offsetvec.clone().add(diff)), segment.weight / 2.0F, next.segmentno, this.numsplits);
						split.prev = prev;
						this.segments.add(split);
					}

					prev = next;
					this.segments.add(next);
				}

				if (segment.next != null)
				{
					segment.next.prev = prev;
				}
			}

			this.segmentCount *= splitAmount;
		}
	}

	public void finalizeBolt()
	{
		if (!this.finalized)
		{
			this.finalized = true;
			calculateCollisionAndDiffs();

			Collections.sort(this.segments, new Comparator()
			{
				public int compare(Segment o1, Segment o2)
				{
					return Float.compare(o2.weight, o1.weight);
				}

				@Override
				public int compare(Object obj, Object obj1)
				{
					return compare((Segment) obj, (Segment) obj1);
				}
			});
		}
	}

	private static Vector3 getRelativeViewVector(Vector3 pos)
	{
		EntityPlayer renderentity = Minecraft.getMinecraft().thePlayer;
		return new Vector3((float) renderentity.posX - pos.x, (float) renderentity.posY - pos.y, (float) renderentity.posZ - pos.z);
	}

	private void calculateCollisionAndDiffs()
	{
		HashMap<Integer, Integer> lastActiveSegment = new HashMap<Integer, Integer>();

		Collections.sort(this.segments, new Comparator()
		{
			public int compare(Segment o1, Segment o2)
			{
				int comp = Integer.valueOf(o1.splitno).compareTo(Integer.valueOf(o2.splitno));
				if (comp == 0)
				{
					return Integer.valueOf(o1.segmentno).compareTo(Integer.valueOf(o2.segmentno));
				}
				return comp;
			}

			@Override
			public int compare(Object obj, Object obj1)
			{
				return compare((Segment) obj, (Segment) obj1);
			}
		});

		int lastSplitcalc = 0;
		int lastActiveSeg = 0;

		for (Segment segment : this.segments)
		{
			if (segment.splitno > lastSplitcalc)
			{
				lastActiveSegment.put(lastSplitcalc, lastActiveSeg);
				lastSplitcalc = segment.splitno;
				lastActiveSeg = ((Integer) lastActiveSegment.get(this.splitparents.get(segment.splitno))).intValue();
			}

			lastActiveSeg = segment.segmentno;
		}

		lastActiveSegment.put(lastSplitcalc, lastActiveSeg);
		lastSplitcalc = 0;
		lastActiveSeg = ((Integer) lastActiveSegment.get(0)).intValue();
		Segment segment;

		for (Iterator iterator = this.segments.iterator(); iterator.hasNext(); segment.calculateEndDifferences())
		{
			segment = (Segment) iterator.next();
			if (lastSplitcalc != segment.splitno)
			{
				lastSplitcalc = segment.splitno;
				lastActiveSeg = lastActiveSegment.get(segment.splitno);
			}

			if (segment.segmentno > lastActiveSeg)
			{
				iterator.remove();
			}
		}
	}

	/**
	 * Renders the bolts.
	 */
	private void renderBolt(Tessellator tessellator, float partialframe, float cosyaw, float cospitch, float sinyaw, float cossinpitch, int pass)
	{
		Vector3 playerVector = new Vector3(sinyaw * -cospitch, -cossinpitch / cosyaw, cosyaw * cospitch);
		float voltage = this.particleAge >= 0 ? ((float) this.particleAge / (float) this.particleMaxAge) : 0.0F;

		float mainAlpha = 1.0F;

		if (pass == 0)
		{
			mainAlpha = (1.0F - voltage) * 0.4F;
		}
		else
		{
			mainAlpha = 1.0F - voltage * 0.5F;
		}

		int renderlength = (int) ((this.particleAge + partialframe + (int) (this.length * 3.0F)) / (int) (this.length * 3.0F) * this.segmentCount);

		for (Segment renderSegment : this.segments)
		{
			if (renderSegment != null && renderSegment.segmentno <= renderlength)
			{
				float width = (float) (this.boltWidth * (getRelativeViewVector(renderSegment.startpoint.point).getMagnitude() / 5.0F + 1.0F) * (1.0F + renderSegment.weight) * 0.5F);
				Vector3 diff1 = playerVector.crossProduct(renderSegment.prevdiff).scale(width / renderSegment.sinprev);
				Vector3 diff2 = playerVector.crossProduct(renderSegment.nextdiff).scale(width / renderSegment.sinnext);
				Vector3 startvec = renderSegment.startpoint.point;
				Vector3 endvec = renderSegment.endpoint.point;
				float rx1 = (float) (startvec.x - interpPosX);
				float ry1 = (float) (startvec.y - interpPosY);
				float rz1 = (float) (startvec.z - interpPosZ);
				float rx2 = (float) (endvec.x - interpPosX);
				float ry2 = (float) (endvec.y - interpPosY);
				float rz2 = (float) (endvec.z - interpPosZ);
				tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, mainAlpha * renderSegment.weight);
				tessellator.addVertexWithUV(rx2 - diff2.x, ry2 - diff2.y, rz2 - diff2.z, 0.5D, 0.0D);
				tessellator.addVertexWithUV(rx1 - diff1.x, ry1 - diff1.y, rz1 - diff1.z, 0.5D, 0.0D);
				tessellator.addVertexWithUV(rx1 + diff1.x, ry1 + diff1.y, rz1 + diff1.z, 0.5D, 1.0D);
				tessellator.addVertexWithUV(rx2 + diff2.x, ry2 + diff2.y, rz2 + diff2.z, 0.5D, 1.0D);

				if (renderSegment.next == null)
				{
					Vector3 roundend = renderSegment.endpoint.point.clone().add(renderSegment.diff.clone().normalize().scale(width));
					float rx3 = (float) (roundend.x - interpPosX);
					float ry3 = (float) (roundend.y - interpPosY);
					float rz3 = (float) (roundend.z - interpPosZ);
					tessellator.addVertexWithUV(rx3 - diff2.x, ry3 - diff2.y, rz3 - diff2.z, 0.0D, 0.0D);
					tessellator.addVertexWithUV(rx2 - diff2.x, ry2 - diff2.y, rz2 - diff2.z, 0.5D, 0.0D);
					tessellator.addVertexWithUV(rx2 + diff2.x, ry2 + diff2.y, rz2 + diff2.z, 0.5D, 1.0D);
					tessellator.addVertexWithUV(rx3 + diff2.x, ry3 + diff2.y, rz3 + diff2.z, 0.0D, 1.0D);
				}

				if (renderSegment.prev == null)
				{
					Vector3 roundend = renderSegment.startpoint.point.clone().subtract(renderSegment.diff.clone().normalize().scale(width));
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
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialframe, float cosYaw, float cosPitch, float sinYaw, float sinSinPitch, float cosSinPitch)
	{
		EntityPlayer renderentity = Minecraft.getMinecraft().thePlayer;
		int visibleDistance = 100;

		if (!Minecraft.getMinecraft().gameSettings.fancyGraphics)
		{
			visibleDistance /= 2;
		}

		if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance)
		{
			return;
		}

		tessellator.draw();
		GL11.glPushMatrix();

		GL11.glDepthMask(false);
		GL11.glEnable(3042);

		FMLClientHandler.instance().getClient().renderEngine.func_110577_a(TEXTURE);
		/**
		 * Render the actual bolts.
		 */
		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		this.renderBolt(tessellator, partialframe, cosYaw, cosPitch, sinYaw, cosSinPitch, 0);
		tessellator.draw();

		// GL11.glBlendFunc(770, 771);

		tessellator.startDrawingQuads();
		tessellator.setBrightness(15728880);
		this.renderBolt(tessellator, partialframe, cosYaw, cosPitch, sinYaw, cosSinPitch, 1);
		tessellator.draw();

		GL11.glDisable(3042);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();

		FMLClientHandler.instance().getClient().renderEngine.func_110577_a(CalclaviaRenderHelper.PARTICLE_RESOURCE);

		tessellator.startDrawingQuads();
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 2;
	}

	/**
	 * A
	 * 
	 * @author User
	 * 
	 */
	public class BoltPoint
	{
		Vector3 point;
		Vector3 basepoint;
		Vector3 offsetvec;

		public BoltPoint(Vector3 basepoint, Vector3 offsetvec)
		{
			this.point = basepoint.clone().translate(offsetvec);
			this.basepoint = basepoint;
			this.offsetvec = offsetvec;
		}
	}

	public class Segment
	{
		public BoltPoint startpoint;
		public BoltPoint endpoint;
		public Vector3 diff;
		public Segment prev;
		public Segment next;
		public Vector3 nextdiff;
		public Vector3 prevdiff;
		public float sinprev;
		public float sinnext;
		/** The order of important */
		public float weight;
		public int segmentno;
		public int splitno;

		public Segment(BoltPoint start, BoltPoint end, float weight, int segmentnumber, int splitnumber)
		{
			this.startpoint = start;
			this.endpoint = end;
			this.weight = weight;
			this.segmentno = segmentnumber;
			this.splitno = splitnumber;
			calculateDifference();
		}

		public Segment(Vector3 start, Vector3 end)
		{
			this(new BoltPoint(start, new Vector3(0.0D, 0.0D, 0.0D)), new BoltPoint(end, new Vector3(0.0D, 0.0D, 0.0D)), 1.0F, 0, 0);
		}

		public void calculateDifference()
		{
			this.diff = this.endpoint.point.clone().subtract(this.startpoint.point);
		}

		public void calculateEndDifferences()
		{
			if (this.prev != null)
			{
				Vector3 prevdiffnorm = this.prev.diff.clone().normalize();
				Vector3 thisdiffnorm = this.diff.clone().normalize();
				this.prevdiff = thisdiffnorm.translate(prevdiffnorm).normalize();
				this.sinprev = ((float) Math.sin(Vector3.anglePreNorm(thisdiffnorm, prevdiffnorm.scale(-1.0F)) / 2.0F));
			}
			else
			{
				this.prevdiff = this.diff.clone().normalize();
				this.sinprev = 1.0F;
			}
			if (this.next != null)
			{
				Vector3 nextdiffnorm = this.next.diff.clone().normalize();
				Vector3 thisdiffnorm = this.diff.clone().normalize();
				this.nextdiff = thisdiffnorm.translate(nextdiffnorm).normalize();
				this.sinnext = ((float) Math.sin(Vector3.anglePreNorm(thisdiffnorm, nextdiffnorm.scale(-1.0F)) / 2.0F));
			}
			else
			{
				this.nextdiff = this.diff.clone().normalize();
				this.sinnext = 1.0F;
			}
		}

		@Override
		public String toString()
		{
			return this.startpoint.point.toString() + " " + this.endpoint.point.toString();
		}

	}

}