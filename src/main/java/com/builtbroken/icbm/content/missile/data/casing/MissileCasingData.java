package com.builtbroken.icbm.content.missile.data.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.mc.api.IModObject;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.framework.json.imp.IJsonGenObject;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.processors.JsonGenData;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/13/2017.
 */
public class MissileCasingData extends JsonGenData implements IJsonGenObject, IModObject
{
    public final String ID;

    private int width;
    private int height;
    private int mass;
    private int size = -1;
    private Pos centerOffset = Pos.zero;
    private float health;

    /** List of textures that are valid for this casing */
    public final List<String> textureNames = new ArrayList();

    public MissileCasingData(IJsonProcessor processor, String ID)
    {
        super(processor);
        this.ID = ID;
    }

    @Override
    public void onCreated()
    {
        if (size >= 0 && size < MissileSize.values().length)
        {
            if (MissileSize.values()[size].casingDataMap.containsKey(ID))
            {
                throw new IllegalArgumentException("Duplicate ID found for " + ID);
            }
            MissileSize.values()[size].casingDataMap.put(ID, this);
        }
    }

    @Override
    public String getContentID()
    {
        return contentType() + "." + uniqueContentID();
    }

    @Override
    public String getUniqueID()
    {
        return ID;
    }

    @Override
    public String toString()
    {
        return "MissileCasingData[" + getContentID() + "]";
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
    public String getMod()
    {
        return ICBM.DOMAIN;
    }

    public int getWidth()
    {
        return width;
    }

    @JsonProcessorData(value = "bodyWidth", type = "int")
    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    @JsonProcessorData(value = "bodyHeight", type = "int")
    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getMass()
    {
        return mass;
    }

    @JsonProcessorData(value = "bodyMass", type = "int")
    public void setMass(int mass)
    {
        this.mass = mass;
    }

    public int getMissileBodySize()
    {
        return size;
    }

    @JsonProcessorData(value = "bodySizeClassification")
    public void setMissileBodySize(String size)
    {
        for (MissileSize missileSize : MissileSize.values())
        {
            if (missileSize.name().equalsIgnoreCase(size))
            {
                setMissileBodySize(missileSize.ordinal());
            }
        }
        if (this.size == -1)
        {
            throw new IllegalArgumentException("Failed to read in missile size for value '" + size + "'");
        }
    }

    @JsonProcessorData(value = "bodySizeNumber", type = "int") //Same as string version
    public void setMissileBodySize(int size)
    {
        this.size = size;
        if (size < 0 || size >= MissileSize.values().length)
        {
            throw new IllegalArgumentException("Size '" + size + "' is outside the range of valid sizes.");
        }
    }

    public Pos getCenterOffset()
    {
        return centerOffset;
    }

    @JsonProcessorData(value = "bodyCenterOffset", type = "pos")
    public void setCenterOffset(Pos centerOffset)
    {
        this.centerOffset = centerOffset;
    }

    public float getHealth()
    {
        return health;
    }

    @JsonProcessorData(value = "bodyHealth", type = "float")
    public void setHealth(float health)
    {
        this.health = health;
    }
}
