package icbm.explosion.ex.missiles;

import icbm.explosion.ICBMExplosion;
import icbm.explosion.ex.Ex;
import net.minecraft.item.ItemStack;

public abstract class Missile extends Ex
{
    public Missile(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.hasBlock = false;
        this.hasGrenade = false;
        this.hasMinecart = false;
    }

    @Override
    public ItemStack getItemStack()
    {
        return new ItemStack(ICBMExplosion.itemMissile, 1, this.getID());
    }
}
