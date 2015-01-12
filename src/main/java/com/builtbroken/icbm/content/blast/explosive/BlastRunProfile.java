package com.builtbroken.icbm.content.blast.explosive;


import com.builtbroken.jlib.lang.StringHelpers;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.testing.debug.profiler.RunProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Profile data for blast basic
 * Created by robert on 12/10/2014.
 */
public class BlastRunProfile extends RunProfile
{
    public final int size;
    public final float energy;
    public final Pos center;
    public int tilesPathed = 0;
    public int airBlocksPathed = 0;
    public int blocksRemoved = 0;
    public List<Long> blockIterationTimes = new ArrayList();

    public BlastRunProfile(BlastBasic blast)
    {
        super("icbm.blast."+ blast.getClass().getSimpleName() + "#"+ System.nanoTime());
        this.size = blast.size;
        this.energy = blast.energy;
        this.center = new Pos(blast.x, blast.y, blast.z);
    }

    @Override
    public void addRunData(StringBuilder stringBuilder)
    {
        stringBuilder.append("\nCenter: " + center);
        stringBuilder.append("\nEnergy: " + energy);
        stringBuilder.append("\nSize:   " + size);
        stringBuilder.append("\n");

        stringBuilder.append("\nIterations: " + StringHelpers.fitIntoSpaces(tilesPathed, 5));
        stringBuilder.append("\nAir:        " + StringHelpers.fitIntoSpaces(airBlocksPathed, 5));
        stringBuilder.append("\nBlocks:     " + StringHelpers.fitIntoSpaces(blocksRemoved, 5));
        stringBuilder.append("\n");

        long averageBlockIterationTime = 0;
        for (Long n : blockIterationTimes)
        {
            averageBlockIterationTime += n;
        }
        averageBlockIterationTime /= blockIterationTimes.size();

        Collections.sort(blockIterationTimes);
        stringBuilder.append("\nAvg B Time: " + StringHelpers.formatNanoTime(averageBlockIterationTime));
        stringBuilder.append("\nMin B Time: " + StringHelpers.formatNanoTime(blockIterationTimes.get(0)));
        stringBuilder.append("\nMax B Time: " + StringHelpers.formatNanoTime(blockIterationTimes.get(blockIterationTimes.size() - 1)));
        stringBuilder.append("\n");
    }
}
