package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExPlantLife extends ExplosiveHandlerICBM<BlastPlantLife>
{
    public ExPlantLife()
    {
        super("PlantLife", 3);
    }

    @Override
    protected BlastPlantLife newBlast()
    {
        return new BlastPlantLife();
    }
}
