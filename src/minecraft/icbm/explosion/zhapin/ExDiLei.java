package icbm.explosion.zhapin;

import icbm.api.ICBM;
import icbm.explosion.ESuiPian;
import icbm.explosion.muoxing.jiqi.MDiLei;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExDiLei extends ZhaPin
{
	protected ExDiLei(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.isMobile = true;
		this.setYinXin(40);
	}

	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		if (!worldObj.isRemote)
		{
			worldObj.createExplosion(explosionSource, position.x, position.y, position.z, 1.5f, true);
		}

		explosionSource.motionX = -0.125 + 0.25 * worldObj.rand.nextFloat();
		explosionSource.motionY = 0.6 + 0.3 * worldObj.rand.nextFloat();
		explosionSource.motionZ = -0.125 + 0.25 * worldObj.rand.nextFloat();
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		explosionSource.motionY -= 0.03D;
		explosionSource.rotationPitch += 1 * worldObj.rand.nextFloat();

		if (callCount < 20 * 2 && !explosionSource.isCollided) { return true; }

		if (callCount >= 20 * 2 && callCount % 2 == 0 && !worldObj.isRemote)
		{
			int amount = 5;
			float amountToRotate = 360 / amount;

			for (int i = 0; i < amount; i++)
			{
				// Try to do a 360 explosion on
				// all 6
				// faces of the cube.
				float rotationYaw = 0.0F + amountToRotate * i;

				for (int ii = 0; ii < amount; ii++)
				{
					ESuiPian arrow = new ESuiPian(worldObj, position.x, position.y, position.z, true, false);
					arrow.arrowCritical = true;
					arrow.setFire(60);
					float rotationPitch = 0.0F + amountToRotate * ii;
					arrow.setLocationAndAngles(position.x, position.y, position.z, rotationYaw, rotationPitch);
					arrow.posX -= (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
					arrow.posY -= 0.10000000149011612D;
					arrow.posZ -= (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
					arrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);
					arrow.yOffset = 0.0F;
					arrow.motionX = (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
					arrow.motionZ = (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
					arrow.motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI));
					arrow.setArrowHeading(arrow.motionX * worldObj.rand.nextFloat(), arrow.motionY * worldObj.rand.nextFloat(), arrow.motionZ * worldObj.rand.nextFloat(), 1.5f + (0.7f * worldObj.rand.nextFloat()), 2.0F);
					worldObj.spawnEntityInWorld(arrow);
				}
			}
		}

		if (callCount >= 20 * 2 + 20) { return false; }

		return true;
	}

	@Override
	public int proceduralInterval()
	{
		return 1;
	}

	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{

	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "S", "L", "R", 'S', ZhaPin.qunDan.getItemStack(), 'L', ZhaPin.la.getItemStack(), 'R', ZhaPin.tui.getItemStack() }), this.getName(), ICBM.CONFIGURATION, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object[] getRenderData()
	{
		return new Object[] { MDiLei.INSTANCE, ICBM.TEXTURE_FILE_PATH + "S-Mine.png" };
	}

	@Override
	public float getRadius()
	{
		return 20;
	}

	@Override
	public double getEnergy()
	{
		return 2000;
	}
}
