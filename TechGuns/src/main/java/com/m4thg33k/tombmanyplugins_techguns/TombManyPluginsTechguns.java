package com.m4thg33k.tombmanyplugins_techguns;

import com.m4thg33k.tombmanygraves.api.events.EventRegisterSpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.ISpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.inventory.TransitionInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import techguns.capabilities.TGExtendedPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = TombManyPluginsTechguns.MODID,
        name = TombManyPluginsTechguns.MODNAME,
        version = TombManyPluginsTechguns.VERSION,
        dependencies = TombManyPluginsTechguns.DEPENDENCIES
)
public class TombManyPluginsTechguns {
    public static final String MODID = "tombmanypluginstechguns";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPlugins Techguns";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:techguns@[2.0.1.1,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsTechguns INSTANCE = new TombManyPluginsTechguns();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void registerSpecialInventories(EventRegisterSpecialInventory e) throws Exception{
        e.registerSpecialInventory(new SpecialInventoryTechguns());
    }

    class SpecialInventoryTechguns implements ISpecialInventory{
        @Override
        public String getUniqueIdentifier() {
            return "TechgunsInventory";
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
            return SpecialInventoryHelper.getTagListFromIInventory(TGExtendedPlayer.get(player).getTGInventory());
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagList){
                TransitionInventory graveItems = new TransitionInventory((NBTTagList)compound);
                IInventory currentItems = TGExtendedPlayer.get(player).getTGInventory();

                for (int i=0; i<graveItems.getSizeInventory(); i++){
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (!graveItem.isEmpty()){
                        ItemStack playerItem = currentItems.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()){
                            currentItems.setInventorySlotContents(i, graveItem);
                        } else if (shouldForce){
                            currentItems.setInventorySlotContents(i, graveItem);
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
                return (new TransitionInventory((NBTTagList)compound)).getListOfNonEmptyItemStacks();
            } else {
                return new ArrayList<ItemStack>();
            }
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "Techguns";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0;
        }
    }

}
