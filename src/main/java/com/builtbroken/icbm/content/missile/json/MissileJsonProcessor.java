package com.builtbroken.icbm.content.missile.json;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.missile.data.casing.MissileCasingData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.json.processors.JsonProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Handles loading {@link MissileCasingData} from JSON data
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/13/2017.
 */
public class MissileJsonProcessor extends JsonProcessor<MissileCasingData>
{
    public MissileJsonProcessor()
    {
        super(MissileCasingData.class);
    }

    @Override
    public MissileCasingData process(JsonElement element)
    {
        debugPrinter.start("MissileProcessor", "Processing entry", Engine.runningAsDev);

        final JsonObject sentryJsonObject = element.getAsJsonObject();
        ensureValuesExist(sentryJsonObject, "id");

        String id = sentryJsonObject.getAsJsonPrimitive("id").getAsString();

        debugPrinter.log("ID: " + id);

        MissileCasingData sentryData = new MissileCasingData(this, id);

        //Call to process injection tags
        for (Map.Entry<String, JsonElement> entry : sentryJsonObject.entrySet())
        {
            if (keyHandler.handle(sentryData, entry.getKey().toLowerCase(), entry.getValue()))
            {
                debugPrinter.log("Injected Key: " + entry.getKey());
            }
        }

        debugPrinter.end("Done...");
        return sentryData;
    }

    @Override
    public String getMod()
    {
        return ICBM.DOMAIN;
    }

    @Override
    public String getJsonKey()
    {
        return "missile";
    }

    @Override
    public String getLoadOrder()
    {
        return "after:item";
    }
}
