package icbm.explosion.missile.modular;

import calclavia.api.icbm.IMissile;
import calclavia.api.icbm.IMissileLockable;
import calclavia.api.icbm.ITarget;
import calclavia.api.icbm.explosion.IExplosiveContainer;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public interface IModularMissile extends IMissileLockable, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile, ITarget
{

}
