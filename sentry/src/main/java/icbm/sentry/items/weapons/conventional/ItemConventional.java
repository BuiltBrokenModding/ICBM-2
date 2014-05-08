package icbm.sentry.items.weapons.conventional;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import icbm.sentry.items.weapons.AmmoHandler;
import icbm.sentry.items.weapons.ItemWeapon;

/**
 * @author Archtikz
 */
public class ItemConventional extends ItemWeapon {

	public int gunDamage;
	private AmmoHandler ammoHandler;

	public ItemConventional(int id, int gunDamage, String soundname) {
		super(id, soundname);
		this.gunDamage = gunDamage;
	}
	
	@Override
	public void onHitEntity(World world, EntityPlayer shooter, Entity entityHit) {
		DamageSource damageSource = DamageSource.causeMobDamage(shooter);
		entityHit.attackEntityFrom(damageSource, gunDamage);		
	}

	@Override
	public void onHitBlock(World world, EntityPlayer shooter, Vector3 hitVec) {
		if(world.getBlockId(hitVec.intX(), hitVec.intY(), hitVec.intZ()) == Block.glass.blockID || world.getBlockId(hitVec.intX(), hitVec.intY(), hitVec.intZ()) == Block.thinGlass.blockID) {
			world.setBlockToAir(hitVec.intX(), hitVec.intY(), hitVec.intZ());
			world.playSoundEffect(hitVec.intX(), hitVec.intY(), hitVec.intZ(), "minecraft:random.glass", 5F, 1F);
		}
	}

	@Override
	public void onPreWeaponFired() {
		
	}
	
	@Override
	public void onPostWeaponFired() {
		
	}
}
