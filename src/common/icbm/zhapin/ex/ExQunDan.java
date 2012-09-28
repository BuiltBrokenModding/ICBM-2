package icbm.zhapin.ex;

import icbm.ESuiPian;
import icbm.ICBM;
import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExQunDan extends ZhaPin
{
	public ExQunDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		int amount = 28;
		boolean isExplosive = false;
		
		if(this.getTier() == 2)
		{
			amount = 13;
			isExplosive = true;
		}
		
		if(explosionSource instanceof EShouLiuDan)
		{
			amount /= 2;
			position.y += 0.5D;
		}

		float amountToRotate = 360 / amount;

        for (int i = 0; i < amount; i++)
        {
            //Try to do a 360 explosion on all 6 faces of the cube.
            float rotationYaw = 0.0F + amountToRotate * i;

            for (int ii = 0; ii < amount; ii ++)
            {
            	ESuiPian arrow = new ESuiPian(worldObj, position.x, position.y, position.z, isExplosive);
                float rotationPitch = 0.0F + amountToRotate * ii;
                arrow.arrowCritical = true;
                arrow.setFire(100);
                arrow.setLocationAndAngles(position.x, position.y, position.z, rotationYaw, rotationPitch);
                arrow.posX -= (MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
                arrow.posY -= 0.10000000149011612D;
                arrow.posZ -= (MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
                arrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);
                arrow.yOffset = 0.0F;
                arrow.motionX = (-MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI));
                arrow.motionZ = (MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI));
                arrow.motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI));
                arrow.setArrowHeading(arrow.motionX, arrow.motionY, arrow.motionZ, 1.2F, 1.0F);

                if (!worldObj.isRemote)
                {
                    worldObj.spawnEntityInWorld(arrow);
                }
            }
        }
        
        return false;
	}

	@Override
	public void init()
	{
		if(this.getTier() == 1)
		{
			RecipeManager.addRecipe(this.getItemStack(), new Object [] {"???", "?@?", "???", '@', Block.tnt, '?', Item.arrow});
		}
		else if(this.getTier() == 2)
		{
	        RecipeManager.addRecipe(this.getItemStack(), new Object [] {" @ ", "@?@", " @ ", '?', huo.getItemStack(), '@', xiaoQunDan.getItemStack()}, this.getMing(), ICBM.CONFIGURATION, true);
		}
	}
}
