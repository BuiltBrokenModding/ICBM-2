package icbm.sentry.turret.sentries;

import icbm.core.ICBMCore;
import icbm.sentry.ProjectileType;
import icbm.sentry.interfaces.IAutoSentry;
import icbm.sentry.task.TaskKillTarget;
import icbm.sentry.task.TaskManager;
import icbm.sentry.task.TaskSearchTarget;
import icbm.sentry.turret.TileTurret;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.network.PacketHandler;

import com.google.common.io.ByteArrayDataInput;

/** Extend this class for all turrets that are automatic.
 * 
 * @author Rseifert */
public abstract class TileAutoTurret extends TileTurret implements IAutoSentry, IBlockActivate
{
    /** CURRENT TARGET TO ATTACK */
    public Entity target;

    /** AI MANAGER */
    public final TaskManager taskManager;

    /** DEFAULT TARGETING RANGE */
    public int baseTargetRange = 20;

    /** MAX TARGETING RANGE */
    public int maxTargetRange = 90;

    /** IDLE ROTATION SPEED */
    public float rotationSpeed = 3;

    /** MAIN AMMO TYPE */
    public ProjectileType projectileType = ProjectileType.CONVENTIONAL;

    public int lastRotateTick;

    protected boolean allowFreePitch;

    public TileAutoTurret()
    {
        taskManager = new TaskManager(this);
        this.loadTasks();
    }

    public void loadTasks()
    {
        this.taskManager.addTask(new TaskSearchTarget());
        this.taskManager.addTask(new TaskKillTarget());
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (lastRotateTick > 0)
            lastRotateTick--;

        if (!this.worldObj.isRemote)
        {
            this.taskManager.onUpdate();

            if (!this.taskManager.hasTasks())
            {
                this.loadTasks();
            }
        }
    }

    @Override
    public float getRotationSpeed()
    {
        return this.rotationSpeed;
    }

    public AxisAlignedBB getTargetingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - this.maxTargetRange, this.yCoord - 5, zCoord - this.maxTargetRange, xCoord + this.maxTargetRange, yCoord + this.maxTargetRange, zCoord + this.maxTargetRange);
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

    public boolean isValidTarget(Entity entity)
    {
        if (entity != null)
        {
            if (!entity.isDead && !entity.isEntityInvulnerable())
            {
                if (this.pos().distance(new Vector3(entity)) <= this.maxTargetRange)
                {
                    float[] rotations = this.lookHelper.getDeltaRotations(new Vector3(entity).translate(new Vector3(0, entity.getEyeHeight(), 0)));

                    if ((rotations[1] <= this.getPitchServo().upperLimit() && rotations[1] >= this.getPitchServo().lowerLimit()) || this.allowFreePitch)
                    {
                        if (this.lookHelper.canEntityBeSeen(entity))
                        {
                            if (entity instanceof IMob)
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra) throws IOException
    {
        if (id == TurretPacketType.SHOT.ordinal())
        {
            Vector3 target = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
            this.getYawServo().setRotation(data.readFloat());
            this.getYawServo().setRotation(data.readFloat());
            this.drawParticleStreamTo(target);
        }

        super.onReceivePacket(id, data, player, extra);
    }

    /** Sends the firing info to the client to render tracer effects */
    public void sendShotToClient(Vector3 position)
    {
        PacketHandler.sendPacketToClients(ICBMCore.PACKET_TILE.getPacket(this, TurretPacketType.SHOT.ordinal(), this, position, this.getYawServo().getRotation(), this.getPitchServo().getRotation()), this.worldObj, new Vector3(this), 40);
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

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (entityPlayer != null)
        {
            entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Sentries are indev and don't currently have a functioning GUI"));
            return true;
        }
        return false;
    }
}
