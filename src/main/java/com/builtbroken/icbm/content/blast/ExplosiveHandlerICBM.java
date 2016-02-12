package com.builtbroken.icbm.content.blast;

import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.items.IExplosiveItem;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.explosive.AbstractExplosiveHandler;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public abstract class ExplosiveHandlerICBM<B extends Blast> extends AbstractExplosiveHandler
{
    protected final int multi;

    /**
     * Creates an explosive using a blast class, and name
     *
     * @param name  - name to use for registry id
     * @param multi - size to adjust/multiply the original explosive size by
     */
    public ExplosiveHandlerICBM(String name, int multi)
    {
        super(name);
        this.multi = multi;
    }

    @Override
    public void addInfoToItem(EntityPlayer player, ItemStack stack, List<String> lines)
    {
        if (stack != null && stack.getItem() instanceof IExplosiveItem)
        {
            String s = LanguageUtility.getLocal("info.icbm:warhead.size.name");
            if (s != null && !s.isEmpty())
            {
                lines.add(String.format(s, ((IExplosiveItem) stack.getItem()).getExplosiveSize(stack)));
            }
        }
    }

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double size, NBTTagCompound tag)
    {
        B blast = newBlast(tag);
        if (blast != null)
        {
            blast.setLocation(world, x, y, z);
            blast.setCause(triggerCause);
            blast.setYield(size * multi);
            blast.setAdditionBlastData(tag);
        }
        return blast;
    }

    /**
     * New Instance of the blast class
     *
     * @return
     */
    protected B newBlast(NBTTagCompound tag)
    {
        return newBlast();
    }

    /**
     * New Instance of the blast class
     *
     * @return
     */
    protected B newBlast()
    {
        return null;
    }
}
