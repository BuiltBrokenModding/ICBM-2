package icbm.content.missile;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import icbm.ICBM;
import icbm.IChunkLoadHandler;
import icbm.Reference;
import icbm.Settings;
import icbm.api.IMissile;
import icbm.content.ItemSaveUtil;
import icbm.explosion.DamageUtility;
import icbm.explosion.ExplosiveRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fluids.IFluidBlock;
import icbm.api.explosion.*;
import resonant.api.map.RadarRegistry;
import resonant.lib.transform.vector.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @Author - Calclavia
 */
public class EntityMissile extends Entity implements IChunkLoadHandler, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile
{
    public enum MissileType
    {
        GUIDED,
        DUMMY;
    }

    public static final float SPEED = 0.012F;
    public final List<ChunkCoordIntPair> loadedChunks = new ArrayList<ChunkCoordIntPair>();

    public String explosiveID = null;
    public int maxHeight = 200;
    public Vector3 targetVector = null;
    public Vector3 startPos = null;
    public Vector3 launcherPos = null;

    public int targetHeight = 0;
    public int ticksInAir = -1;
    // Difference
    public double deltaPathX;
    public double deltaPathY;
    public double deltaPathZ;
    // Flat Distance
    public double flatDistance;
    // The flight time in ticks
    public float missileFlightTime;
    // Acceleration
    public float acceleration;
    // Hp
    public float damage = 0;
    public float max_damage = 10;

    private Ticket chunkTicket;

    public double daoDanGaoDu = 2;

    // Missile Type
    public MissileType missileType = MissileType.DUMMY;

    public Vector3 motionVector = new Vector3();

    private double missileLockVertTicks = 3;

    // Used for the rocket launcher preventing the players from killing themselves.
    private final HashSet<Entity> ignoreEntity = new HashSet<Entity>();


    public NBTTagCompound nbtData = new NBTTagCompound();

    public EntityMissile(World par1World)
    {
        super(par1World);
        this.setSize(1F, 1F);
        this.renderDistanceWeight = 3;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
    }

    /**
     * Spawns a traditional missile and cruise missiles
     *
     * @param explosiveId - Explosive ID
     * @param startPos    - Starting Position
     * @param launcherPos - Missile Launcher Position
     */
    public EntityMissile(World world, Vector3 startPos, Vector3 launcherPos, String explosiveId)
    {
        this(world);
        this.explosiveID = explosiveId;
        this.startPos = startPos;
        this.launcherPos = launcherPos;

        this.setPosition(this.startPos.x(), this.startPos.y(), this.startPos.z());
        this.setRotation(0, 90);
    }

    /**
     * For rocket launchers
     *
     * @param explosiveId - Explosive ID
     * @param startPos    - Starting Position
     * @param yaw         - The yaw of the missle
     * @param pitch       - the pitch of the missle
     */
    public EntityMissile(World world, Vector3 startPos, String explosiveId, float yaw, float pitch)
    {
        this(world);
        this.explosiveID = explosiveId;
        this.launcherPos = this.startPos = startPos;
        this.missileType = MissileType.GUIDED;

        this.setPosition(this.startPos.x(), this.startPos.y(), this.startPos.z());
        this.setRotation(yaw, pitch);
    }

    /** Fires a missile from the entity using its facing direction and location. For more
     * complex launching options create your own implementation.
     *
     * @param entity - entity that is firing the missile, most likely a player with a launcher
     * @param missile - item stack that represents the missile plus explosive settings to fire
     */
    public static void fireMissileByEntity(EntityLivingBase entity, ItemStack missile)
    {
        World world = entity.worldObj;
        Vector3 launcher = new Vector3(entity).add(new Vector3(0, 0.5, 0));
        Vector3 playerAim = new Vector3(entity.getLook(1));
        Vector3 start = launcher.add(playerAim.multiply(1.1));
        Vector3 target = launcher.add(playerAim.multiply(100));

        EntityMissile entityMissile = new EntityMissile(world, start, ItemSaveUtil.getExplosive(missile).getUnlocalizedName(), -entity.rotationYaw, -entity.rotationPitch);
        entity.worldObj.spawnEntityInWorld(entityMissile);

        if (entity instanceof EntityPlayer && entity.isSneaking())
        {
            entity.mountEntity(entityMissile);
            entity.setSneaking(false);
        }

        entityMissile.ignore(entity);
        entityMissile.launch(target);

        //Player audio effect
        world.playSoundAtEntity(entityMissile, Reference.PREFIX + "missilelaunch", 4F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

    }

    @Override
    public String getCommandSenderName()
    {
        return ExplosiveRegistry.get(this.explosiveID).toString();
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        try
        {
            ByteBufUtils.writeUTF8String(data, this.explosiveID);
            data.writeInt(this.missileType.ordinal());

            data.writeDouble(this.startPos.x());
            data.writeDouble(this.startPos.y());
            data.writeDouble(this.startPos.z());

            data.writeInt(this.launcherPos.xi());
            data.writeInt(this.launcherPos.yi());
            data.writeInt(this.launcherPos.zi());

            data.writeFloat(rotationYaw);
            data.writeFloat(rotationPitch);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        try
        {
            this.explosiveID = ByteBufUtils.readUTF8String(data);
            this.missileType = MissileType.values()[data.readInt()];
            this.startPos = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
            this.launcherPos = new Vector3(data.readInt(), data.readInt(), data.readInt());

            rotationYaw = data.readFloat();
            rotationPitch = data.readFloat();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Tells the missile to launch at the location
     * @param target - location to aim for, ignored for Dumb fired missiles */
    public void launch(Vector3 target)
    {
        this.startPos = new Vector3(this);
        this.targetVector = target;
        this.targetHeight = this.targetVector.yi();
        this.ticksInAir = 0;

        if(missileType == MissileType.GUIDED)
            this.recalculatePath();
        // TODO add an event system here

        //TODO RadarRegistry.register(this);
        //TODO Reference.LOGGER.info("Launching " + this.getEntityName() + " (" + this.getEntityId() + ") from " + startPos.xi() + ", " + startPos.yi() + ", " + startPos.zi() + " to " + targetVector.xi() + ", " + targetVector.yi() + ", " + targetVector.zi());
    }

    public void launch(Vector3 target, int height)
    {
        this.missileLockVertTicks = height;
        this.launch(target);
    }

    public EntityMissile ignore(Entity entity)
    {
        ignoreEntity.add(entity);
        return this;
    }

    /**
     * Recalculates required parabolic path for the missile Registry
     */
    public void recalculatePath()
    {
        if (this.targetVector != null)
        {
            // Calculate the distance difference of the missile
            this.deltaPathX = this.targetVector.x() - this.startPos.x();
            this.deltaPathY = this.targetVector.y() - this.startPos.y();
            this.deltaPathZ = this.targetVector.z() - this.startPos.z();

            // TODO: Calculate parabola and relative out the height.
            // Calculate the power required to reach the target co-ordinates
            // Ground Displacement TODO check if this is correct as the original method calls no longer exist
            this.flatDistance = this.startPos.subtract(this.targetVector).magnitude();
            // Parabolic Height
            this.maxHeight = 160 + (int) (this.flatDistance * 3);
            // Flight time
            this.missileFlightTime = (float) Math.max(100, 2 * this.flatDistance) - this.ticksInAir;
            // Acceleration
            this.acceleration = (float) this.maxHeight * 2 / (this.missileFlightTime * this.missileFlightTime);
        }
    }

    @Override
    public void entityInit()
    {
        this.dataWatcher.addObject(16, -1);
        this.dataWatcher.addObject(17, 0);
        this.chunkLoaderInit(ForgeChunkManager.requestTicket(ICBM.INSTANCE, this.worldObj, Type.ENTITY));
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
        if (!this.worldObj.isRemote && Settings.LOAD_CHUNKS && this.chunkTicket != null)
        {
            for (ChunkCoordIntPair chunk : loadedChunks)
                ForgeChunkManager.unforceChunk(chunkTicket, chunk);

            loadedChunks.clear();
            loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ + 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ - 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ - 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ + 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ + 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ - 1));

            for (ChunkCoordIntPair chunk : loadedChunks)
                ForgeChunkManager.forceChunk(chunkTicket, chunk);

        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        if (this.worldObj.isRemote)
        {
            this.ticksInAir = this.dataWatcher.getWatchableObjectInt(16);
        }
        else
        {
            this.dataWatcher.updateObject(16, ticksInAir);
        }

        if (this.ticksInAir >= 0)
        {
            RadarRegistry.register(this);

            if (!this.worldObj.isRemote)
            {
                if (this.missileType == MissileType.DUMMY)
                {
                    //Move the missile
                    if (this.ticksInAir == 0 && this.motionVector != null)
                    {
                        this.motionVector = new Vector3(this.deltaPathX / (missileFlightTime * 0.3), this.deltaPathY / (missileFlightTime * 0.3), this.deltaPathZ / (missileFlightTime * 0.3));
                        this.motionX = this.motionVector.x();
                        this.motionY = this.motionVector.y();
                        this.motionZ = this.motionVector.z();
                    }
                }
                else
                {
                    // Locks the change in X & Z until the min Y has been achieved
                    if (this.missileLockVertTicks > 0)
                    {
                        this.motionY = SPEED * this.ticksInAir * (this.ticksInAir / 2);
                        this.missileLockVertTicks -= this.motionY;
                    }
                    else
                    {
                        this.motionY = this.acceleration * (this.missileFlightTime / 2);
                        this.motionX = this.deltaPathX / missileFlightTime;
                        this.motionZ = this.deltaPathZ / missileFlightTime;
                        this.motionY -= this.acceleration;

                    }
                }
            }

            // Set rotation to face motion vector
            this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;

            this.spawnMissileSmoke();
            this.ticksInAir++;
        }

        super.onUpdate();
    }

    @Override
    public boolean interactFirst(EntityPlayer entityPlayer)
    {
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
        return height / 2 + motionY;
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
            delta.y_$eq(Math.sin(Math.toRadians(this.rotationPitch)) * distance);
            // The horizontal distance of the
            // smoke.
            double dH = Math.cos(Math.toRadians(this.rotationPitch)) * distance;
            // The delta X and Z.
            delta.x_$eq(Math.sin(Math.toRadians(this.rotationYaw)) * dH);
            delta.z_$eq(Math.cos(Math.toRadians(this.rotationYaw)) * dH);

            position.add(delta);
            this.worldObj.spawnParticle("flame", position.x(), position.y(), position.z(), 0, 0, 0);
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.multiply(1 - 0.001 * Math.random());
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.multiply(1 - 0.001 * Math.random());
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.multiply(1 - 0.001 * Math.random());
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
        }
    }

    /**
     * Checks to see if and entity is touching the missile. If so, blow up!
     */
    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        if (ignoreEntity.contains(entity))
            return null;

        return super.getCollisionBox(entity);
    }


    public Vector3 getPredictedPosition(int t)
    {
        Vector3 newPos = new Vector3(this);
        double tempMotionY = this.motionY;

        if (this.ticksInAir > 20)
        {
            for (int i = 0; i < t; i++)
            {
                if (this.missileType == MissileType.DUMMY)
                {
                    newPos.addEquals(motionVector);
                }
                else
                {
                    newPos.addEquals(this.motionX, tempMotionY, this.motionZ);

                    tempMotionY -= this.acceleration;
                }
            }
        }

        return newPos;
    }

    @Override
    public void setDead()
    {
        RadarRegistry.unregister(this);

        if (chunkTicket != null)
        {
            ForgeChunkManager.releaseTicket(chunkTicket);
        }

        super.setDead();
    }

    public void dropMissileAsItem()
    {
        ItemStack stack = ((ItemMissile) ICBM.itemMissile).getStackFor(explosiveID);
        if (!this.worldObj.isRemote && stack != null)
        {
            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, stack);

            float var13 = 0.05F;
            Random random = new Random();
            entityItem.motionX = ((float) random.nextGaussian() * var13);
            entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
            entityItem.motionZ = ((float) random.nextGaussian() * var13);
            this.worldObj.spawnEntityInWorld(entityItem);
        }

        this.setDead();
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        this.startPos = new Vector3(nbt.getCompoundTag("startPos"));
        this.targetVector = new Vector3(nbt.getCompoundTag("targetPos"));
        this.launcherPos = new Vector3(nbt.getCompoundTag("launcherPos"));
        this.acceleration = nbt.getFloat("acceleration");
        this.targetHeight = nbt.getInteger("targetHeight");
        this.explosiveID = nbt.getString("explosiveString");
        this.ticksInAir = nbt.getInteger("ticksInAir");
        this.missileLockVertTicks = nbt.getDouble("missileLockVertTicks");
        this.missileType = MissileType.values()[nbt.getInteger("missileType")];
        this.nbtData = nbt.getCompoundTag("data");
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        if (this.startPos != null)
        {
            nbt.setTag("startPos", this.startPos.writeNBT(new NBTTagCompound()));
        }
        if (this.targetVector != null)
        {
            nbt.setTag("targetPos", this.targetVector.writeNBT(new NBTTagCompound()));
        }

        if (this.launcherPos != null)
        {
            nbt.setTag("launcherPos", this.launcherPos.writeNBT(new NBTTagCompound()));
        }

        nbt.setFloat("acceleration", this.acceleration);
        nbt.setString("explosiveString", this.explosiveID);
        nbt.setInteger("targetHeight", this.targetHeight);
        nbt.setInteger("ticksInAir", this.ticksInAir);
        nbt.setDouble("missileLockVertTicks", this.missileLockVertTicks);
        nbt.setInteger("missileType", this.missileType.ordinal());
        nbt.setTag("data", this.nbtData);
    }

    @Override
    public float getShadowSize()
    {
        return 1.0F;
    }

    @Override
    public IExplosive getExplosive()
    {
        return ExplosiveRegistry.get(this.explosiveID);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (DamageUtility.canHarm(this, source, damage))
        {
            this.damage += damage;
            if (this.damage >= this.max_damage)
            {
                onMissileDestroyedByDamage(source, damage);
                this.setDead();
            }
            return true;
        }
        return false;
    }

    /**
     * Called when he missile is killed from a damage source
     * @param source
     * @param damage
     */
    public void onMissileDestroyedByDamage(DamageSource source, float damage)
    {

    }

    public void onCollideWith()
    {

    }

    /** Called when the missile hits a block
     *
     * @param block - block hit
     * @param x - xCoord of block
     * @param y - xCoord of block
     * @param z  - xCoord of block
     * @return true if the missile should stop
     */
    public boolean onCollideWithBlock(Block block, int x, int y, int z)
    {
        triggerImpact();
        return true;
    }

    /** Called when the missile hit something and stopped */
    public void triggerImpact()
    {

    }

    /** Called when the missile has stopped moving */
    public void onStopped()
    {
        triggerImpact();
    }
}