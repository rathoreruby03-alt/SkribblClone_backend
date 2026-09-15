package com.skribblclone.Service;

import com.skribblclone.Entity.Game;
import com.skribblclone.Entity.Player;
import com.skribblclone.Entity.Room;
import com.skribblclone.Repository.WordRepository;
import com.skribblclone.WebSocket.DrawerGameMessage;
import com.skribblclone.WebSocket.HintResultMessage;
import com.skribblclone.WebSocket.LeaderboardMessage;
import com.skribblclone.WebSocket.PublicGameMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {
    private final RoomService roomService;
    private final WordRepository wordRepository;

    public GameService(RoomService roomService, WordRepository wordRepository) {
        this.roomService = roomService;
        this.wordRepository = wordRepository;
    }

    public Game startGame(String roomId, String hostId) {


        Room room = roomService.getRoom(roomId);

        System.out.println("===== START GAME REQUEST RECEIVED =====");
        System.out.println("Room ID: " + roomId);
        System.out.println("Host ID: " + hostId);


        System.out.println("Room found: " + (room != null));

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        System.out.println("Actual Host ID: " + room.getHostId());
        System.out.println("Players: " + room.getPlayers().size());

        boolean allReady = room.getPlayers()
                .stream()
                .allMatch(Player::isReady);

        System.out.println("All players ready: " + allReady);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        if (!room.getHostId().equals(hostId)) {
            throw new RuntimeException("Only the host can start the game");
        }

        if (room.getPlayers().size() < 2) {
            throw new RuntimeException("At least 2 players are required");
        }

        if (!allReady) {
            throw new RuntimeException(
                    "All players must be ready before starting the game"
            );
        }

        Game game = room.getGame();
        game.setMaxHints(room.getHints());

        game.setGameStatus("PLAYING");
        game.setCurrentRound(1);

        // First player becomes the first drawer
        Player firstPlayer = room.getPlayers().get(0);
        game.setDrawerId(firstPlayer.getId());

        // Generate 3 random word options
        generateWordOptions(game, room.getWordCount());
        return game;
    }
    public Game chooseWord(String roomId, String playerId, String word) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        Game game = room.getGame();

        if (!game.getDrawerId().equals(playerId)) {
            throw new RuntimeException("Only the drawer can choose the word");
        }

        if (!game.getWordOptions().contains(word)) {
            throw new RuntimeException("Invalid word");
        }

        game.setCurrentWord(word);
        game.setGameStatus("DRAWING");

        return game;
    }
    public boolean checkGuess(String roomId, String playerId, String guess) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        Game game = room.getGame();

        if (game.getCurrentWord() == null) {
            return false;
        }

        // Drawer cannot guess
        if (game.getDrawerId().equals(playerId)) {
            return false;
        }

        // Guessing is allowed only while round is active
        if (!game.isRoundActive()) {
            return false;
        }

        boolean correct =
                game.getCurrentWord().equalsIgnoreCase(guess.trim());

        if (correct) {

            // Prevent the same player from getting points multiple times
            if (game.getCorrectGuessers().contains(playerId)) {
                return true;
            }

            // Mark player as already guessed correctly
            game.getCorrectGuessers().add(playerId);

            // Give 100 points to the correct guesser
            for (Player player : room.getPlayers()) {

                if (player.getId().equals(playerId)) {

                    player.setScore(player.getScore() + 100);

                    break;
                }
            }

            // Give 50 points to the drawer
            for (Player player : room.getPlayers()) {

                if (player.getId().equals(game.getDrawerId())) {

                    player.setScore(player.getScore() + 50);

                    break;
                }
            }
        }

        return correct;
    }
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(10);
    private final java.util.Map<String, ScheduledFuture<?>> activeTimers =
            new java.util.concurrent.ConcurrentHashMap<>();

    public void startRoundTimer(
            String roomId,
            SimpMessagingTemplate messagingTemplate) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        Game game = room.getGame();

        // Cancel any previous timer for this room
        ScheduledFuture<?> oldTimer = activeTimers.remove(roomId);

        if (oldTimer != null) {
            oldTimer.cancel(false);
        }

        // Start the new round
        game.setRemainingTime(room.getDrawTime());
        game.setRoundActive(true);

        ScheduledFuture<?> timer = scheduler.scheduleAtFixedRate(() -> {

            if (!game.isRoundActive()) {
                return;
            }

            int remaining = game.getRemainingTime();

            if (remaining > 0) {
                game.setRemainingTime(remaining - 1);
            }

            // Send timer to all players
            messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId + "/timer",
                    game.getRemainingTime()
            );

            // Time is over
            if (game.getRemainingTime() <= 0) {

                game.setRoundActive(false);
                game.setGameStatus("ROUND_END");

                // Remove this timer from active timers
                activeTimers.remove(roomId);

                // Cancel this scheduled task
                ScheduledFuture<?> currentTimer =
                        activeTimers.get(roomId);

                if (currentTimer != null) {
                    currentTimer.cancel(false);
                }

                // Notify players that round ended
                PublicGameMessage publicGame =
                        new PublicGameMessage(game);

                messagingTemplate.convertAndSend(
                        "/topic/room/" + roomId + "/round-end",
                        publicGame
                );

                // Move to next round
                Game nextGame = nextRound(roomId);

                // Send leaderboard
                List<Player> leaderboardPlayers =
                        getLeaderboard(roomId);

                LeaderboardMessage leaderboard =
                        new LeaderboardMessage(leaderboardPlayers);

                messagingTemplate.convertAndSend(
                        "/topic/room/" + roomId + "/leaderboard",
                        leaderboard
                );

                // Send next game state
                // Send public next game state
                PublicGameMessage publicNextGame =
                        new PublicGameMessage(nextGame);

                messagingTemplate.convertAndSend(
                        "/topic/room/" + roomId + "/game",
                        publicNextGame
                );
                if (!"GAME_OVER".equals(nextGame.getGameStatus())) {

                    DrawerGameMessage nextDrawerGame =
                            new DrawerGameMessage(nextGame);

                    messagingTemplate.convertAndSend(
                            "/topic/room/" + roomId
                                    + "/drawer/" + nextGame.getDrawerId(),
                            nextDrawerGame
                    );
                }
            }

        }, 1, 1, TimeUnit.SECONDS);

        // Store the timer for this room
        activeTimers.put(roomId, timer);
    }

    public void cancelRoundTimer(String roomId) {

        ScheduledFuture<?> timer = activeTimers.remove(roomId);

        if (timer != null) {
            timer.cancel(false);
        }
    }
    public Game nextRound(String roomId) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        Game game = room.getGame();
        game.setMaxHints(room.getHints());

        // Move to the next round
        int nextRound = game.getCurrentRound() + 1;


        if (nextRound > game.getTotalRounds()) {

            game.setGameStatus("GAME_OVER");
            game.setRoundActive(false);

            // Find the player with the highest score
            Player winner = getWinner(roomId);

            if (winner != null) {
                game.setWinnerId(winner.getId());
                game.setWinnerName(winner.getName());
            }

            return game;
        }

        game.setCurrentRound(nextRound);

        // Move drawer to the next player
        int currentDrawerIndex = 0;

        for (int i = 0; i < room.getPlayers().size(); i++) {

            if (room.getPlayers().get(i).getId()
                    .equals(game.getDrawerId())) {

                currentDrawerIndex = i;
                break;
            }
        }

        int nextDrawerIndex =
                (currentDrawerIndex + 1)
                        % room.getPlayers().size();

        game.setDrawerId(
                room.getPlayers()
                        .get(nextDrawerIndex)
                        .getId()
        );

        // Reset round data
        // Reset round data
        game.setCurrentWord(null);

        game.setHintsUsed(0);
        game.setHint(null);
        game.getCorrectGuessers().clear();

        generateWordOptions(game, room.getWordCount());

        game.setGameStatus("WAITING");
        game.setRoundActive(false);
        game.setRemainingTime(0);

        return game;
    }
    public List<Player> getLeaderboard(String roomId) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        List<Player> players = new java.util.ArrayList<>(
                room.getPlayers()
        );

        players.sort(
                (p1, p2) ->
                        Integer.compare(p2.getScore(), p1.getScore())
        );

        return players;
    }
    public Player getWinner(String roomId) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        return room.getPlayers()
                .stream()
                .max((p1, p2) ->
                        Integer.compare(
                                p1.getScore(),
                                p2.getScore()
                        ))
                .orElse(null);
    }
    public HintResultMessage generateHint(String roomId, String playerId) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        Game game = room.getGame();

        // Only players who are guessing can request a hint
        if (game.getDrawerId().equals(playerId)) {
            throw new RuntimeException("Drawer cannot request a hint");
        }

        // Word must already be selected
        if (game.getCurrentWord() == null) {
            throw new RuntimeException("No word is currently selected");
        }

        // Check hint limit
        if (game.getHintsUsed() >= game.getMaxHints()) {
            throw new RuntimeException("No hints remaining");
        }

        String word = game.getCurrentWord();

        // Create initial hint if this is the first hint
        StringBuilder hint = new StringBuilder();

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);

            if (ch == ' ') {
                hint.append("  ");
            } else {
                hint.append("_ ");
            }
        }

        // Reveal letters based on number of hints used
        int lettersToReveal = game.getHintsUsed() + 1;

        for (int i = 0; i < word.length() && lettersToReveal > 0; i++) {

            char ch = word.charAt(i);

            if (ch != ' ') {

                int position = i * 2;

                hint.setCharAt(position, ch);

                lettersToReveal--;
            }
        }

        game.setHintsUsed(game.getHintsUsed() + 1);
        game.setHint(hint.toString().trim());

        return new HintResultMessage(
                game.getHint(),
                game.getHintsUsed(),
                game.getMaxHints()
        );
    }
    private void generateWordOptions(Game game, int wordCount) {

        List<String> words = wordRepository.findAll()
                .stream()
                .map(word -> word.getWord())
                .collect(java.util.stream.Collectors.toList());

        if (words.isEmpty()) {
            throw new RuntimeException("No words found in database");
        }

        Collections.shuffle(words);

        int count = Math.max(1, Math.min(wordCount, words.size()));

        game.setWordOptions(
                new java.util.ArrayList<>(words.subList(0, count))
        );
    }
    public void setPlayerReady(
            String roomId,
            String playerId,
            boolean ready) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        for (Player player : room.getPlayers()) {

            if (player.getId().equals(playerId)) {
                player.setReady(ready);
                return;
            }
        }

        throw new RuntimeException("Player not found");
    }
    public Room getRoom(String roomId) {
        return roomService.getRoom(roomId);
    }

    public Room leaveRoom(String roomId, String playerId) {

        Room room = roomService.getRoom(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        Game game = room.getGame();

        boolean drawerLeaving =
                game != null &&
                        playerId.equals(game.getDrawerId());

        // Let RoomService remove the player
        room = roomService.leaveRoom(roomId, playerId);

        if (drawerLeaving &&
                !room.getPlayers().isEmpty() &&
                game != null) {

            // Select the new drawer
            String newDrawerId = room.getGame().getDrawerId();

            // Generate fresh word options for the new drawer
            generateWordOptions(
                    room.getGame(),
                    room.getWordCount()
            );

            room.getGame().setCurrentWord(null);
            room.getGame().setGameStatus("WAITING");
            room.getGame().setRoundActive(false);
            room.getGame().setRemainingTime(0);

            System.out.println(
                    "New drawer selected: " + newDrawerId
            );
        }

        return room;
    }
}