package icbm.explosion.entities;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

/** Exploding sheep
 * 
 * @author DarkGuardsman */
public class EntityBombSheep extends EntityCreeper implements IMob
{
    String owner;

    public EntityBombSheep(World world)
    {
        super(world);
    }

    public void setPlayer(String owner)
    {
        this.owner = owner;
    }
}
