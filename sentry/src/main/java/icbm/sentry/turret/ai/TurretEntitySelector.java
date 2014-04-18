package icbm.sentry.turret.ai;

import icbm.Settings;
import icbm.core.DamageUtility;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.utility.nbt.ISaveObj;

/** Basic entity selector used by sentry guns to find valid targets
 * 
 * @author DarkGuardsman */
public class TurretEntitySelector implements IEntitySelector, ISaveObj
{
    ITurretProvider turretProvider;

    /* Global sentry settings */
    public static boolean target_mobs_global = true;
    public static boolean target_animals_global = true;
    public static boolean target_npcs_global = true;
    public static boolean target_players_global = true;
    public static boolean target_flying_global = true;
    public static boolean target_boss_global = false;

    protected boolean target_mobs = true;
    protected boolean target_animals = false;
    protected boolean target_npcs = false;
    protected boolean target_players = true;
    protected boolean target_flying = true;
    protected boolean target_boss = false;

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
        if (DamageUtility.canDamage(entity))
        {
            return !entity.isInvisible();
        }
        return false;
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        nbt.setBoolean("Kill_mobs", target_mobs);
        nbt.setBoolean("Kill_animals", target_animals);
        nbt.setBoolean("Kill_npcs", target_npcs);
        nbt.setBoolean("Kill_players", target_players);
        nbt.setBoolean("Kill_flying", target_flying);
        nbt.setBoolean("Kill_boss", target_boss);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        target_mobs = nbt.getBoolean("kill_mobs");
        target_animals = nbt.getBoolean("kill_animals");
        target_npcs = nbt.getBoolean("kill_npcs");
        target_players = nbt.getBoolean("kill_players");
        target_flying = nbt.getBoolean("kill_flying");
        target_boss = nbt.getBoolean("kill_boss");        
    }
}
