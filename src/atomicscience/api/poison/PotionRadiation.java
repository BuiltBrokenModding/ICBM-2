package atomicscience.api.poison;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.prefab.potion.CustomPotion;
import atomicscience.api.AtomicScience;

public class PotionRadiation extends CustomPotion
{
	public static final PotionRadiation INSTANCE;

	static
	{
		AtomicScience.CONFIGURATION.load();
		INSTANCE = new PotionRadiation(21, true, 5149489, "radiation");
		AtomicScience.CONFIGURATION.save();
	}

	public PotionRadiation(int id, boolean isBadEffect, int color, String name)
	{
		super(AtomicScience.CONFIGURATION.get("Potion", name + " potion ID", id).getInt(id), isBadEffect, color, name);
		this.setIconIndex(6, 0);
	}

	@Override
	public void performEffect(EntityLiving par1EntityLiving, int amplifier)
	{
		if (par1EntityLiving.worldObj.rand.nextFloat() > 0.9 - (amplifier * 0.08))
		{
			par1EntityLiving.attackEntityFrom(PoisonRadiation.damageSource, 1);

			if (par1EntityLiving instanceof EntityPlayer)
			{
				((EntityPlayer) par1EntityLiving).addExhaustion(0.010F * (amplifier + 1));
			}
		}

	}

	@Override
	public boolean isReady(int duration, int amplifier)
	{
		if (duration % 10 == 0)
		{
			return true;
		}

		return false;
	}
}
