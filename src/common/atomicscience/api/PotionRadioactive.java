package atomicscience.api;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import universalelectricity.prefab.potion.CustomPotion;

public class PotionRadioactive extends CustomPotion
{
	public static final PotionRadioactive INSTANCE = new PotionRadioactive(20, true, 5149489, "Radioactive");
	
	public PotionRadioactive(int id, boolean isBadEffect, int color, String name)
	{
		super(id, isBadEffect, color, name);
		this.setIconIndex(6, 0);
	}

	@Override
	public void performEffect(EntityLiving par1EntityLiving, int amplifier)
    {
		par1EntityLiving.attackEntityFrom(DamageSource.magic, 1);
		
		if(par1EntityLiving instanceof EntityPlayer)
		{
			((EntityPlayer)par1EntityLiving).addExhaustion(0.025F * (float)(amplifier + 1));
		}
    }
	
	@Override
	public boolean isReady(int duration, int amplifier)
    {
		if(duration % (20 * 3) == 0)
		{
			return true;
		}
		
		return false;
    }	
}
