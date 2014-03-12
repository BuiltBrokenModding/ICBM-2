package icbm.contraption.block;

import icbm.Reference;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/** @author DarkGuardsman */
public class BlockCombatRail extends BlockRail
{
    public BlockCombatRail(int id)
    {
        super(id);
        this.setHardness(10F);
        this.setResistance(10F);
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName(Reference.PREFIX + "combat.rail");
        this.setTextureName(Reference.PREFIX + "combat.rail");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int y, int x, int z)
    {
        return 0.6f;
    }
}
