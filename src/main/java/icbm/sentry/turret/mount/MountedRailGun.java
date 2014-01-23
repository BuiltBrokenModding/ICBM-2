package icbm.sentry.turret.mount;

import icbm.Reference;
import icbm.sentry.turret.TileSentry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IMultiBlock;
import calclavia.lib.prefab.tile.IRedstoneReceptor;

/** Railgun
 * 
 * @author Calclavia */
public class MountedRailGun extends MountedSentry implements IRedstoneReceptor, IMultiBlock
{
    private int gunChargingTicks = 0;

    private boolean redstonePowerOn = false;
    /** Is current ammo antimatter */
    private boolean isAntimatter;

    private float explosionSize;

    private int explosionDepth;

    /** A counter used client side for the smoke and streaming effects of the Railgun after a shot. */
    private int endTicks = 0;

    public MountedRailGun(TileSentry sentry)
    {
        super(sentry);
        this.host.getPitchServo().setLimits(60, -60);
    }

    @SuppressWarnings("unchecked")
    public void onFire()
    {
        if (!this.host.worldObj.isRemote)
        {
            while (this.explosionDepth > 0)
            {
                MovingObjectPosition objectMouseOver = this.rayTrace(2000);

                if (objectMouseOver != null)
                {

                    if (this.isAntimatter)
                    {
                        /** Remove Redmatter Explosions. */
                        int radius = 50;
                        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(objectMouseOver.blockX - radius, objectMouseOver.blockY - radius, objectMouseOver.blockZ - radius, objectMouseOver.blockX + radius, objectMouseOver.blockY + radius, objectMouseOver.blockZ + radius);
                        List<Entity> missilesNearby = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                        // for (Entity entity : missilesNearby)
                        // {
                        // if (entity instanceof IExplosive)
                        // {
                        // entity.setDead();
                        // }
                        // }
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

                this.explosionDepth--;
            }
        }
    }

    public void renderShot(Vector3 target)
    {
        this.endTicks = 20;
    }

    public void playFiringSound()
    {
        this.host.worldObj.playSoundEffect(this.host.xCoord, this.host.yCoord, this.host.zCoord, Reference.PREFIX + "railgun", 5F, 1F);
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return new Vector3[] { new Vector3(0, 1, 0) };
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

    public long getFiringRequest()
    {
        return 10000;
    }
}
