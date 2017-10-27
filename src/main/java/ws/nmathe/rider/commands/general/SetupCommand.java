package ws.nmathe.rider.commands.general;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.utils.MessageUtilities;

/**
 */
public class SetupCommand implements Command
{
    private String BOTOATH_LINK = "https://discordapp.com/api/oauth2/authorize?client_id=" +
            Main.getBotSelfUser().getId() + "&scope=bot&permissions=268512256\n";
    private String prefix = Main.getBotSettings().getCommandPrefix();

    @Override
    public String help(boolean brief)
    {
        String USAGE_EXTENDED = "\n\nYou can invite Rider to your discord server with " +
                "this link: " + BOTOATH_LINK;

        String USAGE_BRIEF = "``" + prefix + "setup`` ~ the guide to getting " +
                "Rider working on your server.";

        if (brief)
            return USAGE_BRIEF;
        else
            return USAGE_BRIEF + "\n" + USAGE_EXTENDED;
    }

    @Override
    public boolean verify(String[] args, MessageReceivedEvent event)
    {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        String msg = "" +

                "Bot operation requires a channel named **" + Main.getBotSettings().getChannel() + "** " +
                "to operate, and will require the permissions: Send/Read Messages, Manage Messages, and" +
                " Read Message History.\n\n" +

                "Optionally, if your guild provides the bot with the Manage Roles permission and has a " +
                "role named '" + Main.getBotSettings().getChannel() + "' the bot will automatically assign" +
                "said role to anyone with an active 'looking for group' entry.\n\n" +

                "The bot will automatically attempt to remove any messages that begin with the bot's command" +
                " prefix after processing the command. If the bot is not given the manage message permission" +
                "on the channel the command will not be removed, but the bot's behavior will not otherwise be" +
                " negatively effected.";

        MessageUtilities.sendPrivateMsg(msg + "\n\n"+ BOTOATH_LINK, event.getAuthor(), null);
    }
}

