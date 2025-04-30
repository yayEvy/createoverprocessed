package net.createoverprocessed.forge.content.blocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidHelper.FluidExchange;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class GrillBlock extends Block implements IWrenchable {

    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public GrillBlock(Properties arg) {
        super(arg);
    }
}
