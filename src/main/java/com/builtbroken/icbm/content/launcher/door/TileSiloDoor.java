package com.builtbroken.icbm.content.launcher.door;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.block.imp.IActivationListener;
import com.builtbroken.mc.framework.logic.TileNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/1/2018.
 */
@TileWrapped(className = "TileWrapperSiloDoor", wrappers = "MultiBlock")
public class TileSiloDoor extends TileNode implements IRotatable, IActivationListener
{
    public static float openingSpeed = 0.1f;

    public boolean isOpen = false;
    public boolean shouldBeOpen = false;
    public boolean playerSetOpen = false;
    public float doorRotation = 0f;

    public float _prevDoorRotation = 0f;

    private ForgeDirection dir_cache;

    public TileSiloDoor()
    {
        super("door.silo", ICBM.DOMAIN);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        doDoorMotion();
        if (isServer() && ticks % 2 == 0)
        {
            boolean prev = shouldBeOpen;
            shouldBeOpen = isStructureGettingRedstone() || playerSetOpen;
            if (prev != shouldBeOpen)
            {
                world().unwrap().playSoundEffect(xi() + 0.5D, yi() + 0.5D, zi() + 0.5D,
                        "tile.piston.out", 0.5F, world().unwrap().rand.nextFloat() * 0.25F + 0.6F);

            }
            sendDescPacket();
        }
    }

    protected void doDoorMotion()
    {
        if (shouldBeOpen)
        {
            if (doorRotation < 90)
            {
                doorRotation += openingSpeed;
            }
            else
            {
                isOpen = true;
            }
        }
        else
        {
            if (doorRotation > 0)
            {
                doorRotation -= openingSpeed;
            }
            else
            {
                isOpen = false;
            }
        }
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.redstone)
        {
            if (isServer())
            {
                playerSetOpen = !playerSetOpen;
            }
            return true;
        }
        return false;
    }

    public boolean isStructureGettingRedstone()
    {
        //TODO scan all parts of structure to check for redstone
        return false;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        isOpen = buf.readBoolean();
        shouldBeOpen = buf.readBoolean();
        doorRotation = buf.readFloat();
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeBoolean(isOpen);
        buf.writeBoolean(shouldBeOpen);
        buf.writeFloat(doorRotation);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        isOpen = nbt.getBoolean("isOpen");
        shouldBeOpen = nbt.getBoolean("shouldBeOpen");
        doorRotation = nbt.getFloat("doorRotation");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setBoolean("isOpen", isOpen);
        nbt.setBoolean("shouldBeOpen", shouldBeOpen);
        nbt.setFloat("doorRotation", doorRotation);
        return super.save(nbt);
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {

    }

    @Override
    public ForgeDirection getDirection()
    {
        if (dir_cache == null)
        {
            dir_cache = ForgeDirection.getOrientation(host.getHostMeta());
        }
        return dir_cache;
    }
}
