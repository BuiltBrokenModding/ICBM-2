package icbm.explosion.ex.missiles;

import icbm.ICBM;
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
    }
}
