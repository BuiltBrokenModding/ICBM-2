package icbm.zhapin.ex;

import icbm.ESuiPian;
import icbm.ZhuYao;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.Vector3;
import universalelectricity.prefab.RecipeHelper;

public class ExQunDan extends ZhaPin
{
	public ExQunDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		if (!worldObj.isRemote)
		{
			int amount = 28;
	
			if (this.getTier() == 2)
			{
				amount = 13;
			}
			else if (this.getID() == ZhaPin.zhen.getID())
			{
				amount = 20;
			}
	
			if (explosionSource instanceof EShouLiuDan)
			{
				amount /= 2;
				position.y += 0.5D;
			}
	
			float amountToRotate = 360 / amount;
	
			for (int i = 0; i < amount; i++)
			{
				// Try to do a 360 explosion on all 6
				// faces of the cube.
				float rotationYaw = 0.0F + amountToRotate * i;
	
				for (int ii = 0; ii < amount; ii++)
				{
					ESuiPian arrow = new ESuiPian(worldObj, position.x, position.y, position.z, this.getTier() == 2, this.getID() == ZhaPin.zhen.getID());
					
					if(this.getID() != ZhaPin.zhen.getID())
					{
						arrow.arrowCritical = true;
						arrow.setFire(100);
					}
					
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
					arrow.setArrowHeading(arrow.motionX*worldObj.rand.nextFloat(), arrow.motionY*worldObj.rand.nextFloat(), arrow.motionZ*worldObj.rand.nextFloat(), 0.5f+(0.7f*worldObj.rand.nextFloat()), 1.0F);
					worldObj.spawnEntityInWorld(arrow);
					
				}
			}
		}
		
		return false;
	}

	@Override
	public void init()
	{
		if (this.getID() == ZhaPin.xiaoQunDan.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[]
			{ "???", "?@?", "???", '@', Block.tnt, '?', Item.arrow }), this.getMing(), ZhuYao.CONFIGURATION, true);
		}
		else if (this.getID() == ZhaPin.zhen.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(10), new Object[]
			{ "SSS", "SAS", "SSS", 'A', Block.field_82510_ck, 'S', ZhaPin.xiaoQunDan.getItemStack()}), this.getMing(), ZhuYao.CONFIGURATION, true);
		}
		else if (this.getID() == ZhaPin.qunDan.getID())
		{
			RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[]
			{ " @ ", "@?@", " @ ", '?', huo.getItemStack(), '@', xiaoQunDan.getItemStack() }), this.getMing(), ZhuYao.CONFIGURATION, true);
		}
	}
}
