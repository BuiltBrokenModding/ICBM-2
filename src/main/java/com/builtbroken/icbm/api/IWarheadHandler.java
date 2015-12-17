package com.builtbroken.icbm.api;

import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Extended version of IExplosiveHandler that handles warheads and thier explosives.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/16/2015.
 */
public interface IWarheadHandler extends IExplosiveHandler
{
    void addInfoToItem(EntityPlayer player, IWarhead warhead, List<String> list);
}
