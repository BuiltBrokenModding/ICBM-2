package atomicscience.api.poison;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.CustomDamageSource;
import universalelectricity.prefab.potion.CustomPotionEffect;
import atomicscience.api.IAntiPoisonBlock;

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
	protected void doPoisonEntity(Vector3 emitPosition, EntityLivingBase entity, EnumSet<ArmorType> armorWorn, int amplifier)
	{
		if (!PoisonRadiation.disabled)
		{
			if (emitPosition == null)
			{
				entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 15 * (amplifier + 1), amplifier, null));
				return;
			}

			if (this.getAntiRadioactiveCount(entity.worldObj, emitPosition, new Vector3(entity)) <= amplifier)
			{
				entity.addPotionEffect(new CustomPotionEffect(PotionRadiation.INSTANCE.getId(), 20 * 20 * (amplifier + 1), amplifier, null));
			}
		}
	}

	public int getAntiRadioactiveCount(World world, Vector3 startingPosition, Vector3 endingPosition)
	{
		Vector3 delta = Vector3.subtract(endingPosition, startingPosition).normalize();
		Vector3 targetPosition = startingPosition.clone();
		double totalDistance = startingPosition.distanceTo(endingPosition);

		int count = 0;

		if (totalDistance > 1)
		{
			while (targetPosition.distanceTo(endingPosition) <= totalDistance)
			{
				int blockID = targetPosition.getBlockID(world);

				if (blockID > 0)
				{
					if (Block.blocksList[blockID] instanceof IAntiPoisonBlock)
					{
						if (((IAntiPoisonBlock) Block.blocksList[blockID]).isPoisonPrevention(world, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), this))
						{
							count++;
						}
					}
				}

				targetPosition.add(delta);
			}
		}

		return count;
	}
}
