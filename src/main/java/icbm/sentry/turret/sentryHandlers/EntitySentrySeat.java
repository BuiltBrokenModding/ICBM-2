package icbm.sentry.turret.sentryHandlers;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntitySentrySeat extends EntityAnimal {

	EntityAIControlledByPlayer AI;
	
	public EntitySentrySeat(World world) {
		super(world);
		
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		// TODO Auto-generated method stub
		return null;
	}

}
