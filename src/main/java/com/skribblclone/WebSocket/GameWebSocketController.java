package com.skribblclone.WebSocket;

import com.skribblclone.Entity.Game;
import com.skribblclone.Service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketController(
            GameService gameService,
            SimpMessagingTemplate messagingTemplate) {

        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/start")
    public void startGame(StartGameMessage message) {

        Game game = gameService.startGame(
                message.getRoomId(),
                message.getHostId()
        );

        // Send public game information
        PublicGameMessage publicGame =
                new PublicGameMessage(game);

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/game",
                publicGame
        );

        // Send word options only to the drawer
        DrawerGameMessage drawerGame =
                new DrawerGameMessage(game);

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId()
                        + "/drawer/" + game.getDrawerId(),
                drawerGame
        );
    }
    @MessageMapping("/game/ready")
    public void ready(ReadyMessage message) {

        gameService.setPlayerReady(
                message.getRoomId(),
                message.getPlayerId(),
                message.isReady()
        );

        var room = gameService.getRoom(message.getRoomId());

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId(),
                room
        );
    }
    @MessageMapping("/game/choose-word")
    public void chooseWord(ChooseWordMessage message) {

        Game game = gameService.chooseWord(
                message.getRoomId(),
                message.getPlayerId(),
                message.getWord()
        );

        // Send public information to everyone
        PublicGameMessage publicGame =
                new PublicGameMessage(game);

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/game",
                publicGame
        );

        // Send actual word only to the drawer
        DrawerGameMessage drawerGame =
                new DrawerGameMessage(game);

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId()
                        + "/drawer/" + message.getPlayerId(),
                drawerGame
        );

        // Start timer
        gameService.startRoundTimer(
                message.getRoomId(),
                messagingTemplate
        );
    }
    @MessageMapping("/game/draw")
    public void draw(DrawMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/draw",
                message
        );
    }
    @MessageMapping("/game/clear")
    public void clearCanvas(CanvasClearMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/clear",
                message
        );
    }
    @MessageMapping("/game/undo")
    public void undo(UndoMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/undo",
                message
        );
    }
    @MessageMapping("/game/guess")
    public void guess(GuessMessage message) {

        boolean correct = gameService.checkGuess(
                message.getRoomId(),
                message.getPlayerId(),
                message.getMessage()
        );

        // Send guess result to everyone
        // Hide the correct answer from other players
        String displayedMessage;

        if (correct) {
            displayedMessage =
                    "🎉 " + message.getPlayerName()
                            + " guessed the word correctly!";
        } else {
            displayedMessage =
                    message.getPlayerName()
                            + ": " + message.getMessage();
        }

        GuessResultMessage result =
                new GuessResultMessage(
                        message.getPlayerId(),
                        message.getPlayerName(),
                        displayedMessage,
                        correct
                );

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/guess",
                result
        );

        // Send updated leaderboard
        var players = gameService.getLeaderboard(
                message.getRoomId()
        );

        LeaderboardMessage leaderboard =
                new LeaderboardMessage(players);

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/leaderboard",
                leaderboard
        );
    }
    @MessageMapping("/game/chat")
    public void chat(ChatMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/chat",
                message
        );
    }
    @MessageMapping("/game/leaderboard")
    public void leaderboard(GuessMessage message) {

        var players = gameService.getLeaderboard(
                message.getRoomId()
        );

        LeaderboardMessage leaderboard =
                new LeaderboardMessage(players);

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/leaderboard",
                leaderboard
        );
    }
    @MessageMapping("/game/hint")
    public void requestHint(GuessMessage message) {

        HintResultMessage hint = gameService.generateHint(
                message.getRoomId(),
                message.getPlayerId()
        );

        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId() + "/hint",
                hint
        );
    }
    @MessageMapping("/game/leave")
    public void leaveRoom(LeaveRoomMessage message) {

        String roomId = message.getRoomId();
        String playerId = message.getPlayerId();

        var room = gameService.getRoom(roomId);
        if (room != null &&
                room.getGame() != null &&
                message.getPlayerId().equals(room.getGame().getDrawerId())) {

            gameService.cancelRoundTimer(message.getRoomId());
        }

        if (room == null) {
            return;
        }

        room = gameService.leaveRoom(
                roomId,
                playerId
        );
        if (room.getGame() != null &&
                !room.getPlayers().isEmpty() &&
                room.getGame().getDrawerId() != null) {

            DrawerGameMessage drawerGame =
                    new DrawerGameMessage(room.getGame());

            messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId
                            + "/drawer/" + room.getGame().getDrawerId(),
                    drawerGame
            );
        }

        // Tell all remaining players about the updated room
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                room
        );

        // If players are still in the room,
        // send updated game information
        if (!room.getPlayers().isEmpty() &&
                room.getGame() != null) {

            PublicGameMessage publicGame =
                    new PublicGameMessage(room.getGame());

            messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId + "/game",
                    publicGame
            );
        }
    }
}