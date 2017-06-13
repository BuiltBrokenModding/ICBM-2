package com.builtbroken.icbm.content.missile.parts.engine;

import com.builtbroken.icbm.api.modules.IMissile;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * Creative mode only version of the engine that
 * has no fuel requirement and is used as a
 * place holder when creating missiles without
 * crafting. When removed the engine will break
 * preventing players from cheating.
 * <p/>
 * Created by robert on 12/28/2014.
 */
public class RocketEngineCreative extends RocketEngine
{
    public RocketEngineCreative(ItemStack item)
    {
        super(item, "engine.creative");
        this.engineSmokeColor = new Color(79, 43, 80);
        this.engineFireColor = new Color(106, 13, 108);
    }

    @Override
    public float getSpeed(IMissile missile)
    {
        return 2f;
    }

    @Override
    public float getMaxDistance(IMissile missile)
    {
        return 10000f;
    }
}
