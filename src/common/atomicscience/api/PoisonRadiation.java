package atomicscience.api;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;

public class PoisonRadiation extends Poison
{
	public PoisonRadiation(String name, int id)
	{
		super(name, id);
	}
	
	@Override
	protected void doPoisonEntity(EntityLiving entity)
	{
        entity.addPotionEffect(new PotionEffect(Potion.poison.id, 20 * 5, 2));
    	entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 20 * 10, 2));
	}
}
