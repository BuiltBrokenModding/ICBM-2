package icbm.zhapin;

import icbm.zhapin.po.PChuanRanDu;
import icbm.zhapin.po.PDaDu;

import java.util.EnumSet;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.potion.CustomPotionEffect;
import atomicscience.api.poison.Poison;

public class Du extends Poison
{
	private boolean isContagious;

	public Du(String name, int id, boolean isContagious)
	{
		super(name, id);
		this.isContagious = isContagious;
	}

	@Override
	protected void doPoisonEntity(Vector3 emitPosition, EntityLiving entity, EnumSet<ArmorType> armorWorn, int amplifier)
	{
		if (this.isContagious)
		{
			entity.addPotionEffect(new CustomPotionEffect(PChuanRanDu.INSTANCE.getId(), 45 * 20, amplifier, null));
			entity.addPotionEffect(new CustomPotionEffect(Potion.blindness.id, 15 * 20, amplifier));
		}
		else
		{
			entity.addPotionEffect(new CustomPotionEffect(PDaDu.INSTANCE.getId(), 30 * 20, amplifier, null));
			entity.addPotionEffect(new CustomPotionEffect(Potion.confusion.id, 30 * 20, amplifier));
		}

		entity.addPotionEffect(new CustomPotionEffect(Potion.hunger.id, 30 * 20, amplifier));
		entity.addPotionEffect(new CustomPotionEffect(Potion.weakness.id, 35 * 20, amplifier));
		entity.addPotionEffect(new CustomPotionEffect(Potion.digSlowdown.id, 60 * 20, amplifier));
	}

}
