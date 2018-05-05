package com.builtbroken.icbm.content.launcher.door.json;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.framework.json.processors.JsonProcessor;
import com.google.gson.JsonElement;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/4/2018.
 */
public class JsonProcessorDoorData extends JsonProcessor<DoorData>
{
    public JsonProcessorDoorData()
    {
        super(DoorData.class);
    }

    @Override
    public DoorData process(JsonElement element)
    {
        return new DoorData(this);
    }

    @Override
    public String getMod()
    {
        return ICBM.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return "siloDoor";
    }

    @Override
    public String getLoadOrder()
    {
        return null;
    }
}
