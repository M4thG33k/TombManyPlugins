package com.m4thg33k.tombmanyplugins_cosmeticarmorreworked;

import com.m4thg33k.tombmanygraves.api.events.EventRegisterSpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.ISpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.inventory.TransitionInventory;
import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = TombManyPluginsCosmeticArmorReworked.MODID,
        name = TombManyPluginsCosmeticArmorReworked.MODNAME,
        version = TombManyPluginsCosmeticArmorReworked.VERSION,
        dependencies = TombManyPluginsCosmeticArmorReworked.DEPENDENCIES
)
public class TombManyPluginsCosmeticArmorReworked {
    public static final String MODID = "tombmanypluginscosmeticarmorreworked";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPlugins Wearable Backpacks";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:cosmeticarmorreworked;required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsCosmeticArmorReworked INSTANCE = new TombManyPluginsCosmeticArmorReworked();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void registerSpecialInventories(EventRegisterSpecialInventory e) throws Exception {
        e.registerSpecialInventory(new CosmeticArmorSpecialInventory());
    }


    public class CosmeticArmorSpecialInventory implements ISpecialInventory {

        @Override
        public String getUniqueIdentifier() {
            return "CosmeticArmorReworkedInventory";
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            return true; // No logic to stop graves
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            return SpecialInventoryHelper
                    .getTagListFromIInventory(
                            CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID())
                    );
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagList) {
                TransitionInventory graveItems = new TransitionInventory((NBTTagList)compound);
                IInventory currentInventory = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());

                for (int i=0; i<graveItems.getSizeInventory(); i++){
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (!graveItem.isEmpty()){
                        ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()){
                            // No problem, just put the grave item in!
                            currentInventory.setInventorySlotContents(i, graveItem);
                        } else if (shouldForce){
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
            if (compound instanceof NBTTagList){
                return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
            } else {
                return new ArrayList<ItemStack>();
            }
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "Cosmetic Armor";
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


