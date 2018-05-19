package com.m4thg33k.tombmanyplugins_galacticraft;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC;
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = TombManyPluginsGalacticraft.MODID,
        name = TombManyPluginsGalacticraft.MODNAME,
        version = TombManyPluginsGalacticraft.VERSION,
        dependencies = TombManyPluginsGalacticraft.DEPENDENCIES
)
public class TombManyPluginsGalacticraft {
    public static final String MODID = "tombmanypluginsgalacticraft";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPlugins Galacticraft";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:galacticraftcore@[4.0.1.176,);required-after:tombmanygraves2api@[1.12-4.1.0,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsGalacticraft INSTANCE = new TombManyPluginsGalacticraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        new GalacticraftSpecialInventory();
    }


    class GalacticraftSpecialInventory extends AbstractSpecialInventory{
        @Override
        public String getUniqueIdentifier() {
            return "GalacticraftInventory";
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
            return true;
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            return SpecialInventoryHelper.getTagListFromIInventory(AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP)player));
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagList && player instanceof EntityPlayerMP){
                TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);
                IInventoryGC inventoryGC = AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP) player);

                for (int i=0; i<graveItems.getSizeInventory(); i++){
                    ItemStack graveItem = graveItems.getStackInSlot(i);

                    if (!graveItem.isEmpty()){
                        ItemStack playerItem = inventoryGC.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()){
                            // No problem, just put the grave item in!
                            inventoryGC.setInventorySlotContents(i, graveItem);
                        } else if (shouldForce){
                            // Slot is blocked, but we're forcing it
                            inventoryGC.setInventorySlotContents(i, graveItem);
                            SpecialInventoryHelper.dropItem(player, playerItem);
                        } else {
                            // Slot is blocked, but not forcing
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
            return "Galacticraft";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0;
        }
    }
}
