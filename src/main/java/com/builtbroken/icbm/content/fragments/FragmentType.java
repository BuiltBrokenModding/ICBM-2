package com.builtbroken.icbm.content.fragments;

/**
 * Enum of valid fragment types. Controls rendering and logic of fragments
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/2016.
 */
public enum FragmentType
{
    /** Block based items, will render in as a 3D cube */
    BLOCK,
    /** Vanilla MC, will render the same and always be on fire */
    BLAZE,
    /** Any item that will fire like a projectile, similar to an arrow in logic but with item based rendering */
    PROJECTILE,
    /** Works exactly like an arrow */
    ARROW
}
