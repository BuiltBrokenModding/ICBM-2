package icbm.sentry.turret.mount;

import icbm.api.explosion.IExplosive;
import icbm.core.ICBMCore;
import icbm.core.implement.IRedstoneReceptor;
import icbm.sentry.ICBMSentry;
import icbm.sentry.ProjectileType;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import calclavia.lib.multiblock.IMultiBlock;
import calclavia.lib.prefab.network.IPacketReceiver;

/** Railgun
 * 
 * @author Calclavia */
public class TileEntityRailGun extends TileEntityMountableTurret implements IPacketReceiver, IRedstoneReceptor, IMultiBlock
{
    private int gunChargingTicks = 0;

    private boolean redstonePowerOn = false;
    /** Is current ammo antimatter */
    private boolean isAntimatter;

    private float explosionSize;

    private int explosionDepth;

    /** A counter used client side for the smoke and streaming effects of the Railgun after a shot. */
    private int endTicks = 0;

    public TileEntityRailGun()
    {
        this.baseFiringDelay = 80;
        this.minFiringDelay = 50;

        this.maxPitch = 60;
        this.minPitch = -60;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.getPlatform() != null)
        {
            if (this.redstonePowerOn)
            {
                this.tryActivateWeapon();
            }

            if (this.gunChargingTicks > 0)
            {
                this.gunChargingTicks++;

                if (this.gunChargingTicks >= this.getFireDelay())
                {
                    this.onFire();
                    this.gunChargingTicks = 0;
                }
            }

            if (this.worldObj.isRemote && this.endTicks-- > 0)
            {
                MovingObjectPosition objectMouseOver = this.rayTrace(2000);

                if (objectMouseOver != null && objectMouseOver.hitVec != null)
                {
                    this.drawParticleStreamTo(new Vector3(objectMouseOver.hitVec));
                }
            }
        }
    }

    @Override
    public void tryActivateWeapon()
    {
        if (this.canActivateWeapon() && this.gunChargingTicks == 0)
        {
            this.onWeaponActivated();
        }
    }

    @SuppressWarnings("unchecked")
    public void onFire()
    {
        if (!this.worldObj.isRemote)
        {
            while (this.explosionDepth > 0)
            {
                MovingObjectPosition objectMouseOver = this.rayTrace(2000);

                if (objectMouseOver != null)
                {
                    if (!ICBMSentry.isProtected(this.worldObj, new Vector3(objectMouseOver), ICBMSentry.FLAG_RAILGUN))
                    {
                        if (this.isAntimatter)
                        {
                            /** Remove Redmatter Explosions. */
                            int radius = 50;
                            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(objectMouseOver.blockX - radius, objectMouseOver.blockY - radius, objectMouseOver.blockZ - radius, objectMouseOver.blockX + radius, objectMouseOver.blockY + radius, objectMouseOver.blockZ + radius);
                            List<Entity> missilesNearby = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                            for (Entity entity : missilesNearby)
                            {
                                if (entity instanceof IExplosive)
                                {
                                    entity.setDead();
                                }
                            }
                        }

                        Vector3 blockPosition = new Vector3(objectMouseOver.hitVec);

                        int blockID = blockPosition.getBlockID(this.worldObj);
                        Block block = Block.blocksList[blockID];

                        // Any hardness under zero is unbreakable
                        if (block != null && block.getBlockHardness(this.worldObj, blockPosition.intX(), blockPosition.intY(), blockPosition.intZ()) != -1)
                        {
                            this.worldObj.setBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, 0, 0, 2);
                        }

                        Entity responsibleEntity = this.entityFake != null ? this.entityFake.riddenByEntity : null;
                        this.worldObj.newExplosion(responsibleEntity, blockPosition.x, blockPosition.y, blockPosition.z, explosionSize, true, true);
                    }
                }

                this.explosionDepth--;
            }
        }
    }

    @Override
    public void renderShot(Vector3 target)
    {
        this.endTicks = 20;
    }

    @Override
    public void playFiringSound()
    {
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ICBMCore.PREFIX + "railgun", 5F, 1F);
    }

    @Override
    public float getVoltage()
    {
        return 220;
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return new Vector3[] { new Vector3(0, 1, 0) };
    }

    @Override
    public Vector3 getCenter()
    {
        return new Vector3(this).add(new Vector3(0.5, 1.5, 0.5));
    }

    @Override
    public Vector3 getMuzzle()
    {
        return this.getCenter().translate(Vector3.scale(Vector3.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch), 1.6));
    }

    @Override
    public void onPowerOn()
    {
        this.redstonePowerOn = true;
    }

    @Override
    public void onPowerOff()
    {
        this.redstonePowerOn = false;
    }

    @Override
    public float getFiringRequest()
    {
        return 1000000;
    }

    @Override
    public void onWeaponActivated()
    {
        super.onWeaponActivated();
        this.gunChargingTicks = 1;
        this.redstonePowerOn = false;
        this.isAntimatter = false;
        ItemStack ammoStack = this.getPlatform().hasAmmunition(ProjectileType.RAILGUN);

        if (ammoStack != null)
        {
            if (ammoStack.equals(ICBMSentry.antimatterBullet) && this.getPlatform().useAmmunition(ammoStack))
            {
                this.isAntimatter = true;
            }
            else
            {
                this.getPlatform().useAmmunition(ammoStack);
            }
        }

        // TODO: Somehow this energy request method does not work.
        // this.getPlatform().provideElectricity(this.getFiringRequest(), true);
        this.getPlatform().setEnergyStored(0);

        this.explosionSize = 5f;
        this.explosionDepth = 5;

        if (this.isAntimatter)
        {
            this.explosionSize = 8f;
            this.explosionDepth = 10;
        }

        this.playFiringSound();
    }

    @Override
    public boolean canActivateWeapon()
    {
        if (this.getPlatform() != null)
        {
            if (this.getPlatform().hasAmmunition(ProjectileType.RAILGUN) != null)
            {
                if (Math.round(this.getPlatform().provideElectricity(this.getFiringRequest(), false).getWatts()) >= this.getFiringRequest())
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public float addInformation(HashMap<String, Integer> map, EntityPlayer player)
    {
        super.addInformation(map, player);
        return 2;
    }

    @Override
    public int getMaxHealth()
    {
        return 450;
    }
}
