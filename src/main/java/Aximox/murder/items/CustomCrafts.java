package Aximox.murder.items;

import Aximox.murder.Murder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CustomCrafts {

    public void canon(){
        ItemStack result = Murder.getInstance().getCustomItems().canon();
        ShapedRecipe canon = new ShapedRecipe(new NamespacedKey(Murder.getInstance(), "canon"), result);

        canon.shape(
                "NNI",
                "NGN",
                "INN"
        );

        canon.setIngredient('N', Material.NETHERITE_INGOT);
        canon.setIngredient('I', Material.IRON_BLOCK);
        canon.setIngredient('G', Material.GUNPOWDER);

        Bukkit.addRecipe(canon);
    }

    public void fruit(){
        ItemStack result = Murder.getInstance().getCustomItems().fruit();
        ShapedRecipe fruit = new ShapedRecipe(new NamespacedKey(Murder.getInstance(), "fruit"), result);

        fruit.shape(
                "CSC",
                "CFC",
                "CCC"
        );
        fruit.setIngredient('C', Material.CHORUS_FRUIT);
        fruit.setIngredient('S', Material.STICK);
        fruit.setIngredient('F', Material.CHORUS_FLOWER);

        Bukkit.addRecipe(fruit);
    }
}
