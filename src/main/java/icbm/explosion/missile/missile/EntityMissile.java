package icbm.explosion.missile.missile;

import icbm.Reference;
import icbm.api.ILauncherContainer;
import icbm.api.IMissile;
import icbm.api.IMissileLockable;
import icbm.api.RadarRegistry;
import icbm.api.explosion.ExplosiveType;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.api.sentry.IAATarget;
import icbm.core.ICBMConfiguration;
import icbm.core.ICBMCore;
import icbm.core.implement.IChunkLoadHandler;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.machines.TileEntityCruiseLauncher;
import icbm.explosion.missile.ExplosiveRegistry;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import universalelectricity.api.vector.Vector2;
import universalelectricity.api.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityMissile extends Entity implements IChunkLoadHandler, IMissileLockable, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile, IAATarget
{
    public enum MissileType
    {
        missile,
        CruiseMissile,
        HUO_JIAN
    }

    public static final float JIA_KUAI_SU_DU = 0.012F;

    public int explosiveId = 0;
    public int tianGao = 200;
    public Vector3 targetVector = null;
    public Vector3 startPos = null;
    public Vector3 launcherPos = null;
    public boolean isExpoding = false;

    public int baoZhaGaoDu = 0;
    public int feiXingTick = -1;
    // Difference
    public double xXiangCha;
    public double yXiangCha;
    public double zXiangCha;
    // Flat Distance
    public double diShangJuLi;
    // The flight time in ticks
    public float missileFlightTime;
    // Acceleration
    public float acceleration;
    // Protection Time
    public int baoHuShiJian = 2;

    private Ticket chunkTicket;

    // For anti-ballistic missile
    public Entity lockedTarget;
    // Has this missile lock it's target before?
    public boolean didTargetLockBefore = false;
    // Tracking
    public int genZongE = -1;
    // For cluster missile
    public int daoDanCount = 0;

    public double daoDanGaoDu = 2;

    private boolean setExplode;
    private boolean setNormalExplode;

    // Missile Type
    public MissileType missileType = MissileType.missile;

    public Vector3 xiaoDanMotion = new Vector3();

    private double qiFeiGaoDu = 3;

    // Client side
    protected final IUpdatePlayerListBox shengYin;

    public NBTTagCompound nbtData = new NBTTagCompound();

    public EntityMissile(World par1World)
    {
        super(par1World);
        this.setSize(1F, 1F);
        this.renderDistanceWeight = 3;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
        this.shengYin = this.worldObj != null ? ICBMExplosion.proxy.getDaoDanShengYin(this) : null;
    }

    /** Spawns a traditional missile and cruise missiles
     * 
     * @param explosiveId - Explosive ID
     * @param startPos - Starting Position
     * @param launcherPos - Missile Launcher Position */
    public EntityMissile(World world, Vector3 startPos, Vector3 launcherPos, int explosiveId)
    {
        this(world);
        this.explosiveId = explosiveId;
        this.startPos = startPos;
        this.launcherPos = launcherPos;

        this.setPosition(this.startPos.x, this.startPos.y, this.startPos.z);
        this.setRotation(0, 90);
    }

    /** For rocket launchers
     * 
     * @param explosiveId - Explosive ID
     * @param startPos - Starting Position
     * @param targetVector - Target Position */
    public EntityMissile(World world, Vector3 startPos, int explosiveId, float yaw, float pitch)
    {
        this(world);
        this.explosiveId = explosiveId;
        this.launcherPos = this.startPos = startPos;
        this.missileType = MissileType.HUO_JIAN;
        this.baoHuShiJian = 10;

        this.setPosition(this.startPos.x, this.startPos.y, this.startPos.z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public String getEntityName()
    {
        return ExplosiveRegistry.get(this.explosiveId).getMissileName();
    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data)
    {
        try
        {
            data.writeInt(this.explosiveId);
            data.writeInt(this.missileType.ordinal());

            data.writeDouble(this.startPos.x);
            data.writeDouble(this.startPos.y);
            data.writeDouble(this.startPos.z);

            data.writeInt(this.launcherPos.intX());
            data.writeInt(this.launcherPos.intY());
            data.writeInt(this.launcherPos.intZ());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        try
        {
            this.explosiveId = data.readInt();
            this.missileType = MissileType.values()[data.readInt()];
            this.startPos = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
            this.launcherPos = new Vector3(data.readInt(), data.readInt(), data.readInt());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void launch(Vector3 target)
    {
        this.startPos = new Vector3(this);
        this.targetVector = target;
        this.baoZhaGaoDu = this.targetVector.intY();
        ((Missile) ExplosiveRegistry.get(this.explosiveId)).launch(this);
        this.feiXingTick = 0;
        this.jiSuan();
        this.worldObj.playSoundAtEntity(this, Reference.PREFIX + "missilelaunch", 4F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        // TODO add an event system here
        RadarRegistry.register(this);
        ICBMCore.LOGGER.info("Launching " + this.getEntityName() + " (" + this.entityId + ") from " + startPos.intX() + ", " + startPos.intY() + ", " + startPos.intZ() + " to " + targetVector.intX() + ", " + targetVector.intY() + ", " + targetVector.intZ());
    }

    @Override
    public void launch(Vector3 target, int height)
    {
        this.qiFeiGaoDu = height;
        this.launch(target);
    }

    /** Recalculates required parabolic path for the missile.
     * 
     * @param target */
    public void jiSuan()
    {
        if (this.targetVector != null)
        {
            // Calculate the distance difference of the missile
            this.xXiangCha = this.targetVector.x - this.startPos.x;
            this.yXiangCha = this.targetVector.y - this.startPos.y;
            this.zXiangCha = this.targetVector.z - this.startPos.z;

            // TODO: Calculate parabola and relative out the height.
            // Calculate the power required to reach the target co-ordinates
            // Ground Displacement
            this.diShangJuLi = Vector2.distance(this.startPos.toVector2(), this.targetVector.toVector2());
            // Parabolic Height
            this.tianGao = 160 + (int) (this.diShangJuLi * 3);
            // Flight time
            this.missileFlightTime = (float) Math.max(100, 2 * this.diShangJuLi) - this.feiXingTick;
            // Acceleration
            this.acceleration = (float) this.tianGao * 2 / (this.missileFlightTime * this.missileFlightTime);
        }
    }

    @Override
    public void entityInit()
    {
        this.dataWatcher.addObject(16, -1);
        this.chunkLoaderInit(ForgeChunkManager.requestTicket(ICBMExplosion.instance, this.worldObj, Type.ENTITY));
    }

    @Override
    public void chunkLoaderInit(Ticket ticket)
    {
        if (!this.worldObj.isRemote)
        {
            if (ticket != null)
            {
                if (this.chunkTicket == null)
                {
                    this.chunkTicket = ticket;
                    this.chunkTicket.bindEntity(this);
                    this.chunkTicket.getModData();
                }

                ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.chunkCoordX, this.chunkCoordZ));
            }
        }
    }

    public void updateLoadChunk(int newChunkX, int newChunkZ)
    {
        if (!this.worldObj.isRemote && ICBMConfiguration.ZAI_KUAI && this.chunkTicket != null)
        {
            ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(newChunkX, newChunkZ));
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /** Called to update the entity's position/logic. */
    @Override
    public void onUpdate()
    {
        if (this.shengYin != null)
        {
            this.shengYin.update();
        }

        if (!this.worldObj.isRemote)
        {
            if (ICBMExplosion.shiBaoHu(this.worldObj, new Vector3(this), ExplosiveType.AIR, this.explosiveId))
            {
                if (this.feiXingTick >= 0)
                {
                    this.dropMissileAsItem();
                }

                this.setDead();
                return;
            }
        }

        if (this.setNormalExplode)
        {
            this.normalExplode();
            return;
        }

        if (this.setExplode)
        {
            this.explode();
            return;
        }

        try
        {
            if (this.worldObj.isRemote)
            {
                this.feiXingTick = this.dataWatcher.getWatchableObjectInt(16);
            }
            else
            {
                this.dataWatcher.updateObject(16, this.feiXingTick);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (this.feiXingTick >= 0)
        {
            RadarRegistry.register(this);

            if (!this.worldObj.isRemote)
            {
                if (this.missileType == MissileType.CruiseMissile || this.missileType == MissileType.HUO_JIAN)
                {
                    if (this.feiXingTick == 0 && this.xiaoDanMotion != null)
                    {
                        this.xiaoDanMotion = new Vector3(this.xXiangCha / (missileFlightTime * 0.3), this.yXiangCha / (missileFlightTime * 0.3), this.zXiangCha / (missileFlightTime * 0.3));
                        this.motionX = this.xiaoDanMotion.x;
                        this.motionY = this.xiaoDanMotion.y;
                        this.motionZ = this.xiaoDanMotion.z;
                    }

                    this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

                    // Look at the next point
                    this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

                    ((Missile) ExplosiveRegistry.get(this.explosiveId)).update(this);

                    Block block = Block.blocksList[this.worldObj.getBlockId((int) this.posX, (int) this.posY, (int) this.posZ)];

                    if (this.baoHuShiJian <= 0 && ((block != null && !(block instanceof BlockFluid)) || this.posY > 1000 || this.isCollided || this.feiXingTick > 20 * 1000 || (this.motionX == 0 && this.motionY == 0 && this.motionZ == 0)))
                    {
                        this.explode();
                        return;
                    }

                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                }
                else
                {
                    // Start the launch
                    if (this.qiFeiGaoDu > 0)
                    {
                        this.motionY = JIA_KUAI_SU_DU * this.feiXingTick * (this.feiXingTick / 2);
                        this.motionX = 0;
                        this.motionZ = 0;
                        this.qiFeiGaoDu -= this.motionY;
                        this.moveEntity(this.motionX, this.motionY, this.motionZ);

                        if (this.qiFeiGaoDu <= 0)
                        {
                            this.motionY = this.acceleration * (this.missileFlightTime / 2);
                            this.motionX = this.xXiangCha / missileFlightTime;
                            this.motionZ = this.zXiangCha / missileFlightTime;
                        }
                    }
                    else
                    {
                        this.motionY -= this.acceleration;

                        this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

                        // Look at the next point
                        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

                        ((Missile) ExplosiveRegistry.get(this.explosiveId)).update(this);

                        this.moveEntity(this.motionX, this.motionY, this.motionZ);

                        // If the missile contacts anything, it will explode.
                        if (this.isCollided)
                        {
                            this.explode();
                        }

                        // If the missile is commanded to explode before impact
                        if (this.baoZhaGaoDu > 0 && this.motionY < 0)
                        {
                            // Check the block below it.
                            int blockBelowID = this.worldObj.getBlockId((int) this.posX, (int) this.posY - baoZhaGaoDu, (int) this.posZ);

                            if (blockBelowID > 0)
                            {
                                this.baoZhaGaoDu = 0;
                                this.explode();
                            }
                        }
                    } // end else
                }
            }
            else
            {
                this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);
                // Look at the next point
                this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
            }

            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;

            this.spawnMissileSmoke();
            this.baoHuShiJian--;
            this.feiXingTick++;
        }
        else if (this.missileType != MissileType.HUO_JIAN)
        {
            // Check to find the launcher in which this missile belongs in.
            ILauncherContainer launcher = this.getLauncher();

            if (launcher != null)
            {
                launcher.setContainingMissile(this);

                /** Rotate the missile to the cruise launcher's rotation. */
                if (launcher instanceof TileEntityCruiseLauncher)
                {
                    this.missileType = MissileType.CruiseMissile;
                    this.noClip = true;

                    if (this.worldObj.isRemote)
                    {
                        this.rotationYaw = -((TileEntityCruiseLauncher) launcher).rotationYaw + 90;
                        this.rotationPitch = ((TileEntityCruiseLauncher) launcher).rotationPitch;
                    }

                    this.posY = ((TileEntityCruiseLauncher) launcher).yCoord + 1;
                }
            }
            else
            {
                this.setDead();
            }
        }

        super.onUpdate();
    }

    @Override
    public ILauncherContainer getLauncher()
    {
        if (this.launcherPos != null)
        {
            TileEntity tileEntity = this.launcherPos.getTileEntity(this.worldObj);

            if (tileEntity != null && tileEntity instanceof ILauncherContainer)
            {
                if (!tileEntity.isInvalid())
                {
                    return (ILauncherContainer) tileEntity;
                }
            }
        }

        return null;
    }

    @Override
    public boolean interactFirst(EntityPlayer entityPlayer)
    {
        if (((Missile) ExplosiveRegistry.get(this.explosiveId)) != null)
        {
            if (((Missile) ExplosiveRegistry.get(this.explosiveId)).onInteract(this, entityPlayer))
            {
                return true;
            }
        }

        if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == entityPlayer))
        {
            entityPlayer.mountEntity(this);
            return true;
        }

        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        if (this.missileFlightTime <= 0 && this.missileType == MissileType.missile)
        {
            return this.height;
        }
        else if (this.missileType == MissileType.CruiseMissile)
        {
            return this.height * 0.1;
        }

        return this.height / 2 + this.motionY;
    }

    private void spawnMissileSmoke()
    {
        if (this.worldObj.isRemote)
        {
            Vector3 position = new Vector3(this);
            // The distance of the smoke relative
            // to the missile.
            double distance = -this.daoDanGaoDu - 0.2f;
            Vector3 delta = new Vector3();
            // The delta Y of the smoke.
            delta.y = Math.sin(Math.toRadians(this.rotationPitch)) * distance;
            // The horizontal distance of the
            // smoke.
            double dH = Math.cos(Math.toRadians(this.rotationPitch)) * distance;
            // The delta X and Z.
            delta.x = Math.sin(Math.toRadians(this.rotationYaw)) * dH;
            delta.z = Math.cos(Math.toRadians(this.rotationYaw)) * dH;

            position.add(delta);
            this.worldObj.spawnParticle("flame", position.x, position.y, position.z, 0, 0, 0);
            ICBMExplosion.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.scale(1 - 0.001 * Math.random());
            ICBMExplosion.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.scale(1 - 0.001 * Math.random());
            ICBMExplosion.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.scale(1 - 0.001 * Math.random());
            ICBMExplosion.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
        }
    }

    /** Checks to see if and entity is touching the missile. If so, blow up! */

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        // Make sure the entity is not an item
        if (!(entity instanceof EntityItem) && entity != this.riddenByEntity && this.baoHuShiJian <= 0)
        {
            if (entity instanceof EntityMissile)
            {
                ((EntityMissile) entity).setNormalExplode();
            }

            this.setExplode();
        }

        return null;
    }

    @Override
    public Vector3 getPredictedPosition(int t)
    {
        Vector3 guJiDiDian = new Vector3(this);
        double tempMotionY = this.motionY;

        if (this.feiXingTick > 20)
        {
            for (int i = 0; i < t; i++)
            {
                if (this.missileType == MissileType.CruiseMissile || this.missileType == MissileType.HUO_JIAN)
                {
                    guJiDiDian.x += this.xiaoDanMotion.x;
                    guJiDiDian.y += this.xiaoDanMotion.y;
                    guJiDiDian.z += this.xiaoDanMotion.z;
                }
                else
                {
                    guJiDiDian.x += this.motionX;
                    guJiDiDian.y += tempMotionY;
                    guJiDiDian.z += this.motionZ;

                    tempMotionY -= this.acceleration;
                }
            }
        }

        return guJiDiDian;
    }

    @Override
    public void setNormalExplode()
    {
        this.setNormalExplode = true;
    }

    @Override
    public void setExplode()
    {
        this.setExplode = true;
    }

    @Override
    public void setDead()
    {
        RadarRegistry.unregister(this);

        if (this.chunkTicket != null)
        {
            ForgeChunkManager.releaseTicket(this.chunkTicket);
        }

        super.setDead();

        if (this.shengYin != null)
        {
            this.shengYin.update();
        }
    }

    @Override
    public void explode()
    {
        try
        {
            // Make sure the missile is not already exploding
            if (!this.isExpoding)
            {
                if (this.explosiveId == 0)
                {
                    if (!this.worldObj.isRemote)
                    {
                        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F, true);
                    }
                }
                else
                {
                    ((Missile) ExplosiveRegistry.get(this.explosiveId)).createExplosion(this.worldObj, this.posX, this.posY, this.posZ, this);
                }

                this.isExpoding = true;

                ICBMCore.LOGGER.info(this.getEntityName() + " (" + this.entityId + ") exploded in " + (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ);
            }

            this.setDead();

        }
        catch (Exception e)
        {
            ICBMCore.LOGGER.severe("Missile failed to explode properly. Report this to the developers.");
            e.printStackTrace();
        }
    }

    @Override
    public void normalExplode()
    {
        if (!this.isExpoding)
        {
            this.isExpoding = true;

            if (!this.worldObj.isRemote)
            {
                this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F, true);
            }

            this.setDead();
        }
    }

    @Override
    public void dropMissileAsItem()
    {
        if (!this.isExpoding && !this.worldObj.isRemote)
        {
            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ICBMExplosion.itemMissile, 1, this.explosiveId));

            float var13 = 0.05F;
            Random random = new Random();
            entityItem.motionX = ((float) random.nextGaussian() * var13);
            entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
            entityItem.motionZ = ((float) random.nextGaussian() * var13);
            this.worldObj.spawnEntityInWorld(entityItem);
        }

        this.setDead();
    }

    /** (abstract) Protected helper method to read subclass entity data from NBT. */
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        this.startPos = Vector3.readFromNBT(nbt.getCompoundTag("kaiShi"));
        this.targetVector = Vector3.readFromNBT(nbt.getCompoundTag("muBiao"));
        this.launcherPos = Vector3.readFromNBT(nbt.getCompoundTag("faSheQi"));
        this.acceleration = nbt.getFloat("jiaSu");
        this.baoZhaGaoDu = nbt.getInteger("baoZhaGaoDu");
        this.explosiveId = nbt.getInteger("haoMa");
        this.feiXingTick = nbt.getInteger("feiXingTick");
        this.qiFeiGaoDu = nbt.getDouble("qiFeiGaoDu");
        this.missileType = MissileType.values()[nbt.getInteger("xingShi")];
        this.nbtData = nbt.getCompoundTag("data");
    }

    /** (abstract) Protected helper method to write subclass entity data to NBT. */
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        if (this.startPos != null)
        {
            nbt.setCompoundTag("kaiShi", this.startPos.writeToNBT(new NBTTagCompound()));
        }
        if (this.targetVector != null)
        {
            nbt.setCompoundTag("muBiao", this.targetVector.writeToNBT(new NBTTagCompound()));
        }

        if (this.launcherPos != null)
        {
            nbt.setCompoundTag("faSheQi", this.launcherPos.writeToNBT(new NBTTagCompound()));
        }

        nbt.setFloat("jiaSu", this.acceleration);
        nbt.setInteger("haoMa", this.explosiveId);
        nbt.setInteger("baoZhaGaoDu", this.baoZhaGaoDu);
        nbt.setInteger("feiXingTick", this.feiXingTick);
        nbt.setDouble("qiFeiGaoDu", this.qiFeiGaoDu);
        nbt.setInteger("xingShi", this.missileType.ordinal());
        nbt.setTag("data", this.nbtData);
    }

    @Override
    public float getShadowSize()
    {
        return 1.0F;
    }

    @Override
    public int getTicksInAir()
    {
        return this.feiXingTick;
    }

    @Override
    public IExplosive getExplosiveType()
    {
        return ExplosiveRegistry.get(this.explosiveId);
    }

    @Override
    public boolean canLock(IMissile missile)
    {
        return this.feiXingTick > 0;
    }

    @Override
    public void destroyCraft()
    {
        this.normalExplode();
    }

    @Override
    public int doDamage(int damage)
    {
        return -1;
    }

    @Override
    public boolean canBeTargeted(Object turret)
    {
        return this.getTicksInAir() > 0;
    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        return this.nbtData;
    }
}