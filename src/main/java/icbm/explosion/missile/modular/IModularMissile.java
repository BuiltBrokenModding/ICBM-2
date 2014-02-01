package icbm.explosion.missile.modular;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import icbm.api.IMissile;
import icbm.api.IMissileLockable;
import icbm.api.explosion.IExplosiveContainer;
import icbm.api.sentry.IAATarget;

public interface IModularMissile extends IMissileLockable, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile, IAATarget
{

}
