package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRaidStrategyService;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.RaidBossCounters;
import pokeraidbot.infrastructure.CounterPokemon;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class PokemonVsCommand extends ConfigAwareCommand {
    private final PokemonRaidStrategyService raidInfoService;
    private final LocaleService localeService;
    private final PokemonRepository repo;

    public PokemonVsCommand(PokemonRepository repo, PokemonRaidStrategyService raidInfoService,
                            LocaleService localeService, ServerConfigRepository serverConfigRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.raidInfoService = raidInfoService;
        this.localeService = localeService;
        this.name = "vs";
        this.help = localeService.getMessageFor(LocaleService.VS_HELP, LocaleService.DEFAULT);
        this.repo = repo;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String pokemonName = commandEvent.getArgs();
        final Pokemon pokemon = repo.getByName(pokemonName);
        final RaidBossCounters counters = raidInfoService.getCounters(pokemon);
        final String maxCp = raidInfoService.getMaxCp(pokemon);
        StringBuilder builder = new StringBuilder();
        final Locale localeForUser = localeService.getLocaleForUser(commandEvent.getAuthor());
        builder.append("**").append(pokemon).append("**\n");
        builder.append(localeService.getMessageFor(LocaleService.WEAKNESSES, localeForUser))
                .append(Utils.printWeaknesses(pokemon)).append("\n").append(
                localeService.getMessageFor(LocaleService.RESISTANT, localeForUser))
                .append(pokemon.getResistant()).append("\n");

        if (counters != null) {
            appendBestCounters(counters, builder, localeForUser);
        }

        if (maxCp != null) {
            builder.append("Max CP (100% IV): ").append(maxCp).append("\n");
        }

        replyBasedOnConfig(config, commandEvent, builder.toString());
    }

    private void appendBestCounters(RaidBossCounters counters, StringBuilder builder, Locale localeForUser) {
        final String otherCountersText = localeService.getMessageFor(LocaleService.OTHER_COUNTERS, localeForUser);
        final String moveSetText = localeService.getMessageFor(LocaleService.IF_CORRECT_MOVESET, localeForUser);
        final String bestCounterText = localeService.getMessageFor(LocaleService.BEST_COUNTERS, localeForUser);

        if (counters != null && counters.getSupremeCounters().size() > 0) {
            builder.append(bestCounterText);
            final Optional<CounterPokemon> bestCounterPokemon = counters.getSupremeCounters().stream().findFirst();
            builder.append(bestCounterPokemon.get());
            if (counters.getSupremeCounters().size() > 1 || counters.getGoodCounters().size() > 0) {
                final LinkedList<CounterPokemon> totalCounters = new LinkedList<>(counters.getSupremeCounters());
                totalCounters.addAll(counters.getGoodCounters());
                List<String> otherCounters = totalCounters.stream().skip(1)
                        .map(CounterPokemon::getCounterPokemonName).collect(Collectors.toList());
                builder.append("\n")
                        .append(otherCountersText).append("[");
                builder.append(StringUtils.join(otherCounters.toArray(), ", "));
                builder.append("] **").append(moveSetText).append("**\n");
            }
        } else {
            builder.append(bestCounterText);
            final Optional<CounterPokemon> bestCounterPokemon = counters.getGoodCounters().stream().findFirst();
            builder.append(bestCounterPokemon.get());
            if (counters.getGoodCounters().size() > 1) {
                final LinkedList<CounterPokemon> totalCounters = new LinkedList<>(counters.getGoodCounters());
                List<String> otherCounters = totalCounters.stream().skip(1)
                        .map(CounterPokemon::getCounterPokemonName).collect(Collectors.toList());
                builder.append("\n").append(otherCountersText).append("[");
                builder.append(StringUtils.join(otherCounters.toArray(), ", "));
                builder.append("] **").append(moveSetText).append("**\n");
            }
        }
    }
}
