package icbm.explosion.ex.missiles;

import icbm.core.ICBMCore;
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
        return new ItemStack(ICBMCore.itemMissile, 1, this.getID());
    }
}
