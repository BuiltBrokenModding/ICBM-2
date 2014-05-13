package icbm.sentry.weapon.guns;

public class WeaponContent
{

    private int capacity, damage, cooldown, bps;
    private double inaccuracy;
    private String soundname;

    public WeaponContent(int capacity, int damage, double inaccuracy, int cooldown, int bps, String soundname)
    {
        this.capacity = capacity;
        this.damage = damage;
        this.inaccuracy = inaccuracy;
        this.cooldown = cooldown;
        this.soundname = soundname;
        this.bps = bps;
    }

    public int getBulletsPerShot()
    {
        return bps;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int getDamage()
    {
        return damage;
    }

    public double getInaccuracy()
    {
        return inaccuracy;
    }

    public int getCooldown()
    {
        return cooldown;
    }

    public String getSoundname()
    {
        return soundname;
    }
}
