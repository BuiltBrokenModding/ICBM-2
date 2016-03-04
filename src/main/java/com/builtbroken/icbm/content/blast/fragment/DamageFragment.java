package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.IChatComponent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/3/2016.
 */
public class DamageFragment extends EntityDamageSourceIndirect
{
    public DamageFragment(String name, Entity entity, Entity attacker)
    {
        super(name, entity, attacker);
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase entity)
    {
        String translation = LanguageUtility.getLocal("damage.fragment." + damageType);
        if (translation != null)
        {
            return new ChatComponentText(String.format(translation, entity.getCommandSenderName()));
        }
        return super.func_151519_b(entity);
    }
}
