package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.EmbedType;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.Config;
import pokeraidbot.domain.ConfigRepository;

public abstract class ConfigAwareCommand extends Command {
    private final ConfigRepository configRepository;

    public ConfigAwareCommand(ConfigRepository configRepository) {
        Validate.notNull(configRepository);
        this.configRepository = configRepository;
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, String message) {
        if (config != null && config.replyInDmWhenPossible) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    public static void replyBasedOnConfig(Config config, CommandEvent commandEvent, MessageEmbed message) {
        if (config != null && config.replyInDmWhenPossible) {
            commandEvent.replyInDM(message);
            commandEvent.reactSuccess();
        } else {
            commandEvent.reply(message);
        }
    }

    public static void replyErrorBasedOnConfig(Config config, CommandEvent commandEvent, Throwable t) {
        if (config != null && config.replyInDmWhenPossible) {
            commandEvent.replyInDM(t.getMessage());
            commandEvent.reactError();
        } else {
            commandEvent.reply(t.getMessage());
        }
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final String server = commandEvent.getGuild().getName().trim().toLowerCase();
        Config configForServer = null;
        try {
            configForServer = configRepository.getConfigForServer(server);
            executeWithConfig(commandEvent, configForServer);
        } catch (Throwable t) {
            replyErrorBasedOnConfig(configForServer, commandEvent, t);
        }
    };

    protected abstract void executeWithConfig(CommandEvent commandEvent, Config config);
}