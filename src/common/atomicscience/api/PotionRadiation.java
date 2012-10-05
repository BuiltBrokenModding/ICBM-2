package atomicscience.api;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import universalelectricity.prefab.potion.CustomPotion;

public class PotionRadiation extends CustomPotion
{
	public static final PotionRadiation INSTANCE = new PotionRadiation(20, true, 5149489, "Radiation");
	
	public PotionRadiation(int id, boolean isBadEffect, int color, String name)
	{
		super(id, isBadEffect, color, name);
		this.setIconIndex(6, 0);
	}

	@Override
	public void performEffect(EntityLiving par1EntityLiving, int amplifier)
    {
		if(par1EntityLiving.worldObj.rand.nextFloat() > 0.85 - (amplifier*0.2))
		{
			par1EntityLiving.attackEntityFrom(PoisonRadiation.damageSource, 1);
			
			if(par1EntityLiving instanceof EntityPlayer)
			{
				((EntityPlayer)par1EntityLiving).addExhaustion(0.015F * (float)(amplifier + 1));
			}
		}
    }
	
	@Override
	public boolean isReady(int duration, int amplifier)
    {
		if(duration % 10 == 0)
		{
			return true;
		}
		
		return false;
    }	
}
