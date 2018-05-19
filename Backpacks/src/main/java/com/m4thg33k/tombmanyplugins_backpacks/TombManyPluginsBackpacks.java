package com.m4thg33k.tombmanyplugins_backpacks;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import de.eydamos.backpack.data.PlayerSave;
import de.eydamos.backpack.helper.BackpackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = TombManyPluginsBackpacks.MODID,
        name = TombManyPluginsBackpacks.MODNAME,
        version = TombManyPluginsBackpacks.VERSION,
        dependencies = TombManyPluginsBackpacks.DEPENDENCIES
)
public class TombManyPluginsBackpacks {
    public static final String MODID = "tombmanypluginsbackpacks";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPluginsBackpacks";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:backpack@[3.0.2,);required-after:tombmanygraves2api@[1.12-4.1.0,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsBackpacks INSTANCE = new TombManyPluginsBackpacks();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // Register the inventory
        new BackpackSpecialInventory();
    }


    public class BackpackSpecialInventory extends AbstractSpecialInventory {
        private static final String BASE = "Base";
        private static final String STACK = "Stack";

        @Override
        public String getUniqueIdentifier() {
            return "EydamosBackpacksInventory";
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            return true; // no logic to stop graves
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            ItemStack backpack = BackpackHelper.getBackpackFromPlayer(player, false); // get backpack in slot
            if (! backpack.isEmpty()) {
                return SpecialInventoryHelper.getTagListFromIInventory(PlayerSave.loadPlayer(player.getEntityWorld(), player));
            } else {
                return null;
            }
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagList) {
                TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);
                IInventory currentInventory = PlayerSave.loadPlayer(player.getEntityWorld(), player);

                for (int i = 0; i < graveItems.getSizeInventory(); i++) {
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (! graveItem.isEmpty()) {
                        ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()) {
                            // No problem, just put the grave item in!
                            currentInventory.setInventorySlotContents(i, graveItem);
                        } else if (shouldForce) {
                            // Slot is blocked, but we're forcing the grave item into place.
                            currentInventory.setInventorySlotContents(i, graveItem);
                            SpecialInventoryHelper.dropItem(player, playerItem);
                        } else {
                            // Slot is blocked, but we're not forcing items in - drop the grave item
                            SpecialInventoryHelper.dropItem(player, graveItem);
                        }
                    }
                }
            }
        }

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound) {
            if (compound instanceof NBTTagList) {
                return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
            } else {
                return new ArrayList<ItemStack>();
            }
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "Backpacks";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0;
        }

        @Override
        public boolean isOverwritable() {
            return true;
        }
    }
}


