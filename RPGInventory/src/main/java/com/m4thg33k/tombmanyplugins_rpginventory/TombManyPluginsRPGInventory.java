package com.m4thg33k.tombmanyplugins_rpginventory;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.rpginventory.capability.playerinventory.RpgInventoryData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = TombManyPluginsRPGInventory.MODID,
        name = TombManyPluginsRPGInventory.MODNAME,
        version = TombManyPluginsRPGInventory.VERSION,
        dependencies = TombManyPluginsRPGInventory.DEPENDENCIES
)
public class TombManyPluginsRPGInventory {
    public static final String MODID = "tombmanypluginsrpginventory";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPluginsRPGInventory";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:rpginventory@[1.12.2,);required-after:tombmanygraves2api@[1.12-4.1.0,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsRPGInventory INSTANCE = new TombManyPluginsRPGInventory();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        new RPGInventorySpecialInventory();
    }


    class RPGInventorySpecialInventory extends AbstractSpecialInventory{
        @Override
        public String getUniqueIdentifier() {
            return "RPGInventory";
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public boolean isOverwritable() {
            return true;
        }

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            return true; // don't stop grave logic
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            RpgInventoryData inventoryData = RpgInventoryData.get(player);

            if (inventoryData != null){
                ItemStackHandler data = inventoryData.getInventory();

                TransitionInventory transitionInventory = new TransitionInventory(data.getSlots());
                for (int i=0; i <data.getSlots(); i++){
                    ItemStack stack = data.getStackInSlot(i);
                    if (SpecialInventoryHelper.isItemValidForGrave(stack)){
                        transitionInventory.setInventorySlotContents(i, stack);
                        data.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                return transitionInventory.writeToTagList(new NBTTagList());
            }
            return null;
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            RpgInventoryData inventoryData = RpgInventoryData.get(player);
            if (compound instanceof NBTTagList && inventoryData != null){
                TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);

                ItemStackHandler currentInventory =  inventoryData.getInventory();

                for (int i=0; i<graveItems.getSizeInventory(); i++){
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (!graveItem.isEmpty()){
                        ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()){
                            currentInventory.setStackInSlot(i, graveItem);
                        } else if (shouldForce){
                            currentInventory.setStackInSlot(i, graveItem);
                            SpecialInventoryHelper.dropItem(player, playerItem);
                        } else {
                            SpecialInventoryHelper.dropItem(player, graveItem);
                        }
                    }
                }
            }
        }

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound) {
            if (compound instanceof NBTTagList){
                return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
            }
            return new ArrayList<ItemStack>();
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "RPG Inventory";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0;
        }
    }
}


