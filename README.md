# Support Platform
[🌐 README на русском](README.ru.md)

A multi-platform user support system with a single core engine
(currently integrated with Telegram).

The project is built according to **Hexagonal / Clean Architecture** principles:
- all business logic is in the `core`
- platforms (Telegram, Discord, etc.) are connected via adapters
- UI and configuration are separate

## Content

- [Features](#features)
- [Requirements](#requirements)
- [How to use](#how-to-use)
- [Dev running](#dev-running)
- [Architecture](#architecture)
- [Main idea](#main-idea)
- [How to add a new platform](#how-to-add-a-new-platform)
- [Configuration and web panel](#configuration-and-web-panel)
- [Roadmap](#roadmap)
---

## Features

- support tickets (one open ticket per user)
- message rate limit
- ban users
- welcome message upon first contact
- the ability to add a header and footer to support responses
- attachments support
- operator responses -> to user
- web interface for editing config `application.yml`
- architecture ready to connect new platforms

## Requirements

* Java 21
* PostgreSQL or MySQL
* Maven (for dev)

## How to use
Download .jar + .yml from [releases](https://github.com/Lar000ki/support-bot/releases) and put them in one directory
```bash
java -jar target/support-platform-<current version>.jar
```
## Dev running

### Running

```bash
mvn spring-boot:run
```
### Building and running the jar (jar requires .yml in its directory)

```bash
mvn clean package
```
```bash
java -jar target/support-platform-<current version>.jar
```

## Architecture
<details>
<summary><b>command</b></summary>
<code>tree -I 'target|.git|.idea|*.class|test' --dirsfirst</code>
</details>

```
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── bob
│       │           └── support_platform
│       │               ├── config
│       │               │   ├── SupportConfig.java
│       │               │   └── SupportProperties.java
│       │               ├── core
│       │               │   ├── dto
│       │               │   │   ├── AdminReplyContext.java
│       │               │   │   └── CoreCommand.java
│       │               │   ├── interfaces
│       │               │   │   ├── CoreResponse.java
│       │               │   │   └── PlatformMessage.java
│       │               │   ├── model
│       │               │   │   ├── PlatformType.java
│       │               │   │   ├── Ticket.java
│       │               │   │   ├── TicketStatus.java
│       │               │   │   └── User.java
│       │               │   ├── service
│       │               │   │   ├── RateLimitExceededException.java
│       │               │   │   ├── RateLimitService.java
│       │               │   │   ├── SupportService.java
│       │               │   │   ├── TextService.java
│       │               │   │   ├── TicketService.java
│       │               │   │   ├── UserBannedException.java
│       │               │   │   └── UserService.java
│       │               │   ├── CoreCommandProcessor.java
│       │               │   ├── CoreSupportProcessor.java
│       │               │   └── PlatformConfigResolver.java
│       │               ├── platform
│       │               │   ├── discord
│       │               │   └── telegram
│       │               │       ├── adapter
│       │               │       │   ├── TelegramCommandAdapter.java
│       │               │       │   ├── TelegramMessageAdapter.java
│       │               │       │   └── TelegramPlatformMessage.java
│       │               │       ├── TelegramBot.java
│       │               │       ├── TelegramBotConfig.java
│       │               │       ├── TelegramProperties.java
│       │               │       ├── TelegramSender.java
│       │               │       └── TelegramUpdateHandler.java
│       │               ├── repository
│       │               │   ├── TicketRepository.java
│       │               │   └── UserRepository.java
│       │               ├── web
│       │               │   ├── ConfigController.java
│       │               │   ├── SupportConfigDto.java
│       │               │   ├── UiController.java
│       │               │   ├── WebSecurityConfig.java
│       │               │   └── YamlConfigService.java
│       │               └── SupportPlatformApplication.java
│       └── resources
│           ├── static
│           │   └── app.js
│           └── templates
│               └── index.html
├── application.yml
└── pom.xml

```

## Main idea

**Core knows nothing about platforms (telegram, discord, etc.)**

Core:
- accepts abstract messages (`PlatformMessage`)
- returns intents (`CoreResponse`)
- does not send messages itself

Platforms:
- determine context (user / admin / command)
- adapt native messages
- execute CoreResponse

## How to add a new platform

**To add a new platform, the following elements need to be implemented:**

### 1: PlatformMessage

Platform native message adapter:

```java
public interface PlatformMessage {
    PlatformType platform();
    long chatId();
    long senderId();
    String text();
    boolean hasAttachments();
    Object nativeMessage();
}
```

> `nativeMessage()` is the native platform SDK object
> Core **doesn't use** it, it just passes it back to `CoreResponse.CopyMessage`

### 2: MessageAdapter

Converts a native platform message to a `PlatformMessage`:

```java
@Component
public class TelegramMessageAdapter {
    public PlatformMessage adapt(Message message) {
        return new TelegramPlatformMessage(message);
    }
}
```

### 3: CommandAdapter

Converts admin commands to `CoreCommand`:

```java
public record CoreCommand(
    PlatformType platform,
    long adminId,
    String name,
    List<String> args
) {}
```

### 4: UpdateHandler (Router)

The platform **itself determines the context** of the message:

* admin command
* admin reply
* user message

Example logic:

```text
1. If the command - CoreCommandProcessor
2. If the reply is from an administrator - CoreSupportProcessor.handleAdminReply
3. If the message is from a user - CoreSupportProcessor.handleUserMessage
```

### 5: Executing `CoreResponse`

The Core returns **intents**, the platform is obliged to fulfill them:

```java
public sealed interface CoreResponse {
    record SendText(long chatId, String text) implements CoreResponse {}
    record CopyMessage(long from, long to, Object nativeMsg) implements CoreResponse {}
    enum Ignore implements CoreResponse { INSTANCE }
}
```

The platform knows:

* how to send a text
* how to copy a message
* how to ignore

## Configuration and web panel

All settings are located in `application.yml`.

Editing is possible:

* manually
* via the web panel

### Web Admin Panel
* By default, it's located on port 8080
* URL: `http://localhost:8080/`
* HTTP Basic Auth
* Allows editing **the entire `application.yml`**
* Changes take effect after a restart


## Roadmap

- [x] Core engine (tickets, rate-limit, ban)
- [x] Telegram platform
- [x] Web UI for configuration
- [ ] Discord platform
- [ ] Rate-limit storage in Redis