package icbm.explosion.missile;

import icbm.core.base.Modular;

/** @author DarkGuardsman */
public class ModularCasing extends Modular
{

    /** Starting HP of the casing */
    protected float health;
    /** Resistance to damage */
    protected float armor;
    /** Chance for the missile to be damaged when attacked */
    protected float damageChance;
    /** Chance for the missile to catch fire when damaged */
    protected float fireChance;

    public ModularCasing(String name, int tier)
    {
        super(name, tier);
    }

    public float getHealth()
    {
        return this.health;
    }

    public float getArmor()
    {
        return this.armor;
    }

    public float getDamageChance()
    {
        return this.damageChance;
    }

    public float getFireChance()
    {
        return this.fireChance;
    }

    public ModularCasing setDamageChance(float chance)
    {
        this.damageChance = chance;
        return this;
    }

    public ModularCasing setFireChance(float chance)
    {
        this.fireChance = chance;
        return this;
    }

    public ModularCasing setHealth(float health)
    {
        this.health = health;
        return this;
    }

    public ModularCasing setArmor(float armor)
    {
        this.armor = armor;
        return this;
    }

}
