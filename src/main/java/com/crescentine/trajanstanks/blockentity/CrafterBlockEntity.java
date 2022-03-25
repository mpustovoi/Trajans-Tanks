package com.crescentine.trajanstanks.blockentity;

import com.crescentine.trajanstanks.TankMod;
import com.crescentine.trajanstanks.item.TankModItems;
import com.crescentine.trajanstanks.recipe.TankCrafterRecipe;
import com.crescentine.trajanstanks.screen.CrafterScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrafterBlockEntity extends InventoryBlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler();
    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> itemHandler);
    public static final Component TITLE = new TranslatableComponent(
            "container." + TankMod.MOD_ID + ".crafter");

    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(TankModBlockEntities.CRAFTER.get(), pos, state, 9);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Tank Crafter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory p_39955_, Player p_39956_) {
        return null;
    }

    public void tick(Level level, BlockPos pos, BlockState state, CrafterBlockEntity entity) {
        if (hasRecipe(entity)) {
            craftItem(entity);
        }
    }

    private static boolean hasRecipe(CrafterBlockEntity entity) {
        Level world = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.inventory.getSlots());
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            inventory.setItem(i, entity.getItemInSlot(i));
        }

        Optional<TankCrafterRecipe> match = world.getRecipeManager()
                .getRecipeFor(TankCrafterRecipe.Type.INSTANCE, inventory, world);

        return match.isPresent()
                && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem());

    }

    private static void craftItem(CrafterBlockEntity entity) {
        Level world = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.inventory.getSlots());
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            inventory.setItem(i, entity.getItemInSlot(i));
        }

        Optional<TankCrafterRecipe> match = world.getRecipeManager()
                .getRecipeFor(TankCrafterRecipe.Type.INSTANCE, inventory, world);

        if (match.isPresent()) {
            entity.extractItem(0);
            entity.extractItem(1);
            entity.extractItem(2);
            entity.extractItem(3);
            entity.extractItem(4);
            entity.extractItem(5);
            entity.extractItem(6);
            entity.insertItem(8, new ItemStack(match.get().getResultItem().getItem(),
                    entity.getItemInSlot(8).getCount() + 1));
        }
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(8).getItem() == output.getItem() || inventory.getItem(8).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(8).getCount() > inventory.getItem(8).getCount();
    }

}