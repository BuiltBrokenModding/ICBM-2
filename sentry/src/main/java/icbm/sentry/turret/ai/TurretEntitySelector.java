package icbm.sentry.turret.ai;

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
import calclavia.lib.config.Config;
import calclavia.lib.utility.nbt.ISaveObj;

/** Basic entity selector used by sentry guns to find valid targets
 * 
 * @author DarkGuardsman */
public class TurretEntitySelector implements IEntitySelector, ISaveObj
{
    ITurretProvider turretProvider;

    /* Global sentry settings */
    @Config(category = "Sentry_AI_Targeting")
    public static boolean target_mobs_global = true;
    @Config(category = "Sentry_AI_Targeting")
    public static boolean target_animals_global = false;
    @Config(category = "Sentry_AI_Targeting")
    public static boolean target_npcs_global = true;
    @Config(category = "Sentry_AI_Targeting")
    public static boolean target_players_global = true;
    @Config(category = "Sentry_AI_Targeting")
    public static boolean target_flying_global = true;
    @Config(category = "Sentry_AI_Targeting")
    public static boolean target_boss_global = false;

    /* Per sentry targeting variables */
    protected boolean target_mobs = true;
    protected boolean target_animals = false;
    protected boolean target_npcs = false;
    protected boolean target_players = true;
    protected boolean target_flying = true;
    protected boolean target_boss = false;

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
                return target_animals_global && target_animals;
            }
            else if (entity instanceof INpc)
            {
                return target_npcs_global && target_npcs;
            }
        }
        return false;
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
        nbt.setBoolean("vKill_mobs", target_mobs);
        nbt.setBoolean("vKill_animals", target_animals);
        nbt.setBoolean("vKill_npcs", target_npcs);
        nbt.setBoolean("vKill_players", target_players);
        nbt.setBoolean("vKill_flying", target_flying);
        nbt.setBoolean("vKill_boss", target_boss);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("vkill_mobs"))
            target_mobs = nbt.getBoolean("vkill_mobs");
        if (nbt.hasKey("vkill_animals"))
            target_animals = nbt.getBoolean("vkill_animals");
        if (nbt.hasKey("vkill_npcs"))
            target_npcs = nbt.getBoolean("vkill_npcs");
        if (nbt.hasKey("vkill_players"))
            target_players = nbt.getBoolean("vkill_players");
        if (nbt.hasKey("vkill_flying"))
            target_flying = nbt.getBoolean("vkill_flying");
        if (nbt.hasKey("vkill_boss"))
            target_boss = nbt.getBoolean("vkill_boss");
    }
}
