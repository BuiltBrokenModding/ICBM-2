package atomicscience.api;

import java.util.EnumSet;

import net.minecraft.entity.EntityLiving;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.UEDamageSource;
import universalelectricity.prefab.potion.CustomPotionEffect;

public class PoisonRadiation extends Poison
{
	public static final Poison INSTANCE = new PoisonRadiation("Radiation", 0);
	public static final UEDamageSource damageSource = (UEDamageSource) new UEDamageSource("radiation", "%1$s died from radiation.").setDamageBypassesArmor();

	public PoisonRadiation(String name, int id)
	{
		super(name, id);
	}

	@Override
	protected void doPoisonEntity(Vector3 emitPosition, EntityLiving entity, EnumSet<ArmorType> armorWorn, int amplifier)
	{
		if (emitPosition == null)
		{
			entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 60, amplifier, null));
			return;
		}

		if (entity.worldObj.getBlockDensity(emitPosition.toVec3(), entity.boundingBox) < 3)
		{
			entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 60, amplifier, null));
		}
	}

	public static void register()
	{
		PotionRadiation.INSTANCE.register();
	}
}
