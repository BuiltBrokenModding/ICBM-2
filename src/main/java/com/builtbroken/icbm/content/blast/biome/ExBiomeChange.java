package com.builtbroken.icbm.content.blast.biome;

import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import net.minecraft.item.ItemStack;
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
    public static final List<Integer> bannedBiomeIds = new ArrayList();

    public ExBiomeChange()
    {
        super("biomeChange", 1);
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double size, NBTTagCompound tag)
    {
        int id = getBiomeID(tag);
        if (id >= 0 && id < BiomeGenBase.getBiomeGenArray().length && BiomeGenBase.getBiome(id) != null && !bannedBiomeIds.contains(id))
        {
            BlastBiome blast = new BlastBiome(BiomeGenBase.getBiome(id).biomeID);
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

    public static int getBiomeID(ItemStack stack)
    {
        if (stack.getItem() instanceof IExplosiveItem)
        {
            NBTTagCompound data = ((IExplosiveItem) stack.getItem()).getAdditionalExplosiveData(stack);
            return getBiomeID(data);
        }
        return 0;
    }

    public static int getBiomeID(NBTTagCompound data)
    {
        if (data != null && data.hasKey("biomeID"))
        {
            return data.getInteger("biomeID");
        }
        return 0;
    }

    @Override
    public boolean doesDamageMissile(IMissileEntity entity, IMissile missile, IWarhead warhead, boolean warheadBlew, boolean engineBlew)
    {
        return engineBlew;
    }
}
