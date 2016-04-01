package com.builtbroken.icbm.content.blast.item;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/1/2016.
 */
public class BlockFakeCake extends BlockCake
{
    public BlockFakeCake()
    {
        super();
        setHardness(0.5F);
        setStepSound(soundTypeCloth);
        setBlockName("cake");
        disableStats();
        setBlockTextureName("cake");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        this.eatCake(world, x, y, z, player);
        return true;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        this.eatCake(world, x, y, z, player);
    }

    public void eatCake(World world, int x, int y, int z, EntityPlayer player)
    {
        if (ICBM.APRIL_FIRST)
        {
            if(!world.isRemote)
            {
                double posX = (double) ((float) x + world.rand.nextFloat());
                double posY = (double) ((float) y + world.rand.nextFloat());
                double posZ = (double) ((float) z + world.rand.nextFloat());

                Pos pos = new Pos().addRandom(world.rand, 0.2);

                Engine.proxy.spawnParticle("explode", world, (posX + x * 1.0D) / 2.0D, (posY + y * 1.0D) / 2.0D, (posZ + z * 1.0D) / 2.0D, pos.x(), pos.y(), pos.z());
                Engine.proxy.spawnParticle("smoke", world, posX, posY, posZ, pos.x(), pos.y(), pos.z());
                world.playSoundEffect(x + 0.5, y + 0.2, z + 0.5, "icbm:icbm.tada", 0.4f, 1.0F);
                player.addChatComponentMessage(new ChatComponentText("The cake is a lie..."));
            }
            world.setBlockToAir(x, y, z);
        }
        else if (player.canEat(false))
        {
            player.getFoodStats().addStats(2, 0.1F);
            int l = world.getBlockMetadata(x, y, z) + 1;

            if (l >= 6)
            {
                world.setBlockToAir(x, y, z);
            }
            else
            {
                world.setBlockMetadataWithNotify(x, y, z, l, 2);
            }
        }
    }
}
