package icbm.sentry.items.weapons.conventional;

import java.util.List;

import icbm.sentry.items.weapons.AmmoHandler;
import icbm.sentry.items.weapons.ItemWeapon;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

/**
 * Hey, Do you mind if we keep this code in here for now, instead of ItemWeapon, thanks ~Archtikz
 * @author Archtikz
 */
public class ItemConventional extends ItemWeapon {

	public int gunDamage;
	private AmmoHandler ammoHandler;
	private int capacity;
	
	public ItemConventional(int id, int capacity, int gunDamage, String soundname) {
		super(id, soundname);
		this.gunDamage = gunDamage;
		this.capacity = capacity;
		this.setMaxDamage(capacity);
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
	public void onPreWeaponFired(ItemStack stack, World world, EntityPlayer shooter) {
		if(ammoHandler == null) {
			if(searchInventoryForAmmo(shooter, false) != null) {
				if(!world.isRemote) ammoHandler = new AmmoHandler(stack, capacity);
			}
		}
	}
	
	@Override
	public boolean isLoaded() {
		return ammoHandler != null && !ammoHandler.isEmpty();
	}
	
	@Override
	public void onPostWeaponFired(ItemStack stack, World world, EntityPlayer shooter) {
		if(!world.isRemote) {
			if(ammoHandler != null && !ammoHandler.isEmpty()) {
				ammoHandler.consume(1);
				stack.setItemDamage(stack.getItemDamage() - 1);
			}
		}
	}

	@Override
	public void onSneakClick(ItemStack stack, World world, EntityPlayer shooter) {
		if(ammoHandler == null) {
			if(searchInventoryForAmmo(shooter, false) != null) {
				if(!world.isRemote) ammoHandler = new AmmoHandler(stack, capacity);
				searchInventoryForAmmo(shooter, true);
			} else {
				return;
			}
		}
		if(ammoHandler.isEmpty()) {
			if(searchInventoryForAmmo(shooter, false) != null) {
				searchInventoryForAmmo(shooter, true);
				if(!world.isRemote) ammoHandler.reload();
			}
		}		
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		list.add("Ammo: " + ammoHandler.currentAmmo  + "/" + capacity);
		
		super.addInformation(itemStack, entityPlayer, list, par4);
	}
}
