package icbm.content.tile.launcher;

import icbm.content.prefab.tile.TileFrequency;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import resonant.api.explosion.ILauncherController;
import resonant.api.explosion.LauncherType;
import resonant.api.mffs.fortron.FrequencyGridRegistry;
import resonant.lib.transform.vector.Vector3;

public abstract class TileLauncherPrefab extends TileFrequency implements ILauncherController
{
    protected Vector3 targetPos = null;

    public TileLauncherPrefab()
    {
        super(Material.iron);
    }

    @Override
    public void onInstantiate()
    {
        super.onInstantiate();
        FrequencyGridRegistry.instance().add(this);
    }

    @Override
    public void invalidate()
    {
        FrequencyGridRegistry.instance().remove(this);
        super.invalidate();
    }

    @Override
    public Vector3 getTarget()
    {
        if (this.targetPos == null)
        {
            if (this.getLauncherType() == LauncherType.CRUISE)
            {
                this.targetPos = new Vector3(this.xCoord, this.yCoord, this.zCoord);
            }
            else
            {
                this.targetPos = new Vector3(this.xCoord, 0, this.zCoord);
            }
        }

        return this.targetPos;
    }

    @Override
    public void setTarget(Vector3 target)
    {
        this.targetPos = target.floor();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.targetPos = new Vector3(nbt.getCompoundTag("target"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (this.targetPos != null)
        {
            nbt.setTag("target", this.targetPos.writeNBT(new NBTTagCompound()));
        }
    }
}
