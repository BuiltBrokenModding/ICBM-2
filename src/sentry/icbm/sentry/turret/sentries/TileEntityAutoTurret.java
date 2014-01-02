package icbm.sentry.turret.sentries;

import icbm.api.sentry.IAATarget;
import icbm.sentry.IAmmunition;
import icbm.sentry.IAutoSentry;
import icbm.sentry.ICBMSentry;
import icbm.sentry.ProjectileType;
import icbm.sentry.damage.TileDamageSource;
import icbm.sentry.task.TaskManager;
import icbm.sentry.task.TaskSearchTarget;
import icbm.sentry.turret.TileEntityTurret;
import icbm.sentry.turret.upgrades.ItemSentryUpgrade.TurretUpgradeType;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.Vector3;

import com.builtbroken.minecraft.network.PacketHandler;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

/** Extend this class for all turrets that are automatic.
 * 
 * @author Rseifert */
public abstract class TileEntityAutoTurret extends TileEntityTurret implements IAutoSentry
{
    /** CURRENT TARGET TO ATTACK */
    public Entity target;

    /** SHOULD TARGET PLAYERS */
    public boolean targetPlayers = false;
    /** SHOULD TARGET FLYING OBJECTS -> MISSILES, PLANES */
    public boolean targetAir = false;
    /** SHOULD TARGET MONSTERS */
    public boolean targetHostile = false;
    /** SHOULD TARGET ANIMALS, NPCS, SHEEP :( */
    public boolean targetFriendly = false;
    public boolean canTargetAir = false;

    /** AI MANAGER */
    public final TaskManager taskManager = new TaskManager(this);

    /** DEFAULT TARGETING RANGE */
    public int baseTargetRange = 20;

    /** MAX TARGETING RANGE */
    public int maxTargetRange = 90;

    /** IDLE ROTATION SPEED */
    public float rotationSpeed = 3;

    /** MAIN AMMO TYPE */
    public ProjectileType projectileType = ProjectileType.CONVENTIONAL;

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput dataStream, Player player)
    {
        if (!super.simplePacket(id, dataStream, player))
        {
            if (id.equalsIgnoreCase(TurretPacketType.SHOT.name()))
            {
                this.renderShot(new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble()));
                this.currentRotationPitch = dataStream.readFloat();
                this.currentRotationYaw = dataStream.readFloat();
                this.playFiringSound();
                return true;
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote && this.isRunning())
        {
            this.taskManager.onUpdate();

            if (!this.taskManager.hasTasks())
            {
                this.taskManager.addTask(new TaskSearchTarget());
            }
        }
    }

    @Override
    public float getRotationSpeed()
    {
        return this.rotationSpeed;
    }

    @Override
    public AxisAlignedBB getTargetingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), this.yCoord - 5, zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + 5, zCoord + this.getDetectRange());
    }

    @Override
    public Entity getTarget()
    {
        return this.target;
    }

    @Override
    public void setTarget(Entity target)
    {
        this.target = target;
    }

    @Override
    public boolean isValidTarget(Entity entity)
    {
        if (entity != null)
        {
            if (!entity.isDead && !entity.isEntityInvulnerable())
            {
                if (this.getCenter().distance(new Vector3(entity)) <= this.getDetectRange())
                {
                    float[] rotations = this.lookHelper.getDeltaRotations(new Vector3(entity).translate(new Vector3(0, entity.getEyeHeight(), 0)));

                    if ((rotations[1] <= this.maxPitch && rotations[1] >= this.minPitch) || this.allowFreePitch)
                    {
                        if (this.lookHelper.canEntityBeSeen(entity))
                        {
                            if (this.targetAir && this.canTargetAir)
                            {
                                if (this.isAir(entity))
                                {
                                    return true;
                                }
                            }

                            if (this.targetPlayers)
                            {
                                if (entity instanceof EntityPlayer || entity.riddenByEntity instanceof EntityPlayer)
                                {
                                    EntityPlayer player;

                                    if (entity.riddenByEntity instanceof EntityPlayer)
                                    {
                                        player = (EntityPlayer) entity.riddenByEntity;
                                    }
                                    else
                                    {
                                        player = ((EntityPlayer) entity);
                                    }

                                    if (!player.capabilities.isCreativeMode)
                                    {
                                        if (this.getPlatform() != null && this.getPlatform().getUserAccess(player.username) != null)
                                        {
                                            return true;
                                        }
                                    }
                                }
                            }

                            if (this.targetHostile)
                            {
                                if (entity instanceof IMob && !this.isAir(entity))
                                {
                                    return true;
                                }
                            }

                            if (this.targetFriendly)
                            {
                                if ((entity instanceof IAnimals || entity instanceof INpc || entity instanceof IMerchant) && !this.isAir(entity))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean isAir(Entity entity)
    {
        return (entity instanceof IMob && entity instanceof EntityFlying) || (entity instanceof IAATarget && ((IAATarget) entity).canBeTargeted(this)) || entity instanceof EntityWither || entity instanceof EntityDragon;
    }

    @Override
    public boolean canActivateWeapon()
    {
        if (this.isValidTarget(this.getTarget()) && this.getPlatform() != null)
        {
            if (this.lookHelper.isLookingAt(this.target, 5))
            {
                return this.tickSinceFired == 0 && (this.getPlatform().hasAmmunition(this.projectileType) != null || this.projectileType == ProjectileType.UNKNOWN);
            }
        }

        return false;
    }

    /** Sends the firing info to the client to render tracer effects */
    public void sendShotToClient(Vector3 position)
    {
        PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(ICBMSentry.CHANNEL, TurretPacketType.SHOT.ordinal(), this, position.x, position.y, position.z, this.currentRotationPitch, this.currentRotationYaw), this.worldObj, new Vector3(this), 40);
    }

    /** Server side only */
    @Override
    public void onWeaponActivated()
    {
        super.onWeaponActivated();

        if (!this.worldObj.isRemote)
        {
            if (this.onFire())
            {
                this.sendShotToClient(this.getTargetPosition());
                this.playFiringSound();
                //this.getPlatform().provideElectricity(this.getFiringRequest(), true);
            }
        }
    }

    @Override
    public void renderShot(Vector3 target)
    {
        this.drawParticleStreamTo(target);
    }

    /** Does the actual firing process for the sentry */
    protected boolean onFire()
    {
        if (!this.worldObj.isRemote)
        {
            ItemStack ammoStack = this.getPlatform().hasAmmunition(this.projectileType);

            if (this.getPlatform() != null && (ammoStack != null || this.projectileType == ProjectileType.UNKNOWN))
            {
                boolean fired = false;
                IAmmunition bullet = null;

                if (ammoStack != null)
                {
                    bullet = (IAmmunition) ammoStack.getItem();
                }

                if (this.target instanceof EntityLivingBase)
                {
                    //this.getPlatform().provideElectricity(ForgeDirection.UP, ElectricityPack.getFromWatts(this.getFiringRequest(), this.getVoltage()), true);

                    if (this instanceof TileEntityLaserGun)
                    {
                        this.target.attackEntityFrom(TileDamageSource.doLaserDamage(this), 2);
                    }
                    else if (bullet != null)
                    {
                        if (bullet.getType(ammoStack) == ProjectileType.CONVENTIONAL)
                        {
                            this.target.attackEntityFrom(TileDamageSource.doBulletDamage(this), bullet.getDamage());
                        }
                    }

                    fired = true;
                }
                else if (this.target instanceof IAATarget)
                {
                    if (this.worldObj.rand.nextFloat() > 0.2)
                    {
                        int damage = ((IAATarget) this.target).doDamage(8);

                        if (damage == -1 && this.worldObj.rand.nextFloat() > 0.7)
                        {
                            ((IAATarget) this.target).destroyCraft();
                        }
                        else if (damage <= 0)
                        {
                            ((IAATarget) this.target).destroyCraft();
                        }
                    }

                    fired = true;
                }

                if (fired && this.projectileType != ProjectileType.UNKNOWN)
                {
                    if (this.getPlatform().useAmmunition(ammoStack))
                    {
                        boolean drop = true;

                        if (this.getPlatform().getUpgradeCount(TurretUpgradeType.COLLECTOR) > 0)
                        {
                            this.getPlatform().damageUpgrade(TurretUpgradeType.COLLECTOR);
                            if (this.getPlatform().addStackToInventory(ICBMSentry.bulletShell.copy()))
                            {
                                drop = false;
                            }
                        }

                        if (drop && this.worldObj.rand.nextFloat() < 0.9)
                        {
                            Vector3 spawnPos = this.getMuzzle();
                            EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ICBMSentry.bulletShell.copy());
                            entityShell.delayBeforeCanPickup = 20;
                            this.worldObj.spawnEntityInWorld(entityShell);
                        }
                    }

                }

                return fired;
            }
        }

        return false;
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setBoolean("targetPlayers", this.targetPlayers);
        nbt.setBoolean("targetAir", this.targetAir);
        nbt.setBoolean("targetHostile", this.targetHostile);
        nbt.setBoolean("targetFriendly", this.targetFriendly);

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if (nbt.hasKey("targetPlayers"))
        {
            this.targetPlayers = nbt.getBoolean("targetPlayers");
        }
        if (nbt.hasKey("targetAir"))
        {
            this.targetAir = nbt.getBoolean("targetAir");
        }
        if (nbt.hasKey("targetHostile"))
        {
            this.targetHostile = nbt.getBoolean("targetHostile");
        }
        if (nbt.hasKey("targetFriendly"))
        {
            this.targetFriendly = nbt.getBoolean("targetFriendly");
        }

    }

    @Override
    public double getDetectRange()
    {
        if (this.getPlatform() != null)
        {
            return Math.min(this.baseTargetRange + (this.maxTargetRange - this.baseTargetRange) * (this.getPlatform().getUpgradeCount(TurretUpgradeType.RANGE) / 64f), this.maxTargetRange);
        }

        return this.baseTargetRange;
    }

    @Override
    public boolean canApplyPotion(PotionEffect par1PotionEffect)
    {
        return false;
    }

    /** @return Gets the target position accounting target's height. */
    public Vector3 getTargetPosition()
    {
        if (this.getTarget() != null)
        {
            return new Vector3(this.getTarget()).add(new Vector3(0, this.getTarget().getEyeHeight(), 0));
        }

        return null;
    }
}
