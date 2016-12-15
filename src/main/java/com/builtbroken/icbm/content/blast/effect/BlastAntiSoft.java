package com.builtbroken.icbm.content.blast.effect;

import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

/**
 * Blast designed to destroy soft blocks used to construct buildings. Mainly for me(Dark) to clear out Ancient Warfare's bandit camps for looting chests faster.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/7/2015.
 */
public class BlastAntiSoft extends BlastSimplePath<BlastAntiSoft>
{
    public BlastAntiSoft(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(Location location)
    {
        //TODO add blacklist
        //TODO add registry
        if (location.getTileEntity() == null)
        {
            Block block = location.getBlock();
            Material material = block.getMaterial();
            if (material == Material.carpet || material == Material.cloth || material == Material.wood || material == Material.leaves)
            {
                return new BlockEdit(location).set(Blocks.air, 0, true, true);
            }
        }
        return null;
    }
}
