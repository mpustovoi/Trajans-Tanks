package com.crescentine.trajanstanks.integration.jei_compat;

import com.crescentine.trajanstanks.TankMod;
import com.crescentine.trajanstanks.item.TankModItems;
import com.crescentine.trajanstanks.recipe.EngineFabricatorRecipe;
import com.crescentine.trajanstanks.recipe.PlatingPressRecipe;
import com.crescentine.trajanstanks.recipe.TankCrafterRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;


@JeiPlugin
public class TankModJei implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TankMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(VanillaTypes.ITEM, new ItemStack(TankModItems.ENGINE_FABRICATOR.get()), EngineFabricatorRecipeCategory.UID);
        registration.addRecipeCatalyst(VanillaTypes.ITEM, new ItemStack(TankModItems.PLATE_PRESS_BLOCK.get()), PlatingPressRecipeCategory.UID);
        registration.addRecipeCatalyst(VanillaTypes.ITEM, new ItemStack(TankModItems.CRAFTER_BLOCK.get()), TankCrafterRecipeCategory.UID);

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new EngineFabricatorRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new TankCrafterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PlatingPressRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<TankCrafterRecipe> tankCrafterRecipes = rm.getAllRecipesFor(TankCrafterRecipe.Type.INSTANCE);
        List<PlatingPressRecipe> platingPressRecipes = rm.getAllRecipesFor(PlatingPressRecipe.Type.INSTANCE);
        List<EngineFabricatorRecipe> engineFabricatorRecipes = rm.getAllRecipesFor(EngineFabricatorRecipe.Type.INSTANCE);

        registration.addRecipes(new RecipeType<>(EngineFabricatorRecipeCategory.UID, EngineFabricatorRecipe.class), engineFabricatorRecipes);
        registration.addRecipes(new RecipeType<>(PlatingPressRecipeCategory.UID, PlatingPressRecipe.class), platingPressRecipes);
        registration.addRecipes(new RecipeType<>(TankCrafterRecipeCategory.UID, TankCrafterRecipe.class), tankCrafterRecipes);
    }
}