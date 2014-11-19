package icbm.explosion.ex.missiles;

import icbm.explosion.Explosion;

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
