package ws.nmathe.rider.commands.general;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.core.command.CommandHandler;
import ws.nmathe.rider.utils.MessageUtilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collection;

/**
 * the command which causes the bot to message the event's parent user with
 * the bot operation command list/guide. Attempts to remove the '!help' message
 * if the message does not originate from a private channel
 */
public class HelpCommand implements Command
{
    private CommandHandler cmdHandler = Main.getCommandHandler();
    private static String prefix = Main.getBotSettings().getCommandPrefix();

    private final String INTRO = "I am **" + Main.getBotSelfUser().getName() + "**, a bot providing group/party finder " +
            "like functionality to your discord server.\n\n";

    private static final String USAGE_EXTENDED = "For additional information and examples concerning these" +
            " commands, use **" + prefix + "help <command>**." +
            " Ex. **" + prefix + "help join**";

    private static final String USAGE_BRIEF = "**" + prefix + "help** - Messages the user help messages.";

    @Override
    public String help(boolean brief)
    {
        if( brief )
            return USAGE_BRIEF;
        else
            return USAGE_BRIEF + "\n\n" + USAGE_EXTENDED;
    }

    @Override
    public boolean verify(String[] args, MessageReceivedEvent event)
    {
        if(args.length>1)
            return false;
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        Collection<Command> commands = cmdHandler.getCommands();

        // send the bot intro with a brief list of commands to the user
        if(args.length < 1)
        {
            String commandsBrief = ""; for( Command cmd : commands )
                commandsBrief += cmd.help( true ) + "\n";

            MessageUtilities.sendPrivateMsg( INTRO + "__**Available commands**__\n" +
                    commandsBrief + USAGE_EXTENDED, event.getAuthor(), null );
        }
        // otherwise get the command using the first arg
        else
        {
            String tmp = args[0].startsWith("lf") ? "lf" : args[0];
            Command cmd = cmdHandler.getCommand( tmp );
            if( cmd != null )
            {
                String helpMsg = cmd.help(false);
                MessageUtilities.sendPrivateMsg(helpMsg, event.getAuthor(), null);
            }
        }
    }
}
