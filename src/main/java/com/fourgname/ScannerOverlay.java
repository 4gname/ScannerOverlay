package com.fourgname;

import com.fourgname.core.Config;
import com.fourgname.core.ScannerBase;
import com.fourgname.network.ScannerNetwork;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.Collections;


@Mod(
        name = ScannerOverlay.NAME,
        version = ScannerOverlay.VERSION,
        modid = ScannerOverlay.VERSION
)
public class ScannerOverlay {

    public static final String MODID = "scov";
    public static final String VERSION = "0.0.1";
    public static final String NAME = "Scanner Overlay";

    @Mod.Instance("scov")
    public static ScannerOverlay instance;
    public static Config mConfig;

    public ScannerOverlay() {
        new ScannerNetwork();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        mConfig = new Config(new File("config/ScannerOverlay.cfg"));
        ScannerBase.preInit();
        this.initModInfo(event.getModMetadata());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    private void initModInfo(ModMetadata info) {
        info.autogenerated = false;
        info.modId = ScannerOverlay.MODID;
        info.name = ScannerOverlay.NAME;
        info.version = ScannerOverlay.VERSION;
        info.url = "https://www.curseforge.com/minecraft/mc-mods/orescanner";
        info.logoFile = "https://media.forgecdn.net/avatars/322/509/637434355386426959.png";
        info.authorList = Collections.singletonList("4gname");
    }
}