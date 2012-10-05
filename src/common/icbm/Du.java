package icbm;

import icbm.po.PChuanRanDu;
import icbm.po.PDaDu;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.Potion;
import universalelectricity.prefab.potion.CustomPotionEffect;
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
	protected void doPoisonEntity(EntityLiving entity, int amplifier)
	{
		if(this.isContagious)
    	{
    		entity.addPotionEffect(new CustomPotionEffect(PChuanRanDu.INSTANCE.getId(), 45 * 20, amplifier, null));
    		entity.addPotionEffect(new CustomPotionEffect(Potion.blindness.id, 15 * 20, amplifier));
    	}
    	else
    	{
            entity.addPotionEffect(new CustomPotionEffect(PDaDu.INSTANCE.getId(), 30 * 20, amplifier, null));
            entity.addPotionEffect(new CustomPotionEffect(Potion.confusion.id, 20 * 20, amplifier));
    	}
    	
    	entity.addPotionEffect(new CustomPotionEffect(Potion.hunger.id, 20 * 20, amplifier));
    	entity.addPotionEffect(new CustomPotionEffect(Potion.weakness.id, 20 * 20, amplifier));
	}

}
