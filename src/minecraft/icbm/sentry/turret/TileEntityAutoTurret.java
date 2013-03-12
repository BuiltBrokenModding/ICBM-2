package icbm.sentry.turret;

import icbm.sentry.ICBMSentry;
import icbm.sentry.api.IAutoSentry;
import icbm.sentry.logic.actions.ActionIdle;
import icbm.sentry.logic.actions.ActionKillTarget;
import icbm.sentry.logic.actions.ActionManager;
import icbm.sentry.logic.actions.ActionRepeat;
import icbm.sentry.logic.actions.ActionRotateTo;
import icbm.sentry.logic.actions.ActionSearchTarget;
import icbm.sentry.terminal.AccessLevel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

/**
 * Extend this class for all turrets that are automatic.
 * 
 * @author Rseifert
 * 
 */
public abstract class TileEntityAutoTurret extends TileEntityBaseTurret implements IAutoSentry
{
	/**
	 * The target this turret is hitting.
	 */
	public Entity target;

	public final ActionManager AIManager = new ActionManager();

	@Override
	public void initiate()
	{
		if (!this.worldObj.isRemote)
		{
			this.AIManager.addCommand(this, ActionSearchTarget.class);
			this.AIManager.addCommand(this, ActionKillTarget.class);
			this.AIManager.addCommand(this, ActionRepeat.class);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.AIManager.onUpdate();

		/**
		 * Only update the action manager for idle movements if the target is invalid.
		 */
		if (this.target == null && !this.worldObj.isRemote)
		{
			this.actionManager.onUpdate();

			if (!this.actionManager.hasTasks())
			{
				this.actionManager.clear();
				this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "" + (this.worldObj.rand.nextInt(60) + 30), "0" });
				this.actionManager.addCommand(this, ActionIdle.class, new String[] { "" + (this.worldObj.rand.nextInt(50) + 10) });

				this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "" + (-this.worldObj.rand.nextInt(60) - 30), "0" });
				this.actionManager.addCommand(this, ActionIdle.class, new String[] { "" + (this.worldObj.rand.nextInt(50) + 10) });

				this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "" + (this.worldObj.rand.nextInt(60) + 30), "0" });
				this.actionManager.addCommand(this, ActionIdle.class, new String[] { "" + (this.worldObj.rand.nextInt(50) + 10) });
			}
		}

	}

	@Override
	public AxisAlignedBB getTargetingBox()
	{
		/*
		 * TODO use meta data to find facing direction and restrict the grid to in front of the
		 * sentry
		 */
		int blockID = this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord);
		int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord - 1, this.zCoord);

		return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), yCoord - 5, zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + 5, zCoord + this.getDetectRange());
	}

	@Override
	public Entity getTarget()
	{
		return this.target;
	}

	@Override
	public boolean setTarget(Entity target, boolean override)
	{
		if (this.target == null)
		{
			this.target = target;
			return true;
		}
		else if (override)
		{
			this.target = target;
			return true;
		}
		return false;
	}

	@Override
	public boolean isValidTarget(Entity entity)
	{
		if (entity != null)
		{
			if (!entity.isDead && !entity.isEntityInvulnerable() && (!(entity instanceof IAnimals) || entity instanceof IMob))
			{
				if (entity.getDistance(this.xCoord, this.yCoord, this.zCoord) < this.getDetectRange())
				{
					if (!this.lookHelper.canEntityBeSeen(entity))
					{
						return false;
					}
					if (entity instanceof EntityPlayer)
					{
						EntityPlayer player = ((EntityPlayer) entity);

						if (player.capabilities.isCreativeMode)
						{
							return false;
						}

						if (this.getPlatform() != null && this.getPlatform().getUserAccess(player.username).ordinal() >= AccessLevel.USER.ordinal())
						{
							return false;
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean canActivateWeapon()
	{
		if (this.isValidTarget(this.target) && this.getPlatform() != null)
		{
			if (!this.lookHelper.isLookingAt(this.target, 10f))
			{
				this.lookHelper.lookAtEntity(this.target);
				return false;
			}
			else
			{
				return this.ticks % this.getCooldown() == 0 && (this.getPlatform().wattsReceived >= this.getRequest()) && this.getPlatform().hasAmmunition(ICBMSentry.conventionalBullet);
			}
		}

		return false;
	}
}
