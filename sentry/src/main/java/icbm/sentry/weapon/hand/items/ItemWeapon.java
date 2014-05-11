package icbm.sentry.weapon.hand.items;

import icbm.core.prefab.item.ItemICBMBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.vector.RayTraceHelper;

/**
 * Prefab for all weapon to be based on in ICBM. Does the same basic logic as sentry guns to fire the weapon.
 * 
 * @author Darkguardsman, Archtikz
 */
public abstract class ItemWeapon extends ItemICBMBase {
	protected int blockRange = 150;
	protected String soundEffect;

	protected final int bps;
	protected final int cooldown;
	protected final double inaccuracy;

	public ItemWeapon(int id, String name, WeaponContent wc) {
		super(id, name);

		this.cooldown = wc.getCooldown();
		this.inaccuracy = wc.getInaccuracy();
		this.soundEffect = wc.getSoundname();
		this.bps = wc.getBulletsPerShot();
	}
	

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity par3Entity, int par4, boolean par5) {
		if(!world.isRemote) {
			if(getCooldownTicks(itemstack) > 0) {
				itemstack.getTagCompound().setInteger("cooldownTicks", getCooldownTicks(itemstack) - 1);
			}
		}
		super.onUpdate(itemstack, world, par3Entity, par4, par5);
	}
	
	public int getCooldownTicks(ItemStack stack) {
		//if(stack.getTagCompound() != null) {
			//if(stack.getTagCompound().hasKey("cooldownTicks"))
				return stack.getTagCompound().getInteger("cooldownTicks");
		//}
	//	return 0;
	}

	@Override 
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if(itemstack.stackTagCompound == null) {
			itemstack.setTagCompound(new NBTTagCompound());
			itemstack.getTagCompound().setInteger("cooldownTicks", 0);
		}
		
		if (player.isSneaking()) {
			onSneakClick(itemstack, world, player);
			return itemstack;
		}

		if(getCooldownTicks(itemstack) <= 0) {
			for(int i = 0; i < bps; i++) {
				onPreWeaponFired(itemstack, world, player);
				if (!HandAmmunitionHandler.isEmpty(player, itemstack)) {
					onWeaponFired(itemstack, world, player);
					onPostWeaponFired(itemstack, world, player);
				}
			}
			itemstack.getTagCompound().setInteger("cooldownTicks", cooldown);
		}
		return itemstack;
	}

	/** Based off MachineMuse */
	public void drawParticleStreamTo(World world, Vector3 start, IVector3 hit) {
		double scale = 0.02;
		Vector3 currentPoint = start.clone();
		Vector3 difference = new Vector3(hit).difference(start);

		while (currentPoint.distance(hit) > scale) {
			world.spawnParticle("smoke", currentPoint.x, currentPoint.y, currentPoint.z, 0.0D, 0.0D, 0.0D);
			currentPoint.add(difference.clone().scale(scale));
		}
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

	public abstract void onRender(World world, EntityPlayer player, Vector3 hit);

	public abstract void onSneakClick(ItemStack stack, World world, EntityPlayer shooter);

	public abstract void onPreWeaponFired(ItemStack stack, World world, EntityPlayer shooter);

	public abstract void onPostWeaponFired(ItemStack stack, World world, EntityPlayer shooter);

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
		Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * blockRange, playerPosition.yCoord + playerLook.yCoord * blockRange, playerPosition.zCoord
				+ playerLook.zCoord * blockRange);

		MovingObjectPosition hit = RayTraceHelper.do_rayTraceFromEntity(player, new Vector3().translate(getInaccuracy(world, inaccuracy)).toVec3(), blockRange, true);

		if (hit != null) {
			if (hit.typeOfHit == EnumMovingObjectType.ENTITY && hit.entityHit != null) {
				onHitEntity(world, player, hit.entityHit);
			} else if (hit.typeOfHit == EnumMovingObjectType.TILE) {
				onHitBlock(world, player, new Vector3(hit.hitVec));
			}
			playSoundEffect(player);
			playerViewOffset = hit.hitVec;
			onRender(world, player, new Vector3(hit));

			// TODO make beam brighter the longer it has been used
			// TODO adjust the laser for the end of the gun
			float x = (float) (MathHelper.cos((float) (player.rotationYawHead * 0.0174532925)) * (-.4) - MathHelper.sin((float) (player.rotationYawHead * 0.0174532925)) * (-.1));
			float z = (float) (MathHelper.sin((float) (player.rotationYawHead * 0.0174532925)) * (-.4) + MathHelper.cos((float) (player.rotationYawHead * 0.0174532925)) * (-.1));
		}
	}

	protected float min_range = 1;
	protected float max_range = 100;

	protected double getInaccuracy(World world, double distance) {
		double offset = distance * (world.rand.nextFloat() - world.rand.nextFloat()) * inaccuracy;
		if (distance < min_range || distance > max_range) { return offset * 2; }
		return offset;
	}

	public abstract void onHitEntity(World world, EntityPlayer shooter, Entity entityHit);

	public abstract void onHitBlock(World world, EntityPlayer shooter, Vector3 hitVec);

	public void playSoundEffect(EntityPlayer player) {
		if (this.soundEffect != null && !this.soundEffect.isEmpty()) player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, this.soundEffect, 5F, 1F);
	}
}
