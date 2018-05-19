package com.m4thg33k.tombmanyplugins_wearablebackpacks;


import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.mcft.copy.backpacks.misc.BackpackSize;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = TombManyPluginsWearableBackpacks.MODID,
        name = TombManyPluginsWearableBackpacks.MODNAME,
        version = TombManyPluginsWearableBackpacks.VERSION,
        dependencies = TombManyPluginsWearableBackpacks.DEPENDENCIES
)
public class TombManyPluginsWearableBackpacks {
    public static final String MODID = "tombmanypluginswearablebackpacks";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPlugins Wearable Backpacks";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:wearablebackpacks@[3.1.2,);required-after:tombmanygraves2api@[1.12-4.1.0,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsWearableBackpacks INSTANCE = new TombManyPluginsWearableBackpacks();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        new BackpackSpecialInventory();
    }



    public class BackpackSpecialInventory extends AbstractSpecialInventory {
        private static final String BASE = "Base";
        private static final String STACK = "Stack";

        @Override
        public String getUniqueIdentifier() {
            return "WBInventory";
        }

        @Override
        public int getPriority() {
            // Needs to happen before Vanilla to deal with backpacks in the chest slot
            return 1;
        }

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            return true; // Backpacks have no logic to stop graves
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {

            IBackpack backpack = player.getCapability(IBackpack.CAPABILITY, (EnumFacing) null);

            if (backpack != null && backpack.getData() != null) {
                NBTBase base = backpack.getData().serializeNBT();
                NBTTagCompound compound = new NBTTagCompound();
                compound.setTag(BASE, base);

                ItemStack stack = backpack.getStack();

                if (! stack.isEmpty() || SpecialInventoryHelper.isItemValidForGrave(stack)) {
                    NBTTagCompound stackTag = new NBTTagCompound();
                    stack.writeToNBT(stackTag);
                    compound.setTag(STACK, stackTag);

                    BackpackHelper.setEquippedBackpack(player, ItemStack.EMPTY, (IBackpackData) null);

                    return compound;
                }

            }

            return null;
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagCompound && ((NBTTagCompound) compound).hasKey(STACK)) {
                ItemStack stack = new ItemStack(((NBTTagCompound) compound).getCompoundTag(STACK));
                NBTTagCompound base = ((NBTTagCompound) compound).getCompoundTag(BASE);
                BackpackSize size = BackpackSize.parse(base.getTag("size"));

                IBackpackData data = new BackpackDataItems(size);
                data.deserializeNBT(base);

                if (BackpackHelper.canEquipBackpack(player)) {
                    // If we can equip the backpack. Do it!
                    BackpackHelper.setEquippedBackpack(player, stack, data);
                } else if (! shouldForce) {
                    // We can't equip the backpack naturally and we're not forcing it, so we drop the backpack
                    // and its inventory on the ground.
                    dropBackpackAndInventoryOnGround(player, stack, data);
                } else {
                    // Can't equip naturally, but we want to force it.
                    IBackpack currentlyEquipped = BackpackHelper.getBackpack(player);
                    if (currentlyEquipped != null) {
                        // There is a backpack equipped. Drop it.
                        dropBackpackAndInventoryOnGround(player, currentlyEquipped.getStack(), currentlyEquipped.getData());
                    } else {
                        // There's something in the chest slot
                        SpecialInventoryHelper.dropItem(player, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
                    }

                    // Now we can equip the saved one
                    BackpackHelper.setEquippedBackpack(player, stack, data);
                }
            }
        }

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound) {
            List<ItemStack> items = new ArrayList<ItemStack>();

            if (compound instanceof NBTTagCompound && ((NBTTagCompound) compound).hasKey(STACK)) {
                ItemStack packStack = new ItemStack(((NBTTagCompound) compound).getCompoundTag(STACK));

                if (packStack.isEmpty()) {
                    return items;
                }

                items.add(packStack);

                NBTTagCompound base = (NBTTagCompound) ((NBTTagCompound) compound).getCompoundTag(BASE);
                BackpackSize size = BackpackSize.parse(base.getTag("size"));

                IBackpackData data = new BackpackDataItems(size);
                data.deserializeNBT(base);

                ItemStackHandler stackHandler = ((BackpackDataItems) data).getItems();

                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (! stack.isEmpty()) {
                        items.add(stack);
                    }
                }
            }

            return items;
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "Wearable Backpacks";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0x87703A;
        }

        private void dropBackpackAndInventoryOnGround(EntityPlayer player, ItemStack packStack, IBackpackData data) {
            if (packStack.isEmpty() || data == null) {
                return;
            }

            ItemStackHandler items = ((BackpackDataItems) data).getItems();

            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack stack = items.getStackInSlot(i);
                if (! stack.isEmpty()) {
                    SpecialInventoryHelper.dropItem(player, stack);
                }
            }

            SpecialInventoryHelper.dropItem(player, packStack);
        }

        @Override
        public boolean isOverwritable() {
            return true;
        }
    }
}


