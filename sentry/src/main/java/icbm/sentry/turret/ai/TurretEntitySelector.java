package icbm.sentry.turret.ai;

import icbm.core.Settings;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
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

/** Basic entity selector used by sentry guns to find valid targets
 * 
 * @author DarkGuardsman */
public class TurretEntitySelector implements IEntitySelector
{
    ITurretProvider turretProvider;

    /* Global sentry settings */
    public static boolean target_mobs_global = true;
    public static boolean target_animals_global = true;
    public static boolean target_npcs_global = true;
    public static boolean target_players_global = true;
    public static boolean target_flying_global = true;
    public static boolean target_boss_global = false;

    private boolean target_mobs = true;
    private boolean target_animals = false;
    private boolean target_npcs = false;
    private boolean target_players = true;
    private boolean target_flying = true;
    private boolean target_boss = false;

    public static void configTurretTargeting()
    {
        final String sentryFeatures = "Sentry_AI_Targeting";
        target_mobs_global = Settings.CONFIGURATION.get(sentryFeatures, "Mobs", target_mobs_global, "Preferably left as default, these options are for configuring Sentry AI Targeting, as which entities Sentries will target.").getBoolean(target_mobs_global);
        target_animals_global = Settings.CONFIGURATION.get(sentryFeatures, "PassiveAnimals", target_animals_global).getBoolean(target_animals_global);
        target_npcs_global = Settings.CONFIGURATION.get(sentryFeatures, "NPCs", target_npcs_global).getBoolean(target_npcs_global);
        target_players_global = Settings.CONFIGURATION.get(sentryFeatures, "Players", target_players_global).getBoolean(target_players_global);
        //flying = Settings.CONFIGURATION.get(sentryFeatures, "flyingMobs", flying).getBoolean(flying); maybe also allow this to be configured?
        target_boss_global = Settings.CONFIGURATION.get(sentryFeatures, "Bosses", target_boss_global).getBoolean(target_boss_global);
    }

    public TurretEntitySelector(ITurret turret)
    {
        this.turretProvider = turret.getHost();
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        boolean re = false;
        if (!isFriendly(entity) && isValid(entity))
        {
            if (entity instanceof EntityFlying)
            {
                return target_flying_global && target_flying;
            }
            else if (entity instanceof IBossDisplayData)
            {
                return target_boss_global && target_boss;
            }
            else if (entity instanceof EntityPlayer)
            {
                return target_players_global && target_players;
            }
            else if (isMob(entity))
            {
                return target_mobs_global && target_mobs;
            }
            else if (entity instanceof IAnimals)
            {
                return target_animals_global & target_animals;
            }
            else if (entity instanceof INpc)
            {
                return target_npcs_global & target_npcs;
            }
        }
        return re;
    }

    public boolean isMob(Entity entity)
    {
        //TODO: Add mod compatibility here
        return entity instanceof IMob;
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
