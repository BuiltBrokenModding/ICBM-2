package com.builtbroken.icbm.content.ams;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.radar.RadarRegistry;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/5/2016.
 */
public class TileAMS extends TileModuleMachine implements IPacketIDReceiver
{
    protected EulerAngle aim = new EulerAngle(0, 0, 0);
    protected EntityTargetingSelector selector;

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
            List<Entity> list = RadarRegistry.getAllLivingObjectsWithin(world(), x() + 0.5, y() + 0.5, z() + 0.5, 300, selector);
            EntityPlayer player = world().getClosestPlayer(x(), y(), z(), 50);
            if (player != null)
            {
                Pos target = new Pos(player.posX + (player.width / 2), player.posY + (player.height / 2), player.posZ + (player.width / 2));
                aim = toPos().add(0.5).toEulerAngle(target);
                sendDescPacket();
            }
        }
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
