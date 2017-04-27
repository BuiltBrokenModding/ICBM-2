package com.builtbroken.icbm.content.storage.small;

import com.builtbroken.icbm.content.prefab.ItemBlockICBM;
import net.minecraft.block.Block;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/19/2016.
 */
public class ItemBlockSmallSilo extends ItemBlockICBM
{
    public ItemBlockSmallSilo(Block block)
    {
        super(block);
        additionalHeight = 2;
    }
}
