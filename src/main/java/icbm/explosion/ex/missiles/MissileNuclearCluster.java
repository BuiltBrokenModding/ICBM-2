package icbm.explosion.ex.missiles;

import icbm.content.entity.EntityMissile;
import icbm.content.entity.EntityMissile.MissileType;
import icbm.explosion.Explosive;
import icbm.explosion.blast.BlastNuclear;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;

public class MissileNuclearCluster extends MissileCluster
{
    public MissileNuclearCluster()
    {
        super("nuclearCluster", 3);
        this.hasBlock = false;
    }

    public static final int MAX_CLUSTER = 4;

    @Override
    public void update(EntityMissile missileObj)
    {
        if (missileObj.motionY < -0.5)
        {
            if (missileObj.missileCount < MAX_CLUSTER)
            {
                if (!missileObj.worldObj.isRemote)
                {
                    Vector3 position = new Vector3(missileObj);
                    EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, new Vector3(missileObj), Explosive.nuclear.getID());
                    missileObj.worldObj.spawnEntityInWorld(clusterMissile);
                    clusterMissile.missileType = MissileType.CruiseMissile;
                    clusterMissile.protectionTime = 20;
                    clusterMissile.launch(missileObj.targetVector.add(new Vector3((missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30)));
                }

                missileObj.protectionTime = 20;
                missileObj.missileCount++;
            }
            else
            {
                missileObj.setDead();
            }
        }
    }

    @Override
    public void createExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastNuclear(world, entity, x, y, z, 30, 50).setNuclear().explode();
    }

    @Override
    public boolean isCruise()
    {
        return false;
    }
}
