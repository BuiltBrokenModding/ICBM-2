package icbm.explosion.blast;

import icbm.api.explosion.TriggerCause;

/**
 * Created by robert on 12/1/2014.
 */
public class BlastThreadTest extends BlastBasic
{
    @Override
    public boolean shouldThreadExplosion(TriggerCause triggerCause)
    {
        return true;
    }
}
