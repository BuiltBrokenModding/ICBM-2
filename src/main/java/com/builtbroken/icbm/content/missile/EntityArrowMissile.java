package com.builtbroken.icbm.content.missile;

import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrowMissile extends Entity implements IProjectile
{
    /** The owner of this arrow. */
    public Entity shootingEntity;
    public Pos sourceOfProjectile;

    //Settings
    protected int inGroundKillTime = 1200;
    protected int inAirKillTime = 1200;
    protected DamageSource impact_damageSource = DamageSource.anvil;

    //In ground data
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inBlockID;
    private int inData;
    private boolean inGround;

    //Timers
    protected int ticksInGround;
    protected int ticksInAir;

    public EntityArrowMissile(World world)
    {
        super(world);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.5F, 0.5F);
    }

    public EntityArrowMissile(World world, double x, double y, double z)
    {
        super(world);
        this.setPosition(x, y, z);
        this.sourceOfProjectile = new Pos(x, y, z);
        this.yOffset = 0.0F;
    }

    public EntityArrowMissile(World world, EntityLivingBase shooter, EntityLivingBase target, float p_i1755_4_, float p_i1755_5_)
    {
        this(world);
        this.shootingEntity = shooter;
        this.sourceOfProjectile = new Pos(shooter);

        this.posY = shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D;
        double d0 = target.posX - shooter.posX;
        double d1 = target.boundingBox.minY + (double)(target.height / 3.0F) - this.posY;
        double d2 = target.posZ - shooter.posZ;
        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D)
        {
            float f2 = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            this.setLocationAndAngles(shooter.posX + d4, this.posY, shooter.posZ + d5, f2, f3);
            this.yOffset = 0.0F;
            float f4 = (float)d3 * 0.2F;
            this.setThrowableHeading(d0, d1 + (double)f4, d2, p_i1755_4_, p_i1755_5_);
        }
    }

    public EntityArrowMissile(World world, EntityLivingBase shooter, float f)
    {
        super(world);
        this.renderDistanceWeight = 10.0D;
        this.shootingEntity = shooter;
        this.sourceOfProjectile = new Pos(shooter);

        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, f * 1.5F, 1.0F);
    }

    @Override
    protected void entityInit()
    {
    }


    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //Update rotation to match motion
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }


        //Get block we are face-palmed into
        Block block = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);

        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        //Handle stuck in ground
        if (this.inGround)
        {
            int j = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            if (block == this.inBlockID && j == this.inData)
            {
                ++this.ticksInGround;

                if (this.ticksInGround == inGroundKillTime)
                {
                    this.setDead();
                    return;
                }
            }
            else
            {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;
            if(ticksInAir >= inAirKillTime)
            {
                this.setDead();
                return;
            }

            //Do raytrace TODO move to prefab entity for reuse
            Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec3, false, true, false);
            vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null)
            {
                vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            //Handle entity collision boxes
            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            int i;
            float f1;

            for (i = 0; i < list.size(); ++i)
            {
                Entity entity1 = (Entity)list.get(i);

                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5))
                {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double)f1, (double)f1, (double)f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            //If we collided with an entity, set hit to entity
            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null)
            {
                //Handle entity hit
                if (movingobjectposition.entityHit != null)
                {
                   handleEntityCollision(movingobjectposition, movingobjectposition.entityHit);
                }
                else //Handle block hit
                {
                    handleBlockCollision(movingobjectposition);
                }
            }
            updateMotion();
        }
    }

    protected void handleBlockCollision(MovingObjectPosition movingobjectposition)
    {
        this.xTile = movingobjectposition.blockX;
        this.yTile = movingobjectposition.blockY;
        this.zTile = movingobjectposition.blockZ;

        this.inBlockID = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
        this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

        this.motionX = (double)((float)(movingobjectposition.hitVec.xCoord - this.posX));
        this.motionY = (double)((float)(movingobjectposition.hitVec.yCoord - this.posY));
        this.motionZ = (double)((float)(movingobjectposition.hitVec.zCoord - this.posZ));

        float velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.posX -= this.motionX / (double)velocity * 0.05000000074505806D;
        this.posY -= this.motionY / (double)velocity * 0.05000000074505806D;
        this.posZ -= this.motionZ / (double)velocity * 0.05000000074505806D;
        //TODO this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;

        if (this.inBlockID.getMaterial() != Material.air)
        {
            this.inBlockID.onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
        }
        onImpactTile();
    }

    protected void onImpactTile()
    {
        this.setDead();
    }

    protected void handleEntityCollision(MovingObjectPosition movingobjectposition, Entity entityHit)
    {
        onImpactEntity(entityHit, MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
    }

    protected void onImpactEntity(Entity entityHit, float velocity)
    {
        int damage = MathHelper.ceiling_double_int((double)velocity * 2);

        //If entity takes damage add velocity to entity
        if (impact_damageSource != null && entityHit.attackEntityFrom(impact_damageSource, (float)damage))
        {
            if (entityHit instanceof EntityLivingBase)
            {
                float vel_horizontal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                if (vel_horizontal > 0.0F)
                {
                    entityHit.addVelocity(this.motionX * 0.6000000238418579D / (double)vel_horizontal, 0.1D, this.motionZ * 0.6000000238418579D / (double)vel_horizontal);
                }
            }

        }
        this.setDead();
    }

    protected void updateMotion()
    {
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        //Decrease motion so the projectile stops
        this.motionX *= 0.99F;
        this.motionY *= 0.99F;
        this.motionZ *= 0.99F;
        //Add gravity so the projectile will fall
        this.motionY -= 0.05F;

        //Set position
        this.setPosition(this.posX, this.posY, this.posZ);

        //Adjust for collision
        this.func_145775_I();
    }


    @Override
    public void setThrowableHeading(double xx, double yy, double zz, float multiplier, float p_70186_8_)
    {
        //Normalize
        float velocity = MathHelper.sqrt_double(xx * xx + yy * yy + zz * zz);
        xx /= (double)velocity;
        yy /= (double)velocity;
        zz /= (double)velocity;

        //Add randomization to make the arrow miss
        xx += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
        yy += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
        zz += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;

        //Add multiplier
        xx *= (double)multiplier;
        yy *= (double)multiplier;
        zz *= (double)multiplier;

        //Set motion
        this.motionX = xx;
        this.motionY = yy;
        this.motionZ = zz;

        //Update rotation
        float f3 = MathHelper.sqrt_double(xx * xx + zz * zz);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(xx, zz) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(yy, (double)f3) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    @Override @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_)
    {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }

    @Override @SideOnly(Side.CLIENT)
    public void setVelocity(double xx, double yy, double zz)
    {
        this.motionX = xx;
        this.motionY = yy;
        this.motionZ = zz;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(xx * xx + zz * zz);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(xx, zz) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(yy, (double)f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        nbt.setShort("xTile", (short) this.xTile);
        nbt.setShort("yTile", (short) this.yTile);
        nbt.setShort("zTile", (short) this.zTile);
        nbt.setShort("life", (short) this.ticksInGround);
        nbt.setByte("inTile", (byte) Block.getIdFromBlock(this.inBlockID));
        nbt.setByte("inData", (byte) this.inData);
        nbt.setByte("inGround", (byte) (this.inGround ? 1 : 0));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        this.xTile = nbt.getShort("xTile");
        this.yTile = nbt.getShort("yTile");
        this.zTile = nbt.getShort("zTile");
        this.ticksInGround = nbt.getShort("life");
        this.inBlockID = Block.getBlockById(nbt.getByte("inTile") & 255);
        this.inData = nbt.getByte("inData") & 255;
        this.inGround = nbt.getByte("inGround") == 1;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    @Override
    public boolean canAttackWithItem()
    {
        return false;
    }
}
