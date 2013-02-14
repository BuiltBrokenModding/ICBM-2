package icbm.sentry.turret;

import icbm.sentry.ICBMSentry;
import icbm.sentry.api.IAutoSentry;
import icbm.sentry.logic.actions.ActionIdle;
import icbm.sentry.logic.actions.ActionKillTarget;
import icbm.sentry.logic.actions.ActionManager;
import icbm.sentry.logic.actions.ActionRepeat;
import icbm.sentry.logic.actions.ActionRotateTo;
import icbm.sentry.logic.actions.ActionTargetSearch;
import icbm.sentry.terminal.AccessLevel;
import net.minecraft.entity.Entity;
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

	public boolean isIdle = false;
	public boolean isHunting = false;

	public final ActionManager AIManager = new ActionManager();

	public TileEntityAutoTurret()
	{
		this.addIdleSet();
		this.addHostileSet();
	}

	/**
	 * sets the sentry into an idle like animation state in which it just goes left to right to
	 * simulate searching for a target.
	 */
	public void addIdleSet()
	{
		if (!this.isIdle)
		{
			this.isIdle = true;
			this.actionManager.clear();
			this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "30", "0" });
			this.actionManager.addCommand(this, ActionIdle.class, new String[] { "10" });

			this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "-60", "0" });
			this.actionManager.addCommand(this, ActionIdle.class, new String[] { "10" });

			this.actionManager.addCommand(this, ActionRotateTo.class, new String[] { "30", "0" });
			this.actionManager.addCommand(this, ActionIdle.class, new String[] { "10" });

			this.actionManager.addCommand(this, ActionRepeat.class);
		}
	}

	public void addHostileSet()
	{
		if (!this.isHunting)
		{
			this.AIManager.addCommand(this, ActionTargetSearch.class);
			this.AIManager.addCommand(this, ActionKillTarget.class);
			this.AIManager.addCommand(this, ActionRepeat.class);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.isRunning())
		{
			this.AIManager.onUpdate();

			if (this.target == null)
			{
				this.actionManager.onUpdate();
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
		int blockID = this.worldObj.getBlockId(xCoord, yCoord, zCoord);
		int meta = this.worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord);

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
		if (entity == null || entity.isDead)
		{
			return false;
		}
		if (entity.getDistance(this.xCoord, this.yCoord, this.zCoord) > this.getDetectRange())
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
			if (this.getPlatform() != null && this.getPlatform().getPlayerAccess(player).ordinal() >= AccessLevel.USER.ordinal())
			{
				return false;
			}

		}
		return true;
	}

	@Override
	public boolean canActivateWeapon()
	{
		if (this.isValidTarget(this.target))
		{
			if (!lookManager.isLookingAt(target, 30f))
			{
				lookManager.lookAtEntity(target);
				return false;
			}
			else
			{
				return this.ticks % this.getCooldown() == 0 && (this.getPlatform().wattsReceived >= this.getRequest() || ICBMSentry.debugMode) && this.getPlatform().hasAmmunition(ICBMSentry.conventionalBullet);
			}
		}
		return false;
	}
}
