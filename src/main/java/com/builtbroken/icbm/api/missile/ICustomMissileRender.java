package com.builtbroken.icbm.api.missile;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

/**
 * Applied to instances of {@link com.builtbroken.icbm.api.modules.IMissile} that
 * provide custom rendering.
 * <p>
 * Created by robert on 12/12/2014.
 *
 * @deprecated - replaced by the JSON render system, if this doesn't work for you create a custom model object
 * for the JSON system that will allow higher control of rendering.
 */
@Deprecated
public interface ICustomMissileRender
{
    /**
     * Location and rotation is already set based on default missile. So only make minor adjustments based
     * on your model as needed. Avoid extra effects as this will slow down the render time in the inventory.
     *
     * @param type  - render type
     * @param stack - missile itemstack
     * @param data  - unknown?
     * @return false to use the default missile render
     */
    boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data);

    /**
     * Height offset to render a missile perfectly on top of a block
     *
     * @return value to allow the missile to render correctly
     */
    float getRenderHeightOffset();

    /**
     * Called to render the entity version of the missile.
     * Location is already set before being called
     * <p>
     * By default with a yaw of zero and pitch of zero the missile should point NORTH
     *
     * @param entity - missile entity
     * @param f      - unknown
     * @param f1     - unknown
     * @return false to use the default missile render
     */
    default boolean renderMissileEntity(Entity entity, float f, float f1)
    {
        return renderMissileInWorld();
    }

    /**
     * Called to render a missile with a set yaw and pitch
     * Location is already set before calling, only translate as needed for the model's values
     * <p>
     * By default with a yaw of zero and pitch of zero the missile should point strait up.
     * As most of the missile silos assume the model is point up when passing in rotation
     * values. For entities this changes and is noted in the render entity method.
     *
     * @param yaw   - rotation yaw, rotation around y axis, left & right movement
     * @param pitch - rotation pitch, moving nose up and down
     * @return false to use the default missile render
     */
    default boolean renderMissileInWorld(float yaw, float pitch, float f)
    {
        return renderMissileInWorld();
    }

    /**
     * Location and rotation is already set based on default missile. So only make minor adjustments based
     * on your model as needed.
     *
     * @return false to use the default missile render
     * @deprecated use {@link #renderMissileInWorld(float, float, float)}
     */
    @Deprecated
    default boolean renderMissileInWorld()
    {
        return false;
    }
}
