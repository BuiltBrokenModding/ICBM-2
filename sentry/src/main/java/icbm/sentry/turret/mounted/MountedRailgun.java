package icbm.sentry.turret.mounted;

import net.minecraft.entity.Entity;
import icbm.Reference;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.weapon.types.WeaponRailgun;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IMultiBlock;

/**
 * Railgun
 * 
 * @author Calclavia
 */
public class MountedRailgun extends TurretMounted implements IMultiBlock
{
	private int powerUpTicks = -1;

	public MountedRailgun(TileTurret turretProvider)
	{
		super(turretProvider);
		energy = new EnergyStorageHandler(10000000000L);
		riderOffset = new Vector3(0, 0.2, 0);
		maxCooldown = 20 * 5;
		weaponSystem = new WeaponRailgun(this);
	}

	@Override
	public void update()
	{
		super.update();

		if (!world().isRemote && powerUpTicks >= 0)
		{
			powerUpTicks++;

			if (powerUpTicks++ >= 70)
				fire();
		}
	}

	@Override
	public void fire()
	{
		super.fire();
		powerUpTicks = -1;
	}

	@Override
	public void onRedstone()
	{
		if (powerUpTicks == -1)
		{
			powerUpTicks = 0;
			world().playSoundEffect(x(), y(), z(), Reference.PREFIX + "railgun", 5F, 0.9f + world().rand.nextFloat() * 0.2f);
		}
	}

	@Override
	public Vector3[] getMultiBlockVectors()
	{
		return new Vector3[] { new Vector3(0, 1, 0) };
	}   
}
