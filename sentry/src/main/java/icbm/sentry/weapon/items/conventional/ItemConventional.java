package icbm.sentry.weapon.items.conventional;

import icbm.sentry.weapon.items.HandAmmunitionHandler;
import icbm.sentry.weapon.items.ItemWeapon;
import icbm.sentry.weapon.items.WeaponContent;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

/**
 * Prefab for Conventional Weapons, most of this code will probably be put back up into ItemWeapon in the future, probably.
 * @author Archtikz
 */
public class ItemConventional extends ItemWeapon {

	public int gunDamage;
	private int capacity;
	
	public ItemConventional(int id, String name, WeaponContent wc) {
		super(id, name, wc);
		this.gunDamage = wc.getDamage();
		this.capacity = wc.getCapacity();
		this.setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		
		if(itemstack.stackTagCompound == null) {
			itemstack.stackTagCompound = new NBTTagCompound();
			itemstack.stackTagCompound.setInteger("clipCapacity", capacity);
			itemstack.stackTagCompound.setInteger("clipCurrentAmmo", 0);
		}
				
		return super.onItemRightClick(itemstack, world, player);
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

	}
	
	@Override
	public void onPostWeaponFired(ItemStack stack, World world, EntityPlayer shooter) {
		if(!world.isRemote) {
			if(!HandAmmunitionHandler.isEmpty(shooter, stack)) {
				HandAmmunitionHandler.consume(stack, 1);
			}
		}
	}

	@Override
	public void onSneakClick(ItemStack stack, World world, EntityPlayer shooter) {
		if(HandAmmunitionHandler.isEmpty(shooter, stack)) {
			if(searchInventoryForAmmo(shooter, false) != null) {
				searchInventoryForAmmo(shooter, true);
				if(!world.isRemote) {

					HandAmmunitionHandler.reload(stack);
				}
			}
		}		
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		list.add("Damage Per Shot: " + gunDamage);
		list.add("Ammo: " + HandAmmunitionHandler.getCurrentAmmo(itemStack)  + "/" + capacity);
		
		super.addInformation(itemStack, entityPlayer, list, par4);
	}

	@Override
	public void onRender(World world, EntityPlayer player, Vector3 hit) {
		drawParticleStreamTo(world, new Vector3(player), hit);
	}
}
