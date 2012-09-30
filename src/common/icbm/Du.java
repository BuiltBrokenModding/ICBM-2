package icbm;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.Potion;
import atomicscience.api.Poison;

public class Du extends Poison
{
	private boolean isContagious;
	
	public Du(String name, int id, boolean isContagious)
	{
		super(name, id);
		this.isContagious = isContagious;
	}

	@Override
	protected void doPoisonEntity(EntityLiving entity)
	{
		if(this.isContagious)
    	{
    		entity.addPotionEffect(new ICBMPotionEffect(ICBMPotion.contagiousPoison.id, 45 * 20, 1));
    		entity.addPotionEffect(new ICBMPotionEffect(Potion.blindness.id, 15 * 20, 1));
    	}
    	else
    	{
            entity.addPotionEffect(new ICBMPotionEffect(ICBMPotion.extendedPoison.id, 30 * 20, 1));
            entity.addPotionEffect(new ICBMPotionEffect(Potion.confusion.id, 20 * 20, 1));
    	}
    	
    	entity.addPotionEffect(new ICBMPotionEffect(Potion.hunger.id, 20 * 20, 1));
    	entity.addPotionEffect(new ICBMPotionEffect(Potion.weakness.id, 20 * 20, 1));
	}

}
