package icbm.api.sentry;

import net.minecraft.tileentity.TileEntity;

/**
 * Apply this to an entity if it is meant to be targeted by the AA Turret.
 * 
 * @author Calclavia
 * 
 */
public interface IAATarget
{
	/**
	 * destroys the target with a boom. This is a forced way for the sentry too kill the target if
	 * it doesn't take damage
	 */
	public void destroyCraft();

	/**
	 * Applies damage to the the target
	 * 
	 * @param damage - damage in half HP
	 * @return the amount of HP left. Return -1 if this target can't take damage, and will be chance
	 * killed. Return 0 if this target is dead and destroyCraft() will be called.
	 */
	public int doDamage(int damage);

	/**
	 * Can this auto turret target this craft. Turret will automaticly sort out players, sight line,
	 * and most of its normal data. Use this to create target jamming, cloaking, and other modules.
	 * 
	 * @param turret - turret targeting this
	 * @return true if it can
	 */
	public boolean canBeTargeted(TileEntity turret);
}
