package icbm;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import atomicscience.api.Poison;

public class PoisonChemical extends Poison
{
	private boolean isContagious;
	
	public PoisonChemical(String name, int id, boolean isContagious)
	{
		super(name, id);
		this.isContagious = isContagious;
	}

	@Override
	protected void doPoisonEntity(EntityLiving entity)
	{
		if(this.isContagious)
    	{
    		entity.addPotionEffect(new PotionEffect(ICBMPotion.contagiousPoison.id, 45 * 20, 1));
    		entity.addPotionEffect(new PotionEffect(Potion.blindness.id, 15 * 20, 1));
    	}
    	else
    	{
            entity.addPotionEffect(new PotionEffect(ICBMPotion.extendedPoison.id, 30 * 20, 1));
            entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 20 * 20, 1));
    	}
    	
    	entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 20 * 20, 1));
    	entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 20 * 20, 1));
	}

}
