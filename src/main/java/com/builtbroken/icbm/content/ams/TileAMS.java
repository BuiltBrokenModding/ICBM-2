package com.builtbroken.icbm.content.ams;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.radar.RadarRegistry;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/5/2016.
 */
public class TileAMS extends TileModuleMachine implements IPacketIDReceiver
{
    protected EulerAngle aim = new EulerAngle(0, 0, 0);
    protected EulerAngle currentAim = new EulerAngle(0, 0, 0);

    protected EntityTargetingSelector selector;
    protected Entity target = null;

    long lastRotationUpdate = System.nanoTime();

    public TileAMS()
    {
        super("ICMBxAMS", Material.iron);
        this.hardness = 15f;
        this.resistance = 50f;
        this.itemBlock = ItemBlockAMS.class;
        this.renderNormalBlock = false;
        this.addInventoryModule(9);
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            //Set selector if missing
            if (selector == null)
            {
                selector = new EntityTargetingSelector(this);
            }
            //Clear target if invalid
            if (target != null && target.isDead)
            {
                target = null;
            }
            //Get new target
            if (target == null)
            {
                target = getClosestTarget();
            }
            if (target != null)
            {
                if (ticks % 3 == 0)
                {
                    Pos aimPoint = new Pos(this.target);
                    aim = toPos().add(0.5).toEulerAngle(aimPoint);
                    aim.yaw_$eq(EulerAngle.clampAngleTo360(aim.yaw()));
                    aim.pitch_$eq(EulerAngle.clampAngleTo360(aim.pitch()));

                    sendDescPacket();

                    //Update server rotation value, can be independent from client
                    long delta = System.nanoTime() - lastRotationUpdate;
                    currentAim.yaw_$eq(EulerAngle.clampAngleTo360(MathHelper.lerp(currentAim.yaw(), aim.yaw(), (double) delta / 50000000.0)));
                    currentAim.pitch_$eq(EulerAngle.clampAngleTo360(MathHelper.lerp(currentAim.pitch(), aim.pitch(), (double) delta / 50000000.0)));
                    lastRotationUpdate = System.nanoTime();

                    EulerAngle diff = aim.absoluteDifference(currentAim);
                    if (diff.yaw() > 2 || diff.pitch() > 2)
                    {
                        worldObj.playSoundEffect(x() + 0.5, y() + 0.2, z() + 0.5, "icbm:icbm.servo", ICBM.ams_rotation_volume, 1.0F);
                    }
                }

                if (ticks % 10 == 0 && aim.isWithin(currentAim, 1))
                {
                    fireAt(target);
                }
            }
        }
    }

    private void fireAt(Entity target)
    {
        //TODO move to tip of gun for better effect
        worldObj.playSoundEffect(x() + 0.5, y() + 0.5, z() + 0.5, "icbm:icbm.gun", ICBM.ams_gun_volume, 1.0F);
        if (world().rand.nextFloat() > 0.4)
        {
            if (target instanceof IMissileEntity)
            {
                if (target instanceof EntityMissile)
                {
                    target.attackEntityFrom(DamageSource.generic, world().rand.nextFloat() * 5f);
                }
                else
                {
                    ((IMissileEntity) target).destroyMissile(this, DamageSource.generic, 0.1f, true, true, true);
                }
                sendPacket(new PacketTile(this, 2));
                this.target = null;
                currentAim.yaw_$eq(0);
                currentAim.pitch_$eq(0);
                sendDescPacket();
            }
        }
    }

    protected Entity getClosestTarget()
    {
        List<Entity> list = RadarRegistry.getAllLivingObjectsWithin(world(), x() + 0.5, y() + 0.5, z() + 0.5, 300, selector);
        if (!list.isEmpty())
        {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeInt((int) aim.yaw());
        buf.writeInt((int) aim.pitch());
    }

    @Override
    public Tile newTile()
    {
        return new TileAMS();
    }
}
