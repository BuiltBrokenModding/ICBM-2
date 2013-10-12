package icbm.explosion.model.missiles;

import icbm.api.ITracker;
import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzYaSuo;
import icbm.explosion.model.missiles.EntityMissile.MissileType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MissileHoming extends MissileBase
{
    public MissileHoming(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.hasBlock = false;
    }

    @Override
    public void launch(EntityMissile missileObj)
    {
        if (!missileObj.worldObj.isRemote)
        {
            WorldServer worldServer = (WorldServer) missileObj.worldObj;
            Entity trackingEntity = worldServer.getEntityByID(missileObj.genZongE);

            if (trackingEntity != null)
            {
                if (trackingEntity == missileObj)
                {
                    missileObj.setExplode();
                }

                missileObj.targetVector = new Vector3(trackingEntity);
            }
        }
    }

    @Override
    public void update(EntityMissile missileObj)
    {
        if (missileObj.feiXingTick > missileObj.missileFlightTime / 2 && missileObj.missileType == MissileType.missile)
        {
            WorldServer worldServer = (WorldServer) missileObj.worldObj;
            Entity trackingEntity = worldServer.getEntityByID(missileObj.genZongE);

            if (trackingEntity != null)
            {
                if (trackingEntity == missileObj)
                {
                    missileObj.setExplode();
                }

                missileObj.targetVector = new Vector3(trackingEntity);

                missileObj.missileType = MissileType.CruiseMissile;

                missileObj.xXiangCha = missileObj.targetVector.x - missileObj.posX;
                missileObj.yXiangCha = missileObj.targetVector.y - missileObj.posY;
                missileObj.zXiangCha = missileObj.targetVector.z - missileObj.posZ;

                missileObj.diShangJuLi = Vector2.distance(missileObj.startPos.toVector2(), missileObj.targetVector.toVector2());
                missileObj.tianGao = 150 + (int) (missileObj.diShangJuLi * 1.8);
                missileObj.missileFlightTime = (float) Math.max(100, 2.4 * missileObj.diShangJuLi);
                missileObj.acceleration = (float) missileObj.tianGao * 2 / (missileObj.missileFlightTime * missileObj.missileFlightTime);

                if (missileObj.xiaoDanMotion.equals(new Vector3()) || missileObj.xiaoDanMotion == null)
                {
                    float suDu = 0.3f;
                    missileObj.xiaoDanMotion = new Vector3();
                    missileObj.xiaoDanMotion.x = missileObj.xXiangCha / (missileObj.missileFlightTime * suDu);
                    missileObj.xiaoDanMotion.y = missileObj.yXiangCha / (missileObj.missileFlightTime * suDu);
                    missileObj.xiaoDanMotion.z = missileObj.zXiangCha / (missileObj.missileFlightTime * suDu);
                }
            }
        }
    }

    @Override
    public boolean onInteract(EntityMissile missileObj, EntityPlayer entityPlayer)
    {
        if (!missileObj.worldObj.isRemote && missileObj.feiXingTick <= 0)
        {
            if (entityPlayer.getCurrentEquippedItem() != null)
            {
                if (entityPlayer.getCurrentEquippedItem().getItem() instanceof ITracker)
                {
                    Entity trackingEntity = ((ITracker) entityPlayer.getCurrentEquippedItem().getItem()).getTrackingEntity(missileObj.worldObj, entityPlayer.getCurrentEquippedItem());

                    if (trackingEntity != null)
                    {
                        if (missileObj.genZongE != trackingEntity.entityId)
                        {
                            missileObj.genZongE = trackingEntity.entityId;
                            entityPlayer.addChatMessage("Missile target locked to: " + trackingEntity.getEntityName());

                            if (missileObj.getLauncher() != null && missileObj.getLauncher().getController() != null)
                            {
                                Vector3 newTarget = new Vector3(trackingEntity);
                                newTarget.y = 0;
                                missileObj.getLauncher().getController().setTarget(newTarget);
                            }

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isCruise()
    {
        return false;
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BzYaSuo(world, entity, x, y, z, 4).setDestroyItems().explode();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelICBM getMissileModel()
    {
        return new MMZhuiZhong();
    }
}
