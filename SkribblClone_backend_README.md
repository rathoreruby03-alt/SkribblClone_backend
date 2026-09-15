# 🖥️ Skribbl Clone — Backend

The backend of a real-time multiplayer drawing and guessing game inspired by **Skribbl.io**.

Built using **Java, Spring Boot, Spring WebSocket, Spring Data JPA, Hibernate, Maven, and MySQL**.

## 🌐 Deployment

**Live Backend:** https://skribblclone-backend-f6ve.onrender.com/

**Frontend Repository:** https://github.com/rathoreruby03-alt/SkribblClone_frontend

## ✨ Backend Responsibilities

The backend manages:

- Room creation and joining
- Player management
- Lobby and ready status
- Game creation
- Turn management
- Word selection
- Drawing events
- Guessing
- Chat
- Hints
- Scoring
- Leaderboard
- Round progression
- Game timer
- Drawer rotation
- Game-over state

## 🛠️ Tech Stack

- **Java**
- **Spring Boot**
- **Spring Web**
- **Spring WebSocket**
- **Spring Data JPA**
- **Hibernate**
- **MySQL**
- **Maven**
- **Lombok**
- **Docker**

## 🏗️ Backend Architecture

```text
                    React Frontend
                          │
                 REST API / WebSocket
                          │
                          ▼
              ┌──────────────────────┐
              │   Spring Boot App    │
              └──────────┬───────────┘
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
     Controllers      Services      WebSocket
          │              │              │
          └──────────────┼──────────────┘
                         ▼
                 JPA / Repositories
                         │
                         ▼
                   MySQL Database
```

## 📂 Project Structure

```text
SkribblClone_backend/
│
├── src/
│   └── main/
│       ├── java/com/skribblclone/
│       │   ├── Config/
│       │   │   ├── WebSocketConfig.java
│       │   │   └── CorsConfig.java
│       │   │
│       │   ├── Controller/
│       │   │   └── RoomController.java
│       │   │
│       │   ├── DTO/
│       │   │   ├── CreateRoomRequest.java
│       │   │   └── JoinRoomRequest.java
│       │   │
│       │   ├── Entity/
│       │   │   ├── Player.java
│       │   │   ├── Room.java
│       │   │   ├── RoomEntity.java
│       │   │   ├── Game.java
│       │   │   └── Word.java
│       │   │
│       │   ├── Repository/
│       │   │   ├── RoomRepository.java
│       │   │   └── WordRepository.java
│       │   │
│       │   ├── Service/
│       │   │   ├── RoomService.java
│       │   │   └── GameService.java
│       │   │
│       │   └── WebSocket/
│       │       ├── GameWebSocketController.java
│       │       ├── DrawMessage.java
│       │       ├── GuessMessage.java
│       │       ├── ChatMessage.java
│       │       ├── ReadyMessage.java
│       │       ├── ChooseWordMessage.java
│       │       └── ...
│       │
│       └── resources/
│
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 🔌 REST API

The backend provides REST endpoints for room-related operations.

Examples:

```text
GET    /api/rooms/{roomId}
GET    /api/rooms/public
POST   /api/rooms
POST   /api/rooms/join
```

The exact available endpoints may evolve as the application is developed.

## 🔌 WebSocket

WebSocket endpoint:

```text
/ws
```

Application destination prefix:

```text
/app
```

Message broker prefix:

```text
/topic
```

The backend handles real-time events including:

```text
/app/game/start
/app/game/choose-word
/app/game/draw
/app/game/guess
/app/game/ready
/app/game/undo
/app/game/clear
/app/game/hint
/app/game/chat
/app/game/leave
```

Room updates are broadcast using topics such as:

```text
/topic/room/{roomId}
/topic/room/{roomId}/game
```

## 🎮 Game Flow

```text
Create / Join Room
        ↓
      Lobby
        ↓
 Players Ready
        ↓
    Host Starts
        ↓
   Select Drawer
        ↓
 Drawer Gets Words
        ↓
  Choose Word
        ↓
 Draw + Guess
        ↓
   Timer Ends
        ↓
 Leaderboard Update
        ↓
   Next Round
        ↓
 Final Round Ends
        ↓
  Winner Displayed
```

## 🗄️ Database

The application uses MySQL with Spring Data JPA/Hibernate.

Main entities include:

- `Room`
- `Player`
- `Game`
- `Word`

The `words` table stores the available words and their categories.

Example:

```text
words
├── id
├── word
└── category
```

## ⚙️ Environment Variables

Production database configuration uses environment variables:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
PORT
```

The application configuration uses these variables rather than hardcoding database credentials.

Example:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
server.port=${PORT:8080}
```

**Never commit database passwords or other secrets to GitHub.**

## 🚀 Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/rathoreruby03-alt/SkribblClone_backend.git
cd SkribblClone_backend
```

### 2. Configure MySQL

Create a MySQL database:

```sql
CREATE DATABASE skribbl_db;
```

Configure the required database environment variables.

### 3. Run the application

Using Maven Wrapper on Windows:

```bash
mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

The backend will normally run at:

```text
http://localhost:8080
```

WebSocket endpoint:

```text
ws://localhost:8080/ws
```

## 🐳 Docker

The project includes a Dockerfile for deployment.

Build:

```bash
docker build -t skribbl-backend .
```

Run:

```bash
docker run -p 8080:8080 skribbl-backend
```

Production deployment is currently configured through Render.

## 🌍 Deployment Architecture

```text
┌─────────────────────────┐
│        Vercel           │
│     React Frontend      │
└────────────┬────────────┘
             │
        HTTPS / WSS
             │
             ▼
┌─────────────────────────┐
│         Render          │
│   Spring Boot Backend   │
└────────────┬────────────┘
             │
          MySQL
             │
             ▼
┌─────────────────────────┐
│        Railway          │
│      MySQL Database     │
└─────────────────────────┘
```

## 🔐 CORS & WebSocket Configuration

The backend allows the deployed frontend to communicate with the API and WebSocket endpoint.

Production frontend:

```text
https://skribbl-frontend-sigma.vercel.app
```

WebSocket endpoint:

```text
https://skribblclone-backend-f6ve.onrender.com/ws
```

## 🧪 Testing

Test the backend through the complete multiplayer flow:

1. Create a room.
2. Join with a second player.
3. Mark players ready.
4. Start the game.
5. Generate word options.
6. Select a word.
7. Send drawing events.
8. Send guesses.
9. Verify scoring.
10. Verify timer and round transition.
11. Verify drawer rotation.
12. Verify final leaderboard and winner.

## 📌 Important Notes

- The production database must contain words in the `words` table for gameplay to start.
- Database credentials must be supplied through environment variables.
- Do not commit `.env`, passwords, or other secrets.
- The frontend and backend are maintained in separate repositories.

## 🔗 Related Repository

**Frontend:** https://github.com/rathoreruby03-alt/SkribblClone_frontend

## 👩‍💻 Author

**Ruby Rathore**

BCA | Full Stack Developer Fresher
