package icbm.sentry.turret.mounted;

import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.explosion.explosive.EntityExplosion;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;
import icbm.sentry.turret.weapon.WeaponProjectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IMultiBlock;
import calclavia.lib.prefab.vector.Cuboid;

/**
 * Railgun
 * 
 * @author Calclavia
 */
public class MountedRailgun extends TurretMounted implements IMultiBlock
{
	private int powerUpTicks = -1;

	/** Is current ammo antimatter */
	private boolean isAntimatter;

	private float explosionSize;

	/** A counter used client side for the smoke and streaming effects of the Railgun after a shot. */
	private int endTicks = 0;

	public MountedRailgun(TileTurret turretProvider)
	{
		super(turretProvider);
		energy = new EnergyStorageHandler(10000000000L);
		riderOffset = new Vector3(0, 0.2, 0);
		explosionSize = 5;
		maxCooldown = 20 * 10;

		weaponSystem = new WeaponProjectile(this, 1, 0)
		{
			@Override
			public boolean isAmmo(ItemStack stack)
			{
				return stack.getItem() instanceof IAmmunition && (stack.getItemDamage() == AmmoType.BULLET_RAIL.ordinal() || stack.getItemDamage() == AmmoType.BULLET_ANTIMATTER.ordinal());
			}

			@Override
			public void fire(Vector3 target)
			{
				consumeAmmo(ammoAmount, true);
			}
		};
	}

	@Override
	public boolean canFire()
	{
		return super.canFire() && powerUpTicks == -1;
	}

	@Override
	public void update()
	{
		super.update();

		if (!world().isRemote)
		{
			if (powerUpTicks >= 0 && super.canFire())
			{
				powerUpTicks++;

				if (powerUpTicks >= 70)
				{
					int explosionDepth = 10;
					Vector3 hit = null;

					while (explosionDepth > 0)
					{
						MovingObjectPosition objectMouseOver = ai.rayTrace(2000);

						if (objectMouseOver != null)
						{
							hit = new Vector3(objectMouseOver.hitVec);

							/**
							 * Kill all active explosives with antimatter.
							 */
							if (isAntimatter)
							{
								int radius = 50;
								AxisAlignedBB bounds = new Cuboid().expand(radius).translate(hit).toAABB();
								List<EntityExplosion> entities = world().getEntitiesWithinAABB(EntityExplosion.class, bounds);

								for (EntityExplosion entity : entities)
								{
									entity.endExplosion();
								}
							}

							int blockID = hit.getBlockID(world());

							if (Block.blocksList[blockID] == null || Block.blocksList[blockID].blockResistance != -1)
							{
								hit.setBlock(world(), 0);
							}

							// TODO: Fix this null.
							world().newExplosion(null, hit.x, hit.y, hit.z, explosionSize, true, true);
						}

						explosionDepth--;
					}

					powerUpTicks = -1;
					fire(hit);
					cooldown = maxCooldown;
				}
			}
		}
	}

	@Override
	public void tryFire()
	{
		powerUpTicks = 0;
		playFiringSound();
	}

	public void renderShot(Vector3 target)
	{
		endTicks = 20;
	}

	public void playFiringSound()
	{
		world().playSoundEffect(x(), y(), z(), Reference.PREFIX + "railgun", 5F, 0.9f + world().rand.nextFloat() * 0.2f);
	}

	@Override
	public Vector3[] getMultiBlockVectors()
	{
		return new Vector3[] { new Vector3(0, 1, 0) };
	}
}
