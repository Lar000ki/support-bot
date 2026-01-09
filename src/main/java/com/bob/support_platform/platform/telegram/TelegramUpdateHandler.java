package com.bob.support_platform.platform.telegram;


import com.bob.support_platform.core.CoreCommandProcessor;
import com.bob.support_platform.core.CoreSupportProcessor;
import com.bob.support_platform.core.dto.AdminReplyContext;
import com.bob.support_platform.core.dto.CoreCommand;
import com.bob.support_platform.core.interfaces.CoreResponse;
import com.bob.support_platform.core.interfaces.PlatformMessage;

import com.bob.support_platform.core.model.CommandScope;
import com.bob.support_platform.platform.telegram.adapter.TelegramCommandAdapter;
import com.bob.support_platform.platform.telegram.adapter.TelegramMessageAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TG <--> CORE связь
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateHandler {

    private final TelegramMessageAdapter messageAdapter;
    private final TelegramCommandAdapter commandAdapter;

    private final CoreSupportProcessor supportProcessor;
    private final CoreCommandProcessor commandProcessor;

    private final TelegramSender sender;
    private final TelegramProperties tgProperties;

    public void handle(Update update, TelegramLongPollingBot bot) {

        if (!update.hasMessage()) return;

        Message message = update.getMessage();

        // commands
        Optional<CoreCommand> cmdOpt = commandAdapter.adapt(message);
        if (cmdOpt.isPresent()) {
            CoreCommand cmd = cmdOpt.get();
            if (cmd.type().getScope() == CommandScope.ADMIN && !isAdminContext(message)) {
                return;
            }
            commandProcessor.handle(cmd)
                    .forEach(r -> apply(r, bot));
            return;
        }


        //admin -> user
        if (isAdminReply(message)) {

            Long ticketId = extractTicketId(message);
            if (ticketId == null) return;

            PlatformMessage adminMsg = messageAdapter.adapt(message);

            AdminReplyContext ctx =
                    new AdminReplyContext(adminMsg, ticketId);

            supportProcessor.handleAdminReply(ctx)
                    .forEach(r -> apply(r, bot));

            return;
        }


        //user -> support
        if (message.isUserMessage()) {
            PlatformMessage msg = messageAdapter.adapt(message);
            supportProcessor.handleUserMessage(msg)
                    .forEach(r -> apply(r, bot));
        }
    }

    private boolean isAdminCommand(Message message) {
        return message.hasText()
                && message.getText().startsWith("/")
                && message.getChatId().equals(tgProperties.getSupportChatId());
    }

    private boolean isAdminReply(Message message) {
        return message.isReply()
                && message.getChatId().equals(tgProperties.getSupportChatId())
                && message.getReplyToMessage() != null
                && Boolean.TRUE.equals(
                message.getReplyToMessage().getFrom().getIsBot()
        );
    }


    private void apply(CoreResponse response, TelegramLongPollingBot bot) {

        switch (response) {

            case CoreResponse.SendText r ->
                    sender.sendText(bot, r.chatId(), r.text());

            case CoreResponse.CopyMessage r -> {
                Message original = (Message) r.nativeMsg();
                sender.copyMessage(
                        bot,
                        r.from(),
                        r.to(),
                        original.getMessageId()
                );
            }

            case CoreResponse.Ignore r -> {}
        }
    }

    private Long extractTicketId(Message message) {
        if (message.getReplyToMessage() == null) return null;
        String text = message.getReplyToMessage().getText();
        if (text == null) return null;

        Pattern p = Pattern.compile(":\\s*#(\\d+)");
        Matcher m = p.matcher(text);
        return m.find() ? Long.parseLong(m.group(1)) : null;
    }

    private boolean isAdminContext(Message message) {
        return message.getChatId().equals(tgProperties.getSupportChatId());
    }
}



