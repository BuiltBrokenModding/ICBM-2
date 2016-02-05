package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.content.blast.ExplosiveHandlerICBM;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExFragment extends ExplosiveHandlerICBM<BlastFragment>
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExFragment()
    {
        super("Fragment", 3);
    }

    @Override
    protected BlastFragment newBlast()
    {
        return new BlastFragment();
    }
}
