package icbm.zhapin.po;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class PDongShang extends PICBM
{
	public static PDongShang INSTANCE;

	public PDongShang(int id, boolean isBadEffect, int color, String name)
	{
		super(id, isBadEffect, color, name);
		this.setIconIndex(6, 0);
	}

	@Override
	public void performEffect(EntityLiving par1EntityLiving, int amplifier)
	{
		if (par1EntityLiving instanceof EntityPlayer)
		{
			((EntityPlayer) par1EntityLiving).addExhaustion(3F * (float) (amplifier + 1));
		}

		if (par1EntityLiving.isBurning())
		{
			par1EntityLiving.extinguish();
			par1EntityLiving.removePotionEffect(this.id);
		}

		// Check to see if it's on ice
		if (par1EntityLiving.worldObj.getBlockId(MathHelper.floor_double(par1EntityLiving.posX), MathHelper.floor_double(par1EntityLiving.posY) - 1, MathHelper.floor_double(par1EntityLiving.posZ)) == Block.ice.blockID)
		{
			par1EntityLiving.attackEntityFrom(DamageSource.magic, 2);
		}

		// Shatter enemy if health is too low
		if (par1EntityLiving.getHealth() < 6)
		{
			par1EntityLiving.attackEntityFrom(DamageSource.magic, 999999999);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier)
	{
		if (duration % 20 == 0)
		{
			return true;
		}

		return false;
	}
}
