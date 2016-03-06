package com.builtbroken.icbm.content.ams;

import com.builtbroken.icbm.api.missile.IMissileEntity;
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
        if (isServer() && ticks % 3 == 0)
        {
            if (selector == null)
            {
                selector = new EntityTargetingSelector(this);
            }
            if (target == null)
            {
                target = getClosestTarget();
            }
            if (target != null)
            {
                Pos aimPoint = new Pos(this.target);
                aim = toPos().add(0.5).toEulerAngle(aimPoint);
                sendDescPacket();

                //Update server rotation value, can be independent from client
                long delta = System.nanoTime() - lastRotationUpdate;
                currentAim.yaw_$eq(MathHelper.lerp(aim.yaw() % 360, aim.yaw(), (double) delta / 50000000.0));
                currentAim.pitch_$eq(MathHelper.lerp(aim.pitch() % 360, aim.pitch(), (double) delta / 50000000.0));
                lastRotationUpdate = System.nanoTime();

                if (aim.isWithin(currentAim, 1))
                {
                    fireAt(target);
                }
            }
        }
    }

    private void fireAt(Entity target)
    {
        if (target instanceof IMissileEntity)
        {
            ((IMissileEntity) target).destroyMissile(this, DamageSource.generic, 0.1f, true, true, true);
            sendPacket(new PacketTile(this, 2));
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
        buf.writeDouble(aim.yaw());
        buf.writeDouble(aim.pitch());
    }

    @Override
    public Tile newTile()
    {
        return new TileAMS();
    }
}
