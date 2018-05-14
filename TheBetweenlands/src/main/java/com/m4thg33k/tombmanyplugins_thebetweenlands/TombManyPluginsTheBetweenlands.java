package com.m4thg33k.tombmanyplugins_thebetweenlands;

import com.m4thg33k.tombmanygraves.api.events.EventRegisterSpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.ISpecialInventory;
import com.m4thg33k.tombmanygraves.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.inventory.TransitionInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = TombManyPluginsTheBetweenlands.MODID,
        name = TombManyPluginsTheBetweenlands.MODNAME,
        version = TombManyPluginsTheBetweenlands.VERSION,
        dependencies = TombManyPluginsTheBetweenlands.DEPENDENCIES
)
public class TombManyPluginsTheBetweenlands {
    public static final String MODID = "tombmanypluginsthebetweenlands";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyPluginsTheBetweenlands";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:thebetweenlands@[3.3.8,);required-after:tombmanygraves@[1.12-4.1.0,)";

    @Mod.Instance
    public static TombManyPluginsTheBetweenlands INSTANCE = new TombManyPluginsTheBetweenlands();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void registerSpecialInventories(EventRegisterSpecialInventory e) throws Exception {
        e.registerSpecialInventory(new BetweenlandsEquipmentInventory());
    }

    class BetweenlandsEquipmentInventory implements ISpecialInventory {
        @Override
        public String getUniqueIdentifier() {
            return "TheBetweenlandsEquipment";
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
            return true; // allow graves to form
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            IEquipmentCapability equipmentCapability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
            if (equipmentCapability != null) {
                NBTTagCompound compound = new NBTTagCompound();
                boolean setTag = false;
                EnumEquipmentInventory[] equipmentInventories = EnumEquipmentInventory.values();

                for (EnumEquipmentInventory type : equipmentInventories) {
                    IInventory inv = equipmentCapability.getInventory(type);

                    NBTTagList tagList = SpecialInventoryHelper.getTagListFromIInventory(inv);
                    if (tagList != null) {
                        compound.setTag(type.ordinal() + "", tagList);
                        setTag = true;
                    }
                }

                if (setTag) {
                    return compound;
                } else {
                    return null;
                }
            }
            return null;
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagCompound) {
                EnumEquipmentInventory[] equipmentInventories = EnumEquipmentInventory.values();
                IEquipmentCapability equipmentCapability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

                if (equipmentCapability != null) {

                    for (EnumEquipmentInventory type : equipmentInventories) {
                        if (((NBTTagCompound) compound).hasKey(type.ordinal() + "")) {
                            NBTTagList tagList = (NBTTagList) ((NBTTagCompound) compound).getTag(type.ordinal() + "");

                            TransitionInventory graveItems = new TransitionInventory(tagList);
                            IInventory currentInventory = equipmentCapability.getInventory(type);

                            for (int i = 0; i < graveItems.getSizeInventory(); i++) {
                                ItemStack graveItem = graveItems.getStackInSlot(i);

                                if (type == EnumEquipmentInventory.AMULET && i >= equipmentCapability.getAmuletSlots()){
                                    SpecialInventoryHelper.dropItem(player, graveItem);
                                    continue;
                                }

                                if (! graveItem.isEmpty()) {
                                    ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                                    if (playerItem.isEmpty()) {
                                        currentInventory.setInventorySlotContents(i, graveItem);
                                    } else if (shouldForce) {
                                        currentInventory.setInventorySlotContents(i, graveItem);
                                        SpecialInventoryHelper.dropItem(player, playerItem);
                                    } else {
                                        SpecialInventoryHelper.dropItem(player, graveItem);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound) {
            List<ItemStack> ret = new ArrayList<ItemStack>();

            if (compound instanceof NBTTagCompound) {
                EnumEquipmentInventory[] equipmentInventories = EnumEquipmentInventory.values();


                for (EnumEquipmentInventory type : equipmentInventories) {
                    if (((NBTTagCompound) compound).hasKey(type.ordinal() + "")) {
                        NBTTagList tagList = (NBTTagList) ((NBTTagCompound) compound).getTag(type.ordinal() + "");

                        ret.addAll((new TransitionInventory(tagList)).getListOfNonEmptyItemStacks());
                    }
                }
            }

            return ret;
        }

        @Override
        public String getInventoryDisplayNameForGui() {
            return "Betweenlands Equipment";
        }

        @Override
        public int getInventoryDisplayNameColorForGui() {
            return 0;
        }
    }

}


