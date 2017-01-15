package ws.nmathe.rider;

import ws.nmathe.rider.core.command.CommandHandler;
import ws.nmathe.rider.core.EventListener;
import ws.nmathe.rider.core.group.GroupManager;
import ws.nmathe.rider.core.BotSettings;
import ws.nmathe.rider.utils.HttpUtilities;
import ws.nmathe.rider.utils.__out;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.SelfUser;

/**
 */
public class Main
{
    private static JDA jda;                     // api
    private static BotSettings botSettings;     // global config botSettings

    private static CommandHandler commandHandler = new CommandHandler();
    private static GroupManager groupManager = new GroupManager();

    public static void main( String[] args )
    {
        // get or generate bot settings
        botSettings = BotSettings.init();
        if( botSettings == null )
        {
            __out.printOut(Main.class, "Created a new java properties file. Add your " +
                    "bot token to the file and restart the bot.\n");
            return;
        }

        // build the bot
        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(botSettings.getToken()) // set token
                    .buildBlocking();

            // attach listener
            jda.addEventListener(new EventListener());

            // enable reconnect
            jda.setAutoReconnect(true);

            // set the bot's 'game' message
            jda.getPresence().setGame(new Game()
            {
                @Override
                public String getName()
                {
                    return "LFG Bot | " + botSettings.getCommandPrefix() + "help";
                }

                @Override
                public String getUrl()
                {
                    return "";
                }

                @Override
                public GameType getType()
                {
                    return GameType.DEFAULT;
                }
            });
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return;
        }

        commandHandler.init();
        groupManager.init();

        String auth = botSettings.getWebToken();
        if( auth != null )
            HttpUtilities.updateCount(Main.getBotJda().getGuilds().size(), auth);
    }

    public static SelfUser getBotSelfUser()
    {
        return jda.getSelfUser();
    }

    public static JDA getBotJda()
    {
        return jda;
    }

    public static BotSettings getBotSettings()
    {
        return botSettings;
    }

    public static CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public static GroupManager getGroupManager()
    {
        return groupManager;
    }
}
