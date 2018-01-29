package pokeraidbot.domain.errors;

import net.dv8tion.jda.core.entities.User;
import pokeraidbot.domain.config.LocaleService;

public class WrongNumberOfArgumentsException extends UserMessedUpException {
    public WrongNumberOfArgumentsException(User user, LocaleService localeService, int expectedArguments,
                                           int actualArguments, String helpTextForCommand) {
        super(user, localeService.getMessageFor(LocaleService.WRONG_NUMBER_OF_ARGUMENTS,
                localeService.getLocaleForUser(user),
                "" + expectedArguments,
                // todo: i18n
                "" + actualArguments) + "\n\nHelp:\n" + helpTextForCommand);
    }
}
