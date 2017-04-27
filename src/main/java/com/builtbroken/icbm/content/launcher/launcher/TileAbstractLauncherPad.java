package com.builtbroken.icbm.content.launcher.launcher;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public abstract class TileAbstractLauncherPad extends TileAbstractLauncher
{
    public TileAbstractLauncherPad(String id, String mod)
    {
        super(id, mod);
    }
    /* TODO move to JSON
    public TileAbstractLauncherPad(String name)
    {
        super(name, Material.iron);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
        this.itemBlock = ItemBlockLauncherPad.class;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side)
    {
        return ICBM.blockLauncherParts.getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }
    */
}
