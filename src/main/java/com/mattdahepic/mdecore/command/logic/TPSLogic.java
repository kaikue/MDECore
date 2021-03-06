package com.mattdahepic.mdecore.command.logic;

import com.mattdahepic.mdecore.command.AbstractCommand;
import com.mattdahepic.mdecore.command.AbstractSingleLogicCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class TPSLogic extends AbstractSingleLogicCommand {
    public static TPSLogic instance = new TPSLogic();

    @Override
    public String getCommandName () {
        return "tps";
    }
    @Override
    public int getPermissionLevel () {
        return 0;
    }
    @Override
    public String getCommandSyntax () {
        return "/mde tps [{o | a | <dimension>}]";
    }
    @Override
    public void handleCommand (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args[0].equals(getCommandName())) args = ArrayUtils.remove(args,0);
        if (args.length == 1 || args.length == 0) { //empty arguments
            double tps = getTps(server,null);
            double tickms = getTickMs(server,null);

            sender.addChatMessage(new TextComponentString(String.format("Overall: %.2f TPS/%.2fMS (%s%%)", tps, tickms, (int) (tps / 20D * 100D))));

            for (World world : server.worldServers) {
                tps = getTps(server,world);
                tickms = getTickMs(server,world);
                sender.addChatMessage(new TextComponentString(String.format("%s [%d]: %.2f TPS/%.2fMS (%d%%)",world.getWorldType().getWorldTypeName(),world.provider.getDimension(),tps,tickms,(int)(tps/20D*100D))));
            }
        } else if (args[1].toLowerCase().charAt(0) == 'o') { //overall
            double tickms = getTickMs(server,null);
            double tps = getTps(server,null);

            sender.addChatMessage(new TextComponentString("Overall server tick"));
            sender.addChatMessage(new TextComponentString(String.format("TPS: %.2f TPS of %.2f TPS (%d%%)", tps, 20L, (int) (tps / 20D * 100D))));
            sender.addChatMessage(new TextComponentString(String.format("Tick time: %.2f ms of %.2f ms.",tickms,50L)));
        } else if (args[1].toLowerCase().charAt(0) == 'a') { //all
            double tickms = getTickMs(server,null);
            double tps = getTps(server,null);
    
            sender.addChatMessage(new TextComponentString("Overall server tick"));
            sender.addChatMessage(new TextComponentString(String.format("TPS: %.2f TPS of %.2f TPS (%d%%)", tps, 20L, (int) (tps / 20D * 100D))));
            sender.addChatMessage(new TextComponentString(String.format("Tick time: %.2f ms of %.2f ms.",tickms,50L)));

            int loadedChunks = 0;
            int entities = 0;
            int te = 0;
            int worlds = 0;

            for (World world : server.worldServers) {
                loadedChunks += ((ChunkProviderServer)world.getChunkProvider()).getLoadedChunkCount();
                entities += world.loadedEntityList.size();
                te += world.loadedTileEntityList.size();
                worlds += 1;
            }
            sender.addChatMessage(new TextComponentString(String.format("Total Loaded Worlds/Chunks: %d/%d",worlds,loadedChunks)));
            sender.addChatMessage(new TextComponentString(String.format("Total Entities/TileEntities: %d/%d",entities,te)));
        } else { //dimension
            try {
                int dim = Integer.parseInt(args[1]);
                World world = server.worldServerForDimension(dim);
                if (world == null) AbstractCommand.throwNoWorld();

                double tickms = getTickMs(server,world);
                double tps = getTps(server,world);

                sender.addChatMessage(new TextComponentString(String.format("World %s: %s - Loaded chunks: %d",world.provider.getDimension(),world.getWorldType().getWorldTypeName(),((ChunkProviderServer)world.getChunkProvider()).getLoadedChunkCount())));
                sender.addChatMessage(new TextComponentString(String.format("TPS: %.2f/%.2f TPS (%d%%) - Tick: %.2f ms of %.2f ms",tps,20L,(int)(tps/20D*100D),tickms,50L)));
                sender.addChatMessage(new TextComponentString(String.format("Entities: %d - Tile Entities: %d",world.loadedEntityList.size(),world.loadedTileEntityList.size())));
            } catch (NumberFormatException e) {
                AbstractCommand.throwInvalidNumber(args[1]);
            }
        }
    }
    @Override
    public List<String> getTabCompletionOptions (MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2 || args.length == 3) {
            List<String> worldIDs = new ArrayList<String>();
            worldIDs.add("o");
            worldIDs.add("a");
            for (World world : server.worldServers) {
                worldIDs.add(Integer.toString(world.provider.getDimension()));
            }
            return CommandBase.getListOfStringsMatchingLastWord(args, worldIDs.toArray(new String[]{""}));
        }
        return null;
    }
    private static double getTickTimeSum(long[] times) {
        long timesum = 0L;
        if (times == null) {
            return 0.0D;
        }
        for (int i = 0; i < times.length; i++) {
            timesum += times[i];
        }

        return timesum / times.length;
    }
    private static double getTickMs(MinecraftServer server, World world) {
        return getTickTimeSum(world == null ? server.tickTimeArray : server.worldTickTimes.get(Integer.valueOf(world.provider.getDimension()))) * 1.0E-006D;
    }

    private static double getTps(MinecraftServer server, World world) {
        double tps = 1000.0D / getTickMs(server, world);
        return tps > 20.0D ? 20.0D : tps;
    }
}
