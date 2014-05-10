package icbm.sentry.items.weapons.conventional;

import icbm.sentry.ICBMSentry;
import icbm.sentry.items.weapons.ItemWeapon;
import icbm.sentry.items.weapons.WeaponContent;

import java.awt.Color;
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
 * Hey, Do you mind if we keep this code in here for now, instead of ItemWeapon, thanks ~Archtikz
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
	
	public boolean isEmpty(EntityPlayer player, ItemStack weaponStack) {
		if(weaponStack.stackTagCompound != null && weaponStack.stackTagCompound.hasKey("clipCurrentAmmo"))
			return weaponStack.stackTagCompound.getInteger("clipCurrentAmmo") == 0;
		else if(player.capabilities.isCreativeMode) 
			return false;
		else
			return true;
	}
	
	public void reload(ItemStack weaponStack) {
		weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", capacity); 
	}
	
	public int getCurrentAmmo(ItemStack weaponStack) {
		if(weaponStack.stackTagCompound != null && weaponStack.stackTagCompound.hasKey("clipCurrentAmmo"))
			return weaponStack.stackTagCompound.getInteger("clipCurrentAmmo");
		else
			return 0;
	}

	public void consume(ItemStack weaponStack, int amt) {
		if(amt < 0) {
			return;
		}
		if(amt > weaponStack.stackTagCompound.getInteger("clipCapacity")) {
			weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", 0);
			return;
		}
		weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", weaponStack.stackTagCompound.getInteger("clipCurrentAmmo") - amt);
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
			if(!isEmpty(shooter, stack)) {
				consume(stack, 1);
			}
		}
	}

	@Override
	public void onSneakClick(ItemStack stack, World world, EntityPlayer shooter) {
		if(isEmpty(shooter, stack)) {
			if(searchInventoryForAmmo(shooter, false) != null) {
				searchInventoryForAmmo(shooter, true);
				if(!world.isRemote) {

					reload(stack);
				}
			}
		}		
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		list.add("Damage Per Shot: " + gunDamage);
		list.add("Ammo: " + getCurrentAmmo(itemStack)  + "/" + capacity);
		
		super.addInformation(itemStack, entityPlayer, list, par4);
	}

	@Override
	public void onRender(World world, EntityPlayer player, Vector3 hit) {
		drawParticleStreamTo(world, new Vector3(player), hit);
	}
}
