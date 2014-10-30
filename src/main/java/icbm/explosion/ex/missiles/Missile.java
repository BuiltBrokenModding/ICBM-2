package icbm.explosion.ex.missiles;

import icbm.explosion.ICBMExplosion;
import icbm.explosion.ex.Explosion;
import net.minecraft.item.ItemStack;

/** Ex object that are only defined as missiles
 * 
 * @author Calclavia */
public abstract class Missile extends Explosion
{
    public Missile(String name, int tier)
    {
        super(name, tier);
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
