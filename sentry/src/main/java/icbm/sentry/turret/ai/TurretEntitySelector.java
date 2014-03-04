package icbm.sentry.turret.ai;

import icbm.core.ICBMCore;
import icbm.core.Settings;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.auto.TurretAntiAir;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import calclavia.lib.access.IProfileContainer;

/**
 * Basic entity selector used by sentry guns to find valid targets
 * 
 * @author DarkGuardsman
 */
public class TurretEntitySelector implements IEntitySelector
{
	ITurretProvider turretProvider;
	public static boolean monsters = true, animals = false, npcs = false, players = false, flying = false, bosses = false;

    public static void configTurretTargeting()
    {
        String sentryFeatures = "Sentry_AI_Targeting";
        monsters = Settings.CONFIGURATION.get(sentryFeatures, "Mobs", monsters, "Preferably left as default, these options are for configuring Sentry AI Targeting, as which entities Sentries will target.").getBoolean(monsters);
        animals = Settings.CONFIGURATION.get(sentryFeatures, "PassiveAnimals", animals).getBoolean(animals);
        npcs = Settings.CONFIGURATION.get(sentryFeatures, "NPCs", npcs).getBoolean(npcs);
        players = Settings.CONFIGURATION.get(sentryFeatures, "Players", players).getBoolean(players);
        //flying = Settings.CONFIGURATION.get(sentryFeatures, "flyingMobs", flying).getBoolean(flying); maybe also allow this to be configured?
        bosses = Settings.CONFIGURATION.get(sentryFeatures, "Bosses", bosses).getBoolean(bosses);
    }

	public TurretEntitySelector(ITurret turret)
	{
		this.turretProvider = turret.getHost();
	}

	@Override
	public boolean isEntityApplicable(Entity entity)
	{
		if (!isFriendly(entity) && isValid(entity))
		{
			if (entity instanceof EntityFlying)
			{
                return turretProvider.getTurret() instanceof TurretAntiAir || flying;
			}
            else if (entity instanceof IBossDisplayData)
            {
                return bosses;
            }
			else if (entity instanceof EntityPlayer)
			{
				return players;
			}
			else if (entity instanceof IMob)
			{
				return monsters;
			}
			else if (entity instanceof IAnimals)
			{
				return animals;
			}
			else if (entity instanceof INpc)
			{
				return npcs;
			}
		}
		return false;
	}

	/** Checks if the sentry finds the entity friendly */
	public boolean isFriendly(Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			if (this.turretProvider instanceof IProfileContainer)
			{
				return ((IProfileContainer) this.turretProvider).canAccess(((EntityPlayer) entity).username);
			}
		}
		else if (entity instanceof EntityTameable)
		{
			if (this.turretProvider instanceof EntityOwnable)
			{
				return ((IProfileContainer) this.turretProvider).canAccess(((EntityOwnable) entity).getOwnerName());
			}
		}
		return false;
	}

	/** Checks if the target is valid for being attacked */
	public boolean isValid(Entity entity)
	{
		if (entity instanceof EntityLivingBase)
		{
			if (entity.isEntityAlive() && !entity.isInvisible())
			{
				if (entity instanceof EntityPlayer)
				{
					if (((EntityPlayer) entity).capabilities.isCreativeMode)
					{
						return false;
					}
				}
				return true;
			}

		}
		return false;
	}
}
