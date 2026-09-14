package com.skribblclone.Controller;

import com.skribblclone.DTO.CreateRoomRequest;
import com.skribblclone.DTO.JoinRoomRequest;
import com.skribblclone.Entity.Room;
import com.skribblclone.Service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomController {
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomController(
            RoomService roomService,
            SimpMessagingTemplate messagingTemplate) {

        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(
            @RequestBody CreateRoomRequest request
    ) {

        Room room = roomService.createRoom(
                request.getHostId(),
                request.getHostName(),
                request.getMaxPlayers(),
                request.getRounds(),
                request.getDrawTime(),
                request.getWordCount(),
                request.getHints(),
                request.isPrivateRoom()
        );

        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(
            @PathVariable String roomId
    ) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(room);
    }
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Room> joinRoom(
            @PathVariable String roomId,
            @RequestBody JoinRoomRequest request) {

        Room room = roomService.joinRoom(
                roomId,
                request.getPlayerId(),
                request.getPlayerName()
        );

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                room
        );

        return ResponseEntity.ok(room);
    }
}