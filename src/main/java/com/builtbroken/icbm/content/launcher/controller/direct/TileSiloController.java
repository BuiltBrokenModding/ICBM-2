package com.builtbroken.icbm.content.launcher.controller.direct;

import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Controller that links directly to a silo. Cheap and simple... all a player should really need :P
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloController extends TileMachine
{
    /** Is the tile in redstone mode, meaning it will trigger launch if it receives a redstone signal */
    protected boolean inRedstoneMode = true;

    /** Connected launcher */
    protected TileAbstractLauncher launcher;

    public TileSiloController()
    {
        super("siloController", Material.iron);
        this.canEmmitRedstone = true;
    }

    @Override
    public Tile newTile(World world, int meta)
    {
        return new TileSiloController();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.acacia_stairs.getIcon(0, 0);
    }
}
