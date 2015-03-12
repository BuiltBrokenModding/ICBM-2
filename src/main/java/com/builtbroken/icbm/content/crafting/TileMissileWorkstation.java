package com.builtbroken.icbm.content.crafting;

import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 3/12/2015.
 */
public class TileMissileWorkstation extends TileMissileContainer
{
    //Static values
    public static final int INPUT_SLOT = 0;
    public static final int WARHEAD_SLOT = 1;
    public static final int ENGINE_SLOT = 2;
    public static final int GUIDANCE_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    public static final int[] INPUTS = new int[]{INPUT_SLOT, WARHEAD_SLOT, ENGINE_SLOT, GUIDANCE_SLOT};
    public static final int[] OUTPUTS = new int[]{OUTPUT_SLOT};

    public static final int MAX_PROGRESS = 200;

    //Machine vars
    protected boolean automated = false;
    protected boolean enforce_complete = false;
    protected int progress = 0;


    public TileMissileWorkstation()
    {
        super("missileworkstation", Material.iron, 5);
    }

    @Override
    public void update()
    {
        //TODO add progress timer for manual building as well
        if(automated)
        {
            if(getMissileItem() != null && getStackInSlot(OUTPUT_SLOT) == null && getMissileItem().getItem() instanceof IModularMissileItem)
            {
                if(progress++ >= MAX_PROGRESS && assemble() == "")
                {
                    progress = 0;
                }
            }
        }
    }

    // Adds all components to the missile and sends it to the output slot
    public String assemble()
    {
        if (getStackInSlot(OUTPUT_SLOT) == null)
        {
            if (getMissileItem() == null)
                return "slot.input.empty";
            if(getStackInSlot(OUTPUT_SLOT) != null)
                return "slot.output.full";
            if (getMissileItem().getItem() instanceof IModularMissileItem)
            {
                IModularMissileItem missile = (IModularMissileItem) getMissileItem().getItem();

                //Insert engine
                if (getEngineItem() != null && missile.getEngine(getMissileItem()) == null)
                    if (missile.setEngine(getMissileItem(), getEngineItem(), true))
                    {
                        missile.setEngine(getMissileItem(), getEngineItem(), false);
                        setInventorySlotContents(ENGINE_SLOT, null);
                    }

                //Insert warhead
                if (getWarheadItem() != null && missile.getWarhead(getMissileItem()) == null)
                    if (missile.setWarhead(getMissileItem(), getWarheadItem(), true))
                    {
                        missile.setWarhead(getMissileItem(), getWarheadItem(), false);
                        setInventorySlotContents(WARHEAD_SLOT, null);
                    }

                //Insert guidance
                if (getGuidanceItem() != null && missile.getGuidance(getMissileItem()) == null)
                    if (missile.setGuidance(getMissileItem(), getGuidanceItem(), true))
                    {
                        missile.setGuidance(getMissileItem(), getGuidanceItem(), false);
                        setInventorySlotContents(GUIDANCE_SLOT, null);
                    }
                if(enforce_complete)
                {
                    if(missile.getEngine(getMissileItem()) == null || missile.getWarhead(getMissileItem()) == null || missile.getGuidance(getMissileItem()) == null)
                        return "slot.output.incomplete";
                }
                //Move missile to output slot even if not finished
                setInventorySlotContents(OUTPUT_SLOT, getMissileItem());
                setInventorySlotContents(INPUT_SLOT, null);
                return "";
            }
            return "slot.input.invalid";
        }
        return "slot.output.full";
    }

    /**
     * Breaks down the missile into its parts. Called from the GUI or automation.
     *
     * @return error message
     */
    public String disassemble()
    {
        if (getMissileItem() != null && getMissileItem().getItem() instanceof IModularMissileItem)
        {
            IModularMissileItem missile = (IModularMissileItem) getMissileItem().getItem();
            if (missile.getEngine(getMissileItem()) != null && getEngineItem() != null)
                return "slot.engine.full";
            if (missile.getWarhead(getMissileItem()) != null && getWarheadItem() != null)
                return "slot.warhead.full";
            if (missile.getGuidance(getMissileItem()) != null && getGuidanceItem() != null)
                return "slot.guidance.full";
            if (getStackInSlot(OUTPUT_SLOT) != null)
                return "slot.output.full";

            setInventorySlotContents(WARHEAD_SLOT, missile.getWarhead(getMissileItem()));
            setInventorySlotContents(ENGINE_SLOT, missile.getEngine(getMissileItem()));
            setInventorySlotContents(GUIDANCE_SLOT, missile.getGuidance(getMissileItem()));

            missile.setEngine(getMissileItem(), null, false);
            missile.setWarhead(getMissileItem(), null, false);
            missile.setGuidance(getMissileItem(), null, false);

            setInventorySlotContents(OUTPUT_SLOT, getMissileItem());
            setInventorySlotContents(INPUT_SLOT, null);
            return "";
        }
        return "slot.input.invalid";
    }

    public ItemStack getWarheadItem()
    {
        return getStackInSlot(WARHEAD_SLOT);
    }

    public ItemStack getEngineItem()
    {
        return getStackInSlot(ENGINE_SLOT);
    }

    public ItemStack getGuidanceItem()
    {
        return getStackInSlot(GUIDANCE_SLOT);
    }
}
