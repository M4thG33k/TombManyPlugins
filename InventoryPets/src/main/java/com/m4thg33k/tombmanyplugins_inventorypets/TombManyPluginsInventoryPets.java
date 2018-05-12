package com.m4thg33k.tombmanyplugins_inventorypets;


import com.inventorypets.InventoryPets;
import com.inventorypets.capabilities.CapabilityRefs;
import com.inventorypets.capabilities.ICapabilityPlayer;
import com.m4thg33k.tombmanygraves.api.events.EventRegisterSpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.ISpecialInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = TombManyPluginsInventoryPets.MODID,
        name = TombManyPluginsInventoryPets.MODNAME,
        version = TombManyPluginsInventoryPets.VERSION,
        dependencies = TombManyPluginsInventoryPets.DEPENDENCIES
)
public class TombManyPluginsInventoryPets {
    public static final String MODID = "tombmanypluginsinventorypets";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPlugins Inventory Pets";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:inventorypets@[1.4.9.9b,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsInventoryPets INSTANCE = new TombManyPluginsInventoryPets();

    @Mod.EventHandler
    public void init (FMLInitializationEvent e){
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void registerSpecialInventories(EventRegisterSpecialInventory e) throws Exception {
        e.registerSpecialInventory(new InventoryPetsSpecialInventory());
    }


    class InventoryPetsSpecialInventory implements ISpecialInventory{
        @Override
        public String getUniqueIdentifier() {
            return "InventoryPets";
        }

        @Override
        public int getPriority() {
            return 0; // doesn't actually do any grabbing of items; just pregrabLogic
        }

        @Override
        public boolean isOverwritable() {
            return true;
        }

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            // If the grave pet is active; stop grave logic

            boolean gravePetKeepsInventory = false;
            for (int i=0; i< 10; i++){
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == InventoryPets.petGrave && stack.getItemDamage() == 0){
                    gravePetKeepsInventory = true;
                    break;
                }
            }

            if (!gravePetKeepsInventory){
                ICapabilityPlayer props = CapabilityRefs.getPlayerCaps(player);
                props.setRestoreItems(false);
            }

            return !gravePetKeepsInventory;
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            return null;
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {

        }

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound) {
            return new ArrayList<ItemStack>();
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "Inventory Pets";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0;
        }
    }

}
