package resonant.lib.world

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.{AxisAlignedBB, EnumFacing}
import net.minecraft.world.World
import resonant.lib.transform.vector.VectorWorld

/** Simple version of Vector3 that is used to track location
  * along with the block to be set at that location.
  *
  * Created by robert on 12/2/2014.
  */
class BlockEdit(w: World, x: Double, y: Double, z: Double) extends VectorWorld(w, x, y, z)
{
  // Used to double check in case the block changes before we place the block

  /** Block that was at the location */
  var prev_block: Block = Blocks.air

  /** 0-15 meta that was at the location */
  var prev_meta: Int = 0;

  //Placement data
  /** Block to place */
  var block: Block = Blocks.air
  /** 0-15 meta value to place */
  var meta: Int = 0;

  /** direction placed from */
  var face: EnumFacing = null

  //Extra data for set use cases
  /** Force energy used to place it */
  var energy: Float = 0;
  /** Drop block that was at the location */
  var doItemDrop = false
  /** Ensure prev_block is the same */
  var checkForPrevBlockEquals = false
  /** Ensure prev_block is the same */
  var checkForEntity = false
  
  private var _bounds : AxisAlignedBB = null
  

  def this() = this(null, 0, 0, 0)

  def set(block: Block, meta: Int, doDrops: Boolean, checkEquals: Boolean) : BlockEdit =
  {
    this.block = block;
    this.meta = meta;
    doItemDrop = doDrops
    logPrevBlock()
    return this
  }

  def logPrevBlock()
  {
    checkForPrevBlockEquals = true
    if (world != null)
    {
      prev_block = getBlock
      prev_meta = getBlockMetadata
    }
  }

  override def set(x: Double, y: Double, z: Double)
  {
    super.set(x, y, z)
    if(_bounds != null)
      AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
  }
  
  def getBounds : AxisAlignedBB =
  {
    if(_bounds == null)
      AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1)
    return _bounds
  }

  /** Called to place the block at the location using the data stored in this object.
    * Insure that you have set the block data or the block at the location will default
    * to air
    *
    * @return result of the placement
    */
  def place(): BlockEditResult =
  {
    //We can not place a block without a world
    if (world != null)
    {
      //Check if the chunk exists and is loaded to prevent loading/creating new chunks
      val chunk = world.getChunkFromBlockCoords(xi, zi);
      if (chunk != null && chunk.isChunkLoaded)
      {
        //Check if the prev_block still exists
        if(checkForPrevBlockEquals && prev_block != getBlock && prev_meta != getBlockMetadata)
          return BlockEditResult.PREV_BLOCK_CHANGED
        
        //Check if an entity is in the way
        if (checkForEntity)
        {
          val entities = world.getEntitiesWithinAABB(classOf[Entity], getBounds)
          if (entities.size() > 0)
            return BlockEditResult.ENTITY_BLOCKED
        }
        
        return doPlace()
      }
      return BlockEditResult.CHUNK_UNLOADED
    }
    return BlockEditResult.NULL_WORLD
  }

  /** Called to place the block, override this for custom placement information
    * 
   * @return result of placement
   */
  protected def doPlace(): BlockEditResult =
  {
    //Check if it was already placed to prevent item lose if this is being used by a schematic
    if (getBlock == block && getBlockMetadata == meta)
      return BlockEditResult.ALREADY_PLACED

    //Place the block and check if the world says its placed
    if (super.setBlock(world, block, meta))
    {
      //Handle item drops
      if(doItemDrop)
        getBlock.dropBlockAsItem(world, xi, yi, zi, getBlockMetadata, 0)

      return BlockEditResult.PLACED
    }

    return BlockEditResult.BLOCKED
  }
}
