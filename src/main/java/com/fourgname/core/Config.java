package com.fourgname.core;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.ArrayList;

public class Config {
    public static boolean loadConfig;
    public static Configuration config;

    public static boolean mCheckOil;
    public static int mCheckOilMax;
    public static int mCheckOilMin;
    public static boolean mCheckSmallOres;

    public Config(File file) {
        if (!loadConfig) {
            config = new Configuration(file);
            syncConfig(true);
        }
    }

    public static void syncConfig(boolean load) {
        ArrayList<String> GregTech = new ArrayList<String>();
        try {
            if (!config.isChild && load) {
                config.load();
            }

            Property cfg;

            cfg = config.get("GregTech", "GTUnderFluids", false);
            cfg.comment = "Enable of GregTech Underground Fluids. [Default: false]";
            mCheckOil = cfg.getBoolean(false);
            GregTech.add(cfg.getName());

            cfg = config.get("GregTech", "GTUnderFluids_Max", 100);
            cfg.comment = "Display max amount of Fluids. [Default: 100]";
            mCheckOilMax = cfg.getInt(100);
            GregTech.add(cfg.getName());

            cfg = config.get("GregTech", "GTUnderFluids_Min", 0);
            cfg.comment = "Display min amount of Fluids. [Default: 0]";
            mCheckOilMin = cfg.getInt(0);
            GregTech.add(cfg.getName());

            cfg = config.get("GregTech", "GTSmallOres", false);
            cfg.comment = "Enable of GregTech Small Ores. [Default: false]";
            mCheckSmallOres = cfg.getBoolean(false);
            GregTech.add(cfg.getName());

            config.setCategoryPropertyOrder("GregTech", GregTech);

            if (config.hasChanged()) config.save();

        } catch (Exception e) {
            FMLLog.log(Level.ERROR, e, "Error load config!");
        }
    }
}
