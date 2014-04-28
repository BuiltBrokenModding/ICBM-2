package icbm.sentry.turret.ai;

import icbm.core.DamageUtility;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;

import java.util.HashMap;
import java.util.Map.Entry;

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
import net.minecraft.nbt.NBTTagList;
import calclavia.api.icbm.ITarget;
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
    protected HashMap<String, Boolean> targetting = new HashMap<String, Boolean>();

    public TurretEntitySelector(ITurret turret)
    {
        this.turretProvider = turret.getHost();
        targetting.put("mobs", true);
        targetting.put("animals", false);
        targetting.put("npcs", false);
        targetting.put("players", true);
        targetting.put("flying", true);
        targetting.put("boss", false);
    }

    /** Checks if the turrets logic is allowed to target the type set by user settings */
    public boolean canTargetType(String type)
    {
        //TODO add a way of detecting different ways to enter the same type
        return targetting.containsKey(type) && targetting.get(type);
    }

    public void setTargetType(String type, boolean to)
    {
        //TODO check MC entity list
        if (targetting.containsKey(type))
        {
            targetting.put(type, to);
        }
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (!isFriendly(entity) && isValid(entity))
        {
            if (entity instanceof ITarget && ((ITarget) entity).canBeTargeted(this.turretProvider.getTurret()))
            {
                return true;
            }
            else if (entity instanceof EntityFlying)
            {
                return target_flying_global && canTargetType("flying");
            }
            else if (entity instanceof IBossDisplayData)
            {
                return target_boss_global && canTargetType("boss");
            }
            else if (entity instanceof EntityPlayer)
            {
                return target_players_global && canTargetType("players");
            }
            else if (isMob(entity))
            {
                return target_mobs_global && canTargetType("mobs");
            }
            else if (entity instanceof IAnimals)
            {
                return target_animals_global && canTargetType("animals");
            }
            else if (entity instanceof INpc)
            {
                return target_npcs_global && canTargetType("npcs");
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
        NBTTagList list = new NBTTagList();
        for (Entry<String, Boolean> entry : targetting.entrySet())
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("name", entry.getKey());
            tag.setBoolean("b", entry.getValue());
        }
        nbt.setTag("targetList", list);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("targetList"))
        {
            this.targetting.clear();
            NBTTagList list = nbt.getTagList("targetList");
            for (Object o : list.tagList)
            {
                if (o instanceof NBTTagCompound)
                {
                    this.targetting.put(((NBTTagCompound) o).getString("name"), ((NBTTagCompound) o).getBoolean("b"));
                }
            }
        }
    }
}
