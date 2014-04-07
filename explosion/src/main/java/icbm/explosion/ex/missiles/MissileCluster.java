package icbm.explosion.ex.missiles;

import icbm.explosion.entities.EntityMissile;
import icbm.explosion.entities.EntityMissile.MissileType;
import icbm.explosion.explosive.blast.BlastRepulsive;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

/** @author Calclavia */
public class MissileCluster extends Missile
{
    public MissileCluster(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.hasBlock = false;
        this.modelName = "missile_cluster.tcn";
    }

    public static final int MAX_CLUSTER = 12;

    @Override
    public void update(EntityMissile missileObj)
    {
        if (missileObj.motionY < -0.5)
        {
            if (missileObj.daoDanCount < MAX_CLUSTER)
            {
                if (!missileObj.worldObj.isRemote)
                {
                    Vector3 position = new Vector3(missileObj);
                    EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, new Vector3(missileObj), 0);
                    clusterMissile.missileType = MissileType.CruiseMissile;
                    clusterMissile.protectionTime = 20;
                    clusterMissile.launch(Vector3.translate(missileObj.targetVector, new Vector3((missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6)));
                    missileObj.worldObj.spawnEntityInWorld(clusterMissile);
                }
                missileObj.protectionTime = 20;
                missileObj.daoDanCount++;
            }
            else
            {
                missileObj.setDead();
            }
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 6).setDestroyItems().explode();
    }

    @Override
    public boolean isCruise()
    {
        return false;
    }
}
