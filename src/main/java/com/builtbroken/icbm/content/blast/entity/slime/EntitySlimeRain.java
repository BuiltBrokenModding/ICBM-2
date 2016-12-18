package com.builtbroken.icbm.content.blast.entity.slime;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Version of the slime with custom settings, such as no fall damage, multi-colors, no drops, hats, trolling, etc
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class EntitySlimeRain extends EntitySlime implements IEntityAdditionalSpawnData
{
    //TODO on collision with another slime attempt to merge
    //TODO if 20+ slimes are in a 2.5x2.5 area then form them into a large slime using a twister spinning effect

    //TODO implement sticking to objects
    //TODO implement hats -> hat mod support :P

    //TODO add life limit with config

    public Color color;

    public EntitySlimeRain(World world)
    {
        super(world);
        color = new Color(world.rand.nextInt(255), world.rand.nextInt(255), world.rand.nextInt(255));
    }

    @Override
    public boolean attackEntityFrom(DamageSource damage, float d)
    {
        if (damage != DamageSource.fall)
        {
            return super.attackEntityFrom(damage, d);
        }
        return false;
    }

    @Override
    protected Item getDropItem()
    {
        return Item.getItemById(0);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return true;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeInt(color.getRed());
        buffer.writeInt(color.getGreen());
        buffer.writeInt(color.getBlue());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        color = new Color(additionalData.readInt(), additionalData.readInt(), additionalData.readInt());
    }
}
