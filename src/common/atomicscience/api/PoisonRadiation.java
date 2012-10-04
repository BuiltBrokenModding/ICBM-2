package atomicscience.api;

import net.minecraft.src.EntityLiving;
import universalelectricity.prefab.potion.CustomPotionEffect;

public class PoisonRadiation extends Poison
{
	public static final Poison INSTANCE = new PoisonRadiation("Radiation", 0);

	public PoisonRadiation(String name, int id)
	{
		super(name, id);
	}
	
	@Override
	protected void doPoisonEntity(EntityLiving entity)
	{
        entity.addPotionEffect(new CustomPotionEffect(PotionRadioactive.INSTANCE.getId(), 20 * 60, 2, null));
	}
}
