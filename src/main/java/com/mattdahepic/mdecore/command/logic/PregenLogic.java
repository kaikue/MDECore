package com.mattdahepic.mdecore.command.logic;

import com.google.common.base.Throwables;
import com.mattdahepic.mdecore.command.ICommandLogic;
import com.mattdahepic.mdecore.world.TickHandlerWorld;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.List;

public class PregenLogic implements ICommandLogic {
    public static final String USAGE = "/mde pregen {<user> <x chunk radius> <z chunk radius> | <x chunk start> <z chunk start> <x chunk end> <z chunk end>}";
    public static PregenLogic instance = new PregenLogic();

    @Override
    public String getCommandName () {
        return "pregen";
    }
    @Override
    public int getPermissionLevel () {
        return 2;
    }
    @Override
    public String getCommandSyntax () {
        return USAGE;
    }
    @Override
    public void handleCommand (ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException("Invalid Usage! Type /mde help "+getCommandName()+" for usage");
        }
        World world = sender.getEntityWorld();
        if (world.isRemote) return;

        ChunkCoordIntPair center = null;
        int i = 1;
        int xS, xL;
        if ("@".equals(args[i])) {
            center = new ChunkCoordIntPair(sender.getPosition().getX(),sender.getPosition().getZ());
            i++;
            xS = CommandBase.parseInt(args[i++]);
        } else {
            try {
                xS = CommandBase.parseInt(args[i++]);
            } catch (Throwable t) {
                ICommandSender senderTemp = CommandBase.getPlayer(sender, args[i - 1]);
                center = new ChunkCoordIntPair(senderTemp.getPosition().getX(),senderTemp.getPosition().getZ());
                xS = CommandBase.parseInt(args[i++]);
            }
        }
        int zS = CommandBase.parseInt(args[i++]), zL;
        int t = i + 1;

        try {
            xL = CommandBase.parseInt(args[i++]);
            zL = CommandBase.parseInt(args[i++]);
        } catch (Throwable e) {
            if (i > t || center == null) {
                throw Throwables.propagate(e);
            }
            --i;
            xL = xS;
            zL = zS;
        }

        if (center != null) {
            xS = (center.chunkXPos / 16) - xS;
            zS = (center.chunkZPos / 16) - zS;

            xL = (center.chunkXPos / 16) + xL;
            zL = (center.chunkZPos / 16) + zL;
        }

        if (xL < xS) {
            t = xS;
            xS = xL;
            xL = t;
        }
        if (zL < zS) {
            t = zS;
            zS = zL;
            zL = t;
        }

        synchronized (TickHandlerWorld.chunksToPreGen) {
            ArrayDeque<ChunkCoordIntPair> chunks = TickHandlerWorld.chunksToPreGen.get(world.provider.getDimensionId());
            if (chunks == null) {
                chunks = new ArrayDeque<ChunkCoordIntPair>();
            }

            for (int x = xS; x <= xL; ++x) {
                for (int z = zS; z <= zL; ++z) {
                    chunks.addLast(new ChunkCoordIntPair(x, z));
                }
            }
            TickHandlerWorld.chunksToPreGen.put(world.provider.getDimensionId(), chunks);
        }
    }
    @Override
    public List<String> addTabCompletionOptions (ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        return null;
    }
}