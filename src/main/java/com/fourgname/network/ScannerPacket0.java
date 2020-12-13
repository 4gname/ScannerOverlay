package com.fourgname.network;

import com.fourgname.core.ScannerBase;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import gregtech.common.GT_UndergroundOil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;

public class ScannerPacket0 extends ScannerPacket {

    private int playerID, dimID;

    public ScannerPacket0() {
    }

    public ScannerPacket0(int dimensionId, int aPlayerID) {
        this.dimID = dimensionId;
        this.playerID = aPlayerID;
    }

    public ScannerPacket0(EntityPlayer p) {
        this.dimID = p.worldObj.provider.dimensionId;
        this.playerID = p.getEntityId();
    }

    @Override
    public int getPacketID() {
        return 0;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(10);
        tOut.writeInt(dimID);
        tOut.writeInt(playerID);

        return tOut.toByteArray();
    }

    @Override
    public Object decode(ByteArrayDataInput aData) {
        return new ScannerPacket0(aData.readInt(), aData.readInt());
    }

    @Override
    public void process() {
        World w = DimensionManager.getWorld(dimID);
        if (w != null && w.getEntityByID(playerID) instanceof EntityPlayerMP) {
            if (Loader.isModLoaded("gregtech"))
                checkOil((EntityPlayer) w.getEntityByID(playerID));

        }
    }

    public void checkOil(EntityPlayer mc) {
        if (!mc.worldObj.isRemote) {
            int pX = ((int) mc.posX) >> 4;
            int pZ = ((int) mc.posZ) >> 4;
            World world = mc.worldObj;
            FluidStack underOil = GT_UndergroundOil.undergroundOilReadInformation(world.getChunkFromChunkCoords(pX, pZ));
            if (underOil != null) {
                ScannerBase.setFluidStack(underOil);
            }
        }
    }

}