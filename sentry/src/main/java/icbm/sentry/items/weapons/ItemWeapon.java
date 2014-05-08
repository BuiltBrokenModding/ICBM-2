package icbm.sentry.items.weapons;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.lib.prefab.item.ItemTooltip;
import calclavia.lib.prefab.vector.RayTraceHelper;

/**
 * Prefab for all weapon to be based on in ICBM. Does the same basic logic as sentry guns to fire the weapon.
 * 
 * @author Darkguardsman, Archtikz
 */
public abstract class ItemWeapon extends ItemTooltip {
	protected int blockRange = 150;
	protected String soundEffect;
	protected int bps;
	protected int inaccuracy;
	
	// TODO: Fix inaccuracy/bps
	
	public ItemWeapon(int id, String soundEffect) {
		super(id);
		this.soundEffect = soundEffect;
	}

	public ItemStack searchInventoryForAmmo(EntityPlayer player) {
		for(ItemStack stack : player.inventory.mainInventory) {
			if(stack.getItem() instanceof IItemAmmunition) {
				return stack;
			}
		}
		return null;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		onPreWeaponFired();
		onWeaponFired(itemstack, world, player);
		return itemstack;
	}

	/**
	 * Basic check if the weapon can be fired.
	 * 
	 * @param itemstack - weapon
	 * @param world - world the weapon is going to be firing in
	 * @param player - player firing the weapon
	 * @param rounds - rounds the will be consumed when fired
	 * @return true if the weapon can be fired
	 */
	public boolean canFireWeapon(ItemStack itemstack, World world, EntityPlayer player, int rounds) {
		return true;
	}
	
	public abstract void onPreWeaponFired();
	public abstract void onPostWeaponFired();
	/**
	 * Called when the player fires the weapon, should handle all weapon firing actions, audio, and effects. Shouldn't handle ammo.
	 * 
	 * @param itemstack
	 * @param world
	 * @param player
	 */
	public void onWeaponFired(ItemStack weaponStack, World world, EntityPlayer player) {
		Vec3 playerPosition = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 playerLook = RayTraceHelper.getLook(player, 1.0f);
		Vec3 p = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord, playerPosition.yCoord + playerLook.yCoord, playerPosition.zCoord + playerLook.zCoord);
		Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * blockRange, playerPosition.yCoord + playerLook.yCoord * blockRange, playerPosition.zCoord + playerLook.zCoord * blockRange);
	
		MovingObjectPosition hit = RayTraceHelper.do_rayTraceFromEntity(player, new Vector3().toVec3(), blockRange, true);

		if (hit != null) {
			if (hit.typeOfHit == EnumMovingObjectType.ENTITY && hit.entityHit != null) {
				onHitEntity(world, player, hit.entityHit);
			} else if (hit.typeOfHit == EnumMovingObjectType.TILE) {
				onHitBlock(world, player, new Vector3(hit.hitVec));
			}
			playSoundEffect(player);
			playerViewOffset = hit.hitVec;

			// TODO make beam brighter the longer it has been used
			// TODO adjust the laser for the end of the gun
			float x = (float) (MathHelper.cos((float) (player.rotationYawHead * 0.0174532925)) * (-.4) - MathHelper.sin((float) (player.rotationYawHead * 0.0174532925)) * (-.1));
			float z = (float) (MathHelper.sin((float) (player.rotationYawHead * 0.0174532925)) * (-.4) + MathHelper.cos((float) (player.rotationYawHead * 0.0174532925)) * (-.1));
		}
	}

	public abstract void onHitEntity(World world, EntityPlayer shooter, Entity entityHit);
	public abstract void onHitBlock(World world, EntityPlayer shooter, Vector3 hitVec);
	
	public void playSoundEffect(EntityPlayer player) {
		if (this.soundEffect != null && !this.soundEffect.isEmpty()) player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, this.soundEffect, 5F, 1F);
	}

	public float getGunDamage(ItemStack stack) {
		return 5f;
	}

}
