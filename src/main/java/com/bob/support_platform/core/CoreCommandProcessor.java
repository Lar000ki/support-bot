package com.bob.support_platform.core;

import com.bob.support_platform.core.dto.CoreCommand;
import com.bob.support_platform.core.interfaces.CoreResponse;
import com.bob.support_platform.core.service.TextService;
import com.bob.support_platform.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreCommandProcessor {

    private final UserService userService;
    private final TextService textService;

    public List<CoreResponse> handle(CoreCommand cmd) {

        return switch (cmd.name()) {
            case "ban" -> handleBan(cmd);
            case "unban" -> handleUnban(cmd);

            default -> List.of(
                    new CoreResponse.SendText(
                            cmd.adminId(),
                            textService.get("unknown-command")
                    )
            );
        };
    }

    private List<CoreResponse> handleBan(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/ban " + textService.get("user-id"));
        }

        long userId = parseLong(cmd.args().get(0));
        if (userId == -1) {
            return usage(cmd, "/ban " + textService.get("user-id"));
        }

        userService.setBanned(cmd.platform(), userId, true);

        return List.of(
                new CoreResponse.SendText(
                        cmd.adminId(),
                        textService.get("user") + " " + userId + " " + textService.get("banned")
                )
        );
    }

    private List<CoreResponse> handleUnban(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/unban " + textService.get("user-id"));
        }

        long userId = parseLong(cmd.args().get(0));
        if (userId == -1) {
            return usage(cmd, "/unban " + textService.get("user-id"));
        }

        userService.setBanned(cmd.platform(), userId, false);

        return List.of(
                new CoreResponse.SendText(
                        cmd.adminId(),
                        textService.get("user") + " " + userId + " " + textService.get("unbanned")
                )
        );
    }

    private List<CoreResponse> usage(CoreCommand cmd, String usage) {
        return List.of(
                new CoreResponse.SendText(cmd.adminId(), textService.get("usage-command") + " " + usage)
        );
    }

    private long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }
}

