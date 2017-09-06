package pokeraidbot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.jagrosh.jdautilities.commandclient.examples.PingCommand;
import com.jagrosh.jdautilities.commandclient.examples.ShutdownCommand;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.GameImpl;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
import pokeraidbot.commands.HelpCommand;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

//@SpringBootApplication
public class BotServerMain {
    //Non error, no action exit codes.
    public static final int NORMAL_SHUTDOWN = 10;
    public static final int RESTART_EXITCODE = 11;
    public static final int NEWLY_CREATED_CONFIG = 12;

    //Non error, action required exit codes.
    public static final int UPDATE_LATEST_EXITCODE = 20;
    public static final int UPDATE_RECOMMENDED_EXITCODE = 21;

    //error exit codes.
    public static final int UNABLE_TO_CONNECT_TO_DISCORD = 30;
    public static final int BAD_USERNAME_PASS_COMBO = 31;
    public static final int NO_USERNAME_PASS_COMBO = 32;

    private static JDA api;

    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        if (!System.getProperty("file.encoding").equals("UTF-8")) {
            System.err.println("ERROR: Not using UTF-8 encoding");
            System.exit(-1);
        }

        final InputStream propsAsStream = BotServerMain.class.getResourceAsStream("/pokeraidbot.properties");
        Properties properties = new Properties();
        properties.load(propsAsStream);

        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setOwnerId(properties.getProperty("ownerId"));
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        client.setPrefix("!raid");
        client.setGame(new GameImpl("Type !raid usage", "", Game.GameType.DEFAULT));
        client.addCommands(new AboutCommand(Color.BLUE, "PokeRaidBot reporting for duty!",
                new String[]{}, Permission.ADMINISTRATOR),
                new PingCommand(),
                new HelpCommand(),
                new ShutdownCommand()
                );

        new JDABuilder(AccountType.BOT)
                // set the token
                .setToken(properties.getProperty("token"))

                // set the game for when the bot is loading
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.of("loading..."))

                // add the listeners
                .addEventListener(waiter)
                .addEventListener(client.build())

                // start it up!
                .buildAsync();

//        SpringApplication.run(BotServerMain.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return args -> {
//
//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
//
//        };
//    }
}
