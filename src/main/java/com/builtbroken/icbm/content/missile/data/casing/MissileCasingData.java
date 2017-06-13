package com.builtbroken.icbm.content.missile.data.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.IModObject;
import com.builtbroken.mc.lib.json.imp.IJsonGenObject;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import com.builtbroken.mc.lib.json.processors.JsonGenData;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/13/2017.
 */
public class MissileCasingData extends JsonGenData implements IJsonGenObject, IModObject
{
    public final String ID;

    public MissileCasingData(IJsonProcessor processor, String ID)
    {
        super(processor);
        this.ID = ID;
        super.register();
    }

    @Override
    public void register()
    {
        //MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, "missile_" + size.name().toLowerCase(), size.missile_clazz);
    }

    @Override
    public String getContentID()
    {
        return uniqueContentID();
    }

    @Override
    public String toString()
    {
        return "MissileCasingData[]";
    }

    @Override
    public String uniqueContentID()
    {
        return ID;
    }

    @Override
    public String contentType()
    {
        return "missile";
    }

    @Override
    public String modID()
    {
        return ICBM.DOMAIN;
    }

    public int getMissileBodySize()
    {
        return 0;
    }

    public double getMass()
    {
        return 0;
    }
}
