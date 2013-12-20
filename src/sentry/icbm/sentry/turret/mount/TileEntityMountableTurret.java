package icbm.sentry.turret.mount;

import icbm.sentry.turret.TileEntityTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import calclavia.lib.multiblock.IBlockActivate;
import calclavia.lib.prefab.network.PacketManager;

/** Mountable Turret
 * 
 * @author Calclavia */
public abstract class TileEntityMountableTurret extends TileEntityTurret implements IBlockActivate
{
    /** Fake entity this sentry uses for mounting the player in position */
    protected EntityMountPoint entityFake = null;

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        // Creates a fake entity to be mounted on
        if (this.entityFake == null || this.entityFake.isDead)
        {
            this.entityFake = new EntityMountPoint(this.worldObj, new Vector3(this.xCoord + 0.5, this.yCoord + 1.2, this.zCoord + 0.5), this, true);
            this.worldObj.spawnEntityInWorld(this.entityFake);
        }

        if (this.entityFake.riddenByEntity instanceof EntityPlayer)
        {
            EntityPlayer mountedPlayer = (EntityPlayer) this.entityFake.riddenByEntity;

            if (mountedPlayer.rotationPitch > this.maxPitch)
            {
                mountedPlayer.rotationPitch = this.maxPitch;
            }
            if (mountedPlayer.rotationPitch < this.minPitch)
            {
                mountedPlayer.rotationPitch = this.minPitch;
            }
            this.currentRotationPitch = this.wantedRotationPitch = mountedPlayer.rotationPitch;
            this.currentRotationYaw = this.wantedRotationYaw = mountedPlayer.rotationYaw;
        }
    }

    /** Performs a ray trace for the distance specified and using the partial tick time. Args:
     * distance, partialTickTime */
    public MovingObjectPosition rayTrace(double distance)
    {
        return this.getMuzzle().rayTrace(this.worldObj, this.wantedRotationYaw, this.wantedRotationPitch, true, distance);
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (!entityPlayer.isSneaking())
        {
            if (this.entityFake != null)
            {
                if (this.entityFake.riddenByEntity instanceof EntityPlayer)
                {
                    this.tryActivateWeapon();

                    if (!this.worldObj.isRemote)
                    {
                        PacketManager.sendPacketToClients(this.getRotationPacket());
                    }

                    return true;
                }

            }
        }

        this.mount(entityPlayer);

        return true;
    }

    public void mount(EntityPlayer entityPlayer)
    {
        if (!this.worldObj.isRemote)
        {

            entityPlayer.rotationYaw = this.currentRotationYaw;
            entityPlayer.rotationPitch = this.currentRotationPitch;

            entityPlayer.mountEntity(this.entityFake);
        }
    }

    public void tryActivateWeapon()
    {
        if (this.canActivateWeapon())
        {
            this.onWeaponActivated();
        }
    }

    @Override
    public boolean canApplyPotion(PotionEffect par1PotionEffect)
    {
        return false;
    }

}
