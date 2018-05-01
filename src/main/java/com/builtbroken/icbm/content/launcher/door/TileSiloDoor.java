package com.builtbroken.icbm.content.launcher.door;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.logic.TileNode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/1/2018.
 */
@TileWrapped(className = "TileWrapperSiloDoor", wrappers = "MultiBlock")
public class TileSiloDoor extends TileNode implements IRotatable
{
    public boolean isOpen = false;
    public boolean shouldBeOpen = false;
    public float doorRotation = 0f;

    private ForgeDirection dir_cache;

    public TileSiloDoor()
    {
        super("door.silo", ICBM.DOMAIN);
    }

    public boolean isStructureGettingRedstone()
    {
        //TODO scan all parts of structure to check for redstone
        return false;
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
