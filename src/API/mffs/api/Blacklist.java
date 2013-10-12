package mffs.api;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;

public class Blacklist
{
	/**
	 * Adds blocks to this black list if you do not wish them to be moved by the following:
	 */
	public static final Set<Block> stabilizationBlacklist = new HashSet<Block>();
	public static final Set<Block> disintegrationBlacklist = new HashSet<Block>();
	public static final Set<Block> forceManipulationBlacklist = new HashSet<Block>();

}
