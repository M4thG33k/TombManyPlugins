package com.m4thg33k.tombmanyplugins_techguns;

import com.m4thg33k.tombmanygraves.api.events.EventRegisterSpecialInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    }

}
