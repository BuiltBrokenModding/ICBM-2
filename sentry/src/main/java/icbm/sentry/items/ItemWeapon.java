package icbm.sentry.items;

import net.minecraft.block.Block;
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
 * @author Darkguardsman
 */
public abstract class ItemWeapon extends ItemTooltip {
	protected int blockRange = 150;
	protected String soundEffect;

	public ItemWeapon(int id, String soundEffect) {
		super(id);
		this.soundEffect = soundEffect;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		player.addChatMessage("Click");

		if (canFireWeapon(itemstack, world, player, 1)) {
			ItemStack ammoStack = consumeAmmo(itemstack, world, player, 1, false);
			if (true || ammoStack != null) {
				int roundsFired = ammoStack != null ? ammoStack.getItem() instanceof IAmmunition ? ((IAmmunition) ammoStack.getItem()).getAmmoCount(ammoStack) : ammoStack.stackSize : 0;
				if (true || roundsFired >= 1) {
					onWeaponFired(itemstack, ammoStack, world, player);
				}
			}
		}
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

	/**
	 * Called when the player fires the weapon, should handle all weapon firing actions, audio, and effects. Shouldn't handle ammo.
	 * 
	 * @param itemstack
	 * @param world
	 * @param player
	 */
	public void onWeaponFired(ItemStack weaponStack, ItemStack ammoStack, World world, EntityPlayer player) {
		Vec3 playerPosition = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3 playerLook = RayTraceHelper.getLook(player, 1.0f);
		Vec3 p = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord, playerPosition.yCoord + playerLook.yCoord, playerPosition.zCoord + playerLook.zCoord);

		Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * blockRange, playerPosition.yCoord + playerLook.yCoord * blockRange, playerPosition.zCoord + playerLook.zCoord * blockRange);
		MovingObjectPosition hit = RayTraceHelper.do_rayTraceFromEntity(player, new Vector3().toVec3(), blockRange, true);

		if (hit != null) {
			if (hit.typeOfHit == EnumMovingObjectType.ENTITY && hit.entityHit != null) {
				// TODO re-implements laser damage source
				DamageSource damageSource = DamageSource.causeMobDamage(player);
				hit.entityHit.attackEntityFrom(damageSource, getGunDamage(weaponStack));
				playSoundEffect(player);
			} else if (hit.typeOfHit == EnumMovingObjectType.TILE) {
				if(world.getBlockId(hit.blockX, hit.blockY, hit.blockZ) == Block.glass.blockID || world.getBlockId(hit.blockX, hit.blockY, hit.blockZ) == Block.thinGlass.blockID) {
					world.setBlockToAir(hit.blockX, hit.blockY, hit.blockZ);
					world.playSoundEffect(hit.blockX, hit.blockY, hit.blockZ, "random.glass", 5F, 1F);
				}
			}
			playerViewOffset = hit.hitVec;

			// TODO make beam brighter the longer it has been used
			// TODO adjust the laser for the end of the gun
			float x = (float) (MathHelper.cos((float) (player.rotationYawHead * 0.0174532925)) * (-.4) - MathHelper.sin((float) (player.rotationYawHead * 0.0174532925)) * (-.1));
			float z = (float) (MathHelper.sin((float) (player.rotationYawHead * 0.0174532925)) * (-.4) + MathHelper.cos((float) (player.rotationYawHead * 0.0174532925)) * (-.1));
		}
	}

	public void playSoundEffect(EntityPlayer player) {
		if (this.soundEffect != null && !this.soundEffect.isEmpty()) player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, this.soundEffect, 5F, 1F);
	}

	public float getGunDamage(ItemStack stack) {
		return 5f;
	}

	/**
	 * Called to consume ammo or check if ammo can be consumed.
	 * 
	 * @param itemstack - weapon consuming the ammo
	 * @param world - world the ammo is consumed in
	 * @param player - player consuming the ammo
	 * @param rounds - rounds to consume from the ammo stack
	 * @param doConsume - true will consume the ammo from the gun or player's inventory
	 * @return Ammo stack that was consumed. Make sure to do an additional stack size or ammo count check
	 */
	public ItemStack consumeAmmo(ItemStack itemstack, World world, EntityPlayer player, int rounds, boolean doConsume) {
		return null;
	}

}
