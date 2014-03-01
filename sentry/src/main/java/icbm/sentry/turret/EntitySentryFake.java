package icbm.sentry.turret;

import icbm.core.ICBMCore;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.mount.MountedSentry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Entity that fakes being the sentry gun so that the player can ride the
 * sentry. This entity also handles most damage interaction with other entities.
 * 
 * @Author DarkGuardsman
 */
public class EntitySentryFake extends EntityLiving
{
    private TileTurret sentryHost;
    private boolean shouldSit = false;
    
    public EntitySentryFake(World world)
    {
        super(world);
    }

    public EntitySentryFake(TileTurret controller, boolean sit)
    {
        this(controller.worldObj);
        this.isImmuneToFire = true;
        this.setPosition(controller.xCoord, controller.yCoord, controller.zCoord);
        this.sentryHost = controller;
        this.shouldSit = sit;
    }

    @Override
    public boolean attackEntityFrom (DamageSource source, float amount)
    {
        return true;

    }

    @Override
    public boolean isPotionApplicable (PotionEffect par1PotionEffect)
    {
        return false;
    }

    @Override
    public String getEntityName ()
    {
        return "Seat";
    }

    /** Called to update the entity's position/logic. */
    @Override
    public void onUpdate ()
    {
        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        if (this.sentryHost == null || this.sentryHost.isInvalid())
        {
            this.setDead();
            return;
        }

        if (this.sentryHost instanceof TileTurret)
        {
            if (((TileTurret) this.sentryHost).getSentry() instanceof MountedSentry)
                ((TileTurret) this.sentryHost).setFakeEntity(this);
        }

        if (this.worldObj.isRemote && this.riddenByEntity != null)
        {
            this.riddenByEntity.updateRiderPosition();
        }
        // TODO adjust for center of sentry
        this.setPosition(this.sentryHost.xCoord + 0.5, this.sentryHost.yCoord, this.sentryHost.zCoord + 0.5);
    }

    @Override
    public void updateRiderPosition ()
    {
        // TODO cache this value and only update it when the yaw || pitch
        // changes
        if (this.riddenByEntity != null)
        {
            Vector3 vec = new Vector3(this.sentryHost);
            Vector3 offset = new Vector3(0, 0, 1);
            vec.translate(this.sentryHost.getSentry().getCenterOffset());
            if (this.sentryHost.getSentry() instanceof MountedSentry)
            {
                offset = ((MountedSentry) this.sentryHost.getSentry()).getRiderOffset();
            }
            offset.rotate(this.sentryHost.getYawServo().getRotation(), this.sentryHost.getPitchServo().getRotation());
            vec.add(offset);
            this.riddenByEntity.setPosition(vec.x, vec.y, vec.z);
            this.riddenByEntity.rotationYaw = this.sentryHost.getYawServo().getRotation();
            this.riddenByEntity.rotationPitch = this.sentryHost.getPitchServo().getRotation();
        }
    }

    @Override
    public double getMountedYOffset ()
    {
        if (this.sentryHost.getSentry() instanceof MountedSentry)
            return ((MountedSentry) this.sentryHost.getSentry()).getRiderOffset().y;
        else
            return -0.5;
    }

    @Override
    public boolean shouldRiderSit ()
    {
        return this.shouldSit;
    }

    @Override
    protected boolean canTriggerWalking ()
    {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBox (Entity par1Entity)
    {
        return AxisAlignedBB.getBoundingBox(this.posX - .6, this.posY - .6, this.posZ - .6, this.posX + .6, this.posY + .6, this.posZ + .6);
    }

    @Override
    public boolean canBeCollidedWith ()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderVec3D (Vec3 par1Vec3)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist (double par1)
    {
        return false;
    }

    @Override
    public void setVelocity (double par1, double par3, double par5)
    {

    }

    @Override
    public boolean isInsideOfMaterial (Material par1Material)
    {
        return false;
    }

    @Override
    public boolean interact (EntityPlayer player)
    {
        if (this.sentryHost != null && player != null)
        {
            Block block = Block.blocksList[this.worldObj.getBlockId(this.sentryHost.xCoord, this.sentryHost.yCoord, this.sentryHost.zCoord)];
            if (block != null)
            {
                return block.onBlockActivated(this.sentryHost.worldObj, this.sentryHost.xCoord, this.sentryHost.yCoord, this.sentryHost.zCoord, player, 0, 0, 0, 0);
            }
        }
        return false;
    }

}