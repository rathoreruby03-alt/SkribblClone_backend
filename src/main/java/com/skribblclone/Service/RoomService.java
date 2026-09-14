package com.skribblclone.Service;

import com.skribblclone.Entity.Game;
import com.skribblclone.Entity.Room;
import com.skribblclone.Entity.RoomEntity;
import com.skribblclone.Repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.skribblclone.Entity.Player;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final RoomRepository roomRepository;
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(
            String hostId,
            String hostName,
            int maxPlayers,
            int rounds,
            int drawTime,
            int wordCount,
            int hints,
            boolean privateRoom
    ){

        String roomId = UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();

        Room room = new Room();
        room.setRoomId(roomId);
        room.setHostId(hostId);
        room.setMaxPlayers(maxPlayers);
        room.setRounds(rounds);
        room.setDrawTime(drawTime);
        room.setWordCount(wordCount);
        room.setHints(hints);
        room.setPrivateRoom(privateRoom);

        Game game = new Game();
        game.setTotalRounds(rounds);

        room.setGame(game);

// Add host as the first player
        Player host = new Player(hostId, hostName);
        room.getPlayers().add(host);

        // Save room information to MySQL
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setPrivateRoom(room.isPrivateRoom());

        roomEntity.setRoomId(room.getRoomId());
        roomEntity.setHostId(room.getHostId());
        roomEntity.setMaxPlayers(room.getMaxPlayers());
        roomEntity.setRounds(room.getRounds());
        roomEntity.setDrawTime(room.getDrawTime());
        roomEntity.setWordCount(room.getWordCount());
        roomEntity.setHints(room.getHints());

        roomRepository.save(roomEntity);

// Keep room in memory for live WebSocket game
        rooms.put(roomId, room);

        return room;
    }

    public Room getRoom(String roomId) {

        // First check live rooms in memory
        Room room = rooms.get(roomId);

        if (room != null) {
            return room;
        }

        // If not found, check MySQL
        RoomEntity entity = roomRepository.findById(roomId)
                .orElse(null);

        if (entity == null) {
            return null;
        }

        // Rebuild the Room object
        Room recoveredRoom = new Room();

        recoveredRoom.setRoomId(entity.getRoomId());
        recoveredRoom.setHostId(entity.getHostId());
        recoveredRoom.setMaxPlayers(entity.getMaxPlayers());
        recoveredRoom.setRounds(entity.getRounds());
        recoveredRoom.setDrawTime(entity.getDrawTime());
        recoveredRoom.setWordCount(entity.getWordCount());
        recoveredRoom.setHints(entity.getHints());
        recoveredRoom.setPrivateRoom(entity.isPrivateRoom());

        // Create a fresh game for the recovered room
        Game game = new Game();
        game.setTotalRounds(entity.getRounds());

        recoveredRoom.setGame(game);

        // Put it back into memory
        rooms.put(roomId, recoveredRoom);

        return recoveredRoom;
    }

    public Room joinRoom(
            String roomId,
            String playerId,
            String playerName
    ) {

        Room room = getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        // Player is already in the room
        for (Player existingPlayer : room.getPlayers()) {

            if (existingPlayer.getId().equals(playerId)) {
                return room;
            }
        }

        // Check room capacity
        if (room.getPlayers().size() >= room.getMaxPlayers()) {
            throw new RuntimeException("Room is full");
        }

        Player player = new Player(playerId, playerName);

        room.getPlayers().add(player);

        return room;
    }
    public Room leaveRoom(String roomId, String playerId) {

        Room room = getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        // Check if the leaving player is the host
        boolean isHostLeaving =
                room.getHostId().equals(playerId);

        // Check if the leaving player is the current drawer
        boolean isDrawerLeaving =
                room.getGame() != null &&
                        playerId.equals(room.getGame().getDrawerId());

        // Remove the player
        room.getPlayers().removeIf(
                player -> player.getId().equals(playerId)
        );

        // If no players remain, remove the room
        if (room.getPlayers().isEmpty()) {
            rooms.remove(roomId);
            roomRepository.deleteById(roomId);
            return room;
        }

        // Transfer host if the host left
        if (isHostLeaving) {

            Player newHost = room.getPlayers().get(0);

            room.setHostId(newHost.getId());

            newHost.setReady(false);
        }

        // If the drawer left during a game,
        // select another player as drawer
        if (isDrawerLeaving && !room.getPlayers().isEmpty()) {

            Player newDrawer = room.getPlayers().get(0);

            room.getGame().setDrawerId(newDrawer.getId());

            // Reset the current drawing round
            room.getGame().setCurrentWord(null);
            room.getGame().setWordOptions(null);
            room.getGame().setRoundActive(false);
            room.getGame().setRemainingTime(0);
            room.getGame().setGameStatus("WAITING");
        }

        return room;
    }
}