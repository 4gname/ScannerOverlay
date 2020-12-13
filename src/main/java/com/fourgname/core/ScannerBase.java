package com.fourgname.core;

import com.fourgname.network.ScannerNetwork;
import com.fourgname.network.ScannerPacket0;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ScannerBase {
    public static KeyBinding checkOre;
    public static ScannerBase baseClassInstance;
    public static boolean isScan = false;
    public static FluidStack udnerOil = null;
    private static Minecraft mc = Minecraft.getMinecraft();
    Map chunkInfo = new HashMap<>();
    int counter;

    public static void preInit() {
        FMLCommonHandler.instance().bus().register(new ScannerBase());
        MinecraftForge.EVENT_BUS.register(new ScannerBase());
        ScannerBase.init(new ScannerBase());
    }

    public static void init(ScannerBase baseClassInstance) {
        ScannerBase.baseClassInstance = baseClassInstance;
        checkOre = new KeyBinding("Scan on/off", 44, "Scanner Overlay");
        ClientRegistry.registerKeyBinding(checkOre);
    }

    public static void setFluidStack(FluidStack aOil) {
        udnerOil = aOil;
    }

    public void SetScan(boolean value) {
        EnumChatFormatting[] color = new EnumChatFormatting[]{
                EnumChatFormatting.GREEN,
                EnumChatFormatting.WHITE,
                EnumChatFormatting.RED
        };
        String[] message;
        if (value) {
            message = new String[]{
                    "[Scan] On",
                    "[A] - Amount, [Y] - Y Coord"
            };
        } else message = new String[]{"[Scan] Off"};
        for (int i = 0; i < message.length; i++) {
            ChatComponentText chatComponentText = new ChatComponentText(message[i]);
            ChatStyle chatComponentStyle = chatComponentText.getChatStyle();
            if (value) chatComponentStyle.setColor(color[i]);
            else chatComponentStyle.setColor(color[2]);

            mc.thePlayer.addChatMessage(chatComponentText);
        }
        isScan = value;
        if (isScan) scan();
    }

    public void scan() {
        this.chunkInfo.clear();
        int chunkCoordX = mc.thePlayer.chunkCoordX;
        int chunkCoordZ = mc.thePlayer.chunkCoordZ;
        World world = mc.thePlayer.worldObj;

        if (Loader.isModLoaded("gregtech") && Config.mCheckOil) {
            ScannerNetwork.INSTANCE.sendToServer(new ScannerPacket0(mc.thePlayer));
        }

        for (int x = chunkCoordX * 16; x <= chunkCoordX * 16 + 16; x++) {
            for (int z = chunkCoordZ * 16; z <= chunkCoordZ * 16 + 16; z++) {
                for (int y = 0; y <= world.getActualHeight(); y++) {

                    Block block = world.getBlock(x, y, z);
                    String blockName = block.getLocalizedName();

                    if (blockName.contains("ore") || block instanceof BlockOre) {
                        int meta = world.getBlockMetadata(x, y, z);

                        if (!Loader.isModLoaded("gregtech")) {
                            for (ItemStack b : world.getBlock(x, y, z).getDrops(world, x, y, z, meta, 0)) {
                                if (this.chunkInfo.containsKey(b.getDisplayName())) {

                                    Data oreData1 = (Data) this.chunkInfo.get(b.getDisplayName());
                                    oreData1.count++;
                                    if (oreData1.height < y) {
                                        oreData1.height = y;
                                    }
                                    continue;
                                }
                                this.chunkInfo.put(b.getDisplayName(), new Data());
                                Data Data = (Data) this.chunkInfo.get(b.getDisplayName());
                                Data.height = y;
                            }
                        }

                        if (Loader.isModLoaded("gregtech")) {
                            for (ItemStack b : world.getBlock(x, y, z).getDrops(world, x, y, z, meta, 0)) {
                                if (Config.mCheckSmallOres && !b.getDisplayName().contains("stone")) {
                                    if (this.chunkInfo.containsKey(b.getDisplayName())) {
                                        Data oreData1 = (Data) this.chunkInfo.get(b.getDisplayName());
                                        oreData1.count++;
                                        if (oreData1.height < y) {
                                            oreData1.height = y;
                                        }
                                        continue;
                                    }
                                    this.chunkInfo.put(b.getDisplayName(), new Data());
                                    Data Data = (Data) this.chunkInfo.get(b.getDisplayName());
                                    Data.height = y;
                                }
                            }

                            for (ItemStack drop : block.getDrops(world, x, y, z, meta, 0)) {
                                if (drop.getDisplayName().contains("Ore") && !drop.getDisplayName().contains("Crushed")) {
                                    if (this.chunkInfo.containsKey(drop.getDisplayName())) {
                                        Data oreData1 = (Data) this.chunkInfo.get(drop.getDisplayName());
                                        oreData1.count++;
                                        if (oreData1.height < y) {
                                            oreData1.height = y;
                                        }
                                        continue;
                                    }
                                    this.chunkInfo.put(drop.getDisplayName(), new Data());
                                    Data Data = (Data) this.chunkInfo.get(drop.getDisplayName());
                                    Data.height = y;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (isScan) {

            this.counter++;
            if (this.counter % 500 == 0) {
                scan();
                this.counter = 0;
            }
            if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
                ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                FontRenderer fontRender = mc.fontRenderer;
                int height = res.getScaledHeight();
                mc.entityRenderer.setupOverlayRendering();

                int x = 0;
                int y = height;

                y -= fontRender.FONT_HEIGHT * 16;

                String text;
                for (Object key : this.chunkInfo.keySet()) {

                    Data oreData = (Data) this.chunkInfo.get(key);

                    text = " " + EnumChatFormatting.WHITE + key.toString() + " - A: " +
                            EnumChatFormatting.YELLOW + oreData.count + EnumChatFormatting.WHITE + " Y: " +
                            EnumChatFormatting.YELLOW + oreData.height + EnumChatFormatting.RESET;
                    y -= fontRender.FONT_HEIGHT;
                    fontRender.drawStringWithShadow(text, x, y, 0);
                }

                if (udnerOil != null) {
                    if (udnerOil.amount > Config.mCheckOilMin && udnerOil.amount < Config.mCheckOilMax) {
                        text = " ▉ " + EnumChatFormatting.WHITE + udnerOil.getLocalizedName() +
                                " - A: " + EnumChatFormatting.YELLOW + udnerOil.amount + EnumChatFormatting.WHITE + " L" + EnumChatFormatting.RESET;
                        y -= fontRender.FONT_HEIGHT;
                        fontRender.drawStringWithShadow(text, x, y, udnerOil.getFluid().getColor(udnerOil));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (checkOre.isPressed()) SetScan(!isScan);
    }

    public class Data {
        public int height = 0;
        public int amount = 0;
        public int count = 1;
    }


}
