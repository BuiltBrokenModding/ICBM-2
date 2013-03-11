package atomicscience.api.poison;

import java.util.EnumSet;

import net.minecraft.entity.EntityLiving;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.CustomDamageSource;
import universalelectricity.prefab.potion.CustomPotionEffect;

public class PoisonRadiation extends Poison
{
	public static final Poison INSTANCE = new PoisonRadiation("radiation", 0);
	public static final CustomDamageSource damageSource = (CustomDamageSource) new CustomDamageSource("radiation").setDamageBypassesArmor();
	public static boolean disabled = false;

	public PoisonRadiation(String name, int id)
	{
		super(name, id);
	}

	@Override
	protected void doPoisonEntity(Vector3 emitPosition, EntityLiving entity, EnumSet<ArmorType> armorWorn, int amplifier)
	{
		if (!PoisonRadiation.disabled)
		{
			if (emitPosition == null)
			{
				entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 15 * (amplifier + 1), amplifier, null));
				return;
			}

			if (entity.worldObj.getBlockDensity(emitPosition.toVec3(), entity.boundingBox) < 3)
			{
				entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 20 * (amplifier + 1), amplifier, null));
			}
		}
	}
}
