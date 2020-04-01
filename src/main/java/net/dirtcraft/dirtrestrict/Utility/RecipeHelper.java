package net.dirtcraft.dirtrestrict.Utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import scala.collection.mutable.MultiMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class RecipeHelper {
    private Multimap<ItemKey, IRecipe> removedRecipes = ArrayListMultimap.create();
    private Multimap<ItemKey, Map.Entry<ItemStack, ItemStack>> removedRecipesF = ArrayListMultimap.create();

    private void removeCraftingRecipe(ItemKey key){
        List<IRecipe> masterList = CraftingManager.getInstance().getRecipeList();
        List<IRecipe> recipes = masterList.stream()
                .filter(recipe->recipe.getRecipeOutput().getItem() == key.getItem())
                .filter(recipe->key.data == null || recipe.getRecipeOutput().getMetadata() == key.data)
                .collect(Collectors.toList());
        masterList.removeAll(recipes);
        removedRecipes.putAll(key, recipes);
    }

    private void restoreCraftingRecipe(ItemKey key){
        List<IRecipe> craftingList = CraftingManager.getInstance().getRecipeList();
        craftingList.addAll(removedRecipes.get(key));
        removedRecipes.removeAll(key);
    }

    private void removeSmeltingRecipe(ItemKey key){
        Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.instance().getSmeltingList();
        List<Map.Entry<ItemStack, ItemStack>> entries = smeltingList.entrySet().stream()
                .filter(s->s.getValue().getItem() == key.getItem())
                .filter(s->key.data == null || s.getValue().getMetadata() == key.data)
                .peek(k->smeltingList.remove(k.getKey()))
                .collect(Collectors.toList());
        removedRecipesF.putAll(key, entries);
    }

    private void restoreSmeltingRecipe(ItemKey key){
        Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.instance().getSmeltingList();
        removedRecipesF.get(key).forEach(v->smeltingList.put(v.getKey(), v.getValue()));
        removedRecipesF.removeAll(key);
    }
}
