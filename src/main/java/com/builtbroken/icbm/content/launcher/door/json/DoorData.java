package com.builtbroken.icbm.content.launcher.door.json;

import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.framework.json.processors.JsonGenData;

import java.util.HashMap;

/**
 * Handles properties of the silo doors
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/4/2018.
 */
public class DoorData extends JsonGenData
{
    public static final HashMap<String, DoorData> doorMap = new HashMap();

    public static final DoorData NULL_DOOR = new DoorData(null);

    @JsonProcessorData(value = "id", required = true)
    public String id;

    @JsonProcessorData(value = "doorRender", required = true)
    public String doorRender;

    @JsonProcessorData(value = "doorLayout", required = true)
    public String doorLayout;

    @JsonProcessorData(value = "blockSize", type = "int", required = true)
    public int blockSize = 3;

    @JsonProcessorData(value = "blockHeight", type = "int")
    public int blockHeight = 1;

    public DoorData(IJsonProcessor processor)
    {
        super(processor);
    }

    @Override
    public void onCreated()
    {
        if (doorMap.containsKey(getContentID()))
        {
            throw new RuntimeException("Duplicate key detected while registering " + this + " owned by " + doorMap.get(getContentID()));
        }
        doorMap.put(getContentID(), this);
    }

    @Override
    public void validate()
    {
        id = id.toLowerCase();
        if (getMod() == null)
        {
            throw new RuntimeException("Missing mod_id for " + this);
        }
        if (blockSize % 2 == 0 || blockSize <= 0)
        {
            throw new RuntimeException("Silo door size has to be an odd number, greater than zero " + this);
        }
    }

    @Override
    public String getUniqueID()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "DoorData[" + getContentID() + "]@" + hashCode();
    }

    public static DoorData getDoorData(String doorID)
    {
        DoorData data = doorMap.get(doorID.toLowerCase());
        if (data != null)
        {
            return data;
        }
        return NULL_DOOR;
    }
}
