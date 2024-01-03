package emu.grasscutter.server.game;

import static emu.grasscutter.config.Configuration.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.event.game.ReceivePacketEvent;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Grasscutter.ServerDebugMode;
import emu.grasscutter.server.game.GameSession.SessionState;
import interfaces.ProtoModel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class GameServerPacketHandler {
    private final Int2ObjectMap<PacketHandler> handlers;
    private final Map<String, PacketHandler> versionHandlers;

    public GameServerPacketHandler(Class<? extends PacketHandler> handlerClass) {
        this.handlers = new Int2ObjectOpenHashMap<>();
        this.versionHandlers = new HashMap<>();

        this.registerHandlers(handlerClass);
    }

    public void registerPacketHandler(Class<? extends PacketHandler> handlerClass) {
        try {
            Opcodes opcode = handlerClass.getAnnotation(Opcodes.class);

            if (opcode == null || opcode.disabled() || opcode.value() <= 0) {
                return;
            }

            PacketHandler packetHandler = handlerClass.getDeclaredConstructor().newInstance();

            this.handlers.put(opcode.value(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void registerTypedPacketHandler(Class<TypedPacketHandler<?>> handlerClass) {
        try {
            Class<?> modelClass = TypedPacketHandler.getStaticClass(handlerClass);
            if(modelClass == null){
                return;
            }
            PacketHandler packetHandler = handlerClass.getDeclaredConstructor().newInstance();

            this.versionHandlers.put(modelClass.getSimpleName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerHandlers(Class<? extends PacketHandler> handlerClass) {
        Set<Class<? extends PacketHandler>> handlerClasses = (Set<Class<? extends PacketHandler>>)(Set<?>) Grasscutter.reflector.getSubTypesOf(handlerClass);

        for (Class<? extends PacketHandler> obj : handlerClasses) {
            if(TypedPacketHandler.class.isAssignableFrom(obj)){
                this.registerTypedPacketHandler((Class<TypedPacketHandler<?>>) obj);
            } else {
                this.registerPacketHandler(obj);
            }
        }

        // Debug
        Grasscutter.getLogger().debug("Registered {} version handlers and {} legacy handlers for {}", this.versionHandlers.size(), this.handlers.size(), handlerClass.getSimpleName() + "s");
    }

    @Nullable
    private PacketHandler getHandler(GameSession session, int opcode) {
        String name = session.getPackageIdProvider().getPackageName(opcode);
        PacketHandler handler = this.versionHandlers.get(name);
        return handler!= null ? handler : this.handlers.get(opcode);
    }

    public void handle(GameSession session, int opcode, byte[] header, byte[] payload) {
        PacketHandler handler = getHandler(session, opcode);

        if (handler != null) {
            try {
                // Make sure session is ready for packets
                SessionState state = session.getState();

                if (opcode == PacketOpcodes.PingReq) {
                    // Always continue if packet is ping request
                } else if (opcode == PacketOpcodes.GetPlayerTokenReq) {
                    if (state != SessionState.WAITING_FOR_TOKEN) {
                        return;
                    }
                } else if (state == SessionState.ACCOUNT_BANNED) {
                    session.close();
                    return;
                } else if (opcode == PacketOpcodes.PlayerLoginReq) {
                    if (state != SessionState.WAITING_FOR_LOGIN) {
                        return;
                    }
                } else if (opcode == PacketOpcodes.SetPlayerBornDataReq) {
                    if (state != SessionState.PICKING_CHARACTER) {
                        return;
                    }
                } else {
                    if (state != SessionState.ACTIVE) {
                        return;
                    }
                }

                // Invoke event.
                ReceivePacketEvent event = new ReceivePacketEvent(session, opcode, payload);
                event.call();
                if (!event.isCanceled()) // If event is not canceled, continue.
                    handler.handle(session, header, event.getPacketData());
            } catch (Exception ex) {
                // TODO Remove this when no more needed
                ex.printStackTrace();
            }
            return; // Packet successfully handled
        }

        // Log unhandled packets
        if (GAME_INFO.logPackets == ServerDebugMode.MISSING || GAME_INFO.logPackets == ServerDebugMode.ALL) {
            Grasscutter.getLogger().warn("Unhandled packet (" + opcode + "): " + PacketOpcodesUtils.getOpcodeName(opcode, session));
        }
    }
}
