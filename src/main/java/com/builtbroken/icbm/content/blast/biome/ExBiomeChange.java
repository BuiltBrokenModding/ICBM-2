package com.builtbroken.icbm.content.blast.biome;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExBiomeChange extends ExplosiveHandlerICBM<BlastBiome>
{
    public static final List<Byte> bannedBiomeIds = new ArrayList();

    public ExBiomeChange()
    {
        super("biomeChange", 1);
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double size, NBTTagCompound tag)
    {
        byte id = tag.hasKey("biomeID") ? tag.getByte("biomeID") : -1;
        if (id >= 0 && BiomeGenBase.getBiome(id) != null && !bannedBiomeIds.contains(id))
        {
            BlastBiome blast = new BlastBiome(id);
            if (blast != null)
            {
                blast.setLocation(world, x, y, z);
                blast.setCause(triggerCause);
                blast.setYield(size * multi);
                blast.setAdditionBlastData(tag);
            }
            return blast;
        }
        return null;
    }
}
