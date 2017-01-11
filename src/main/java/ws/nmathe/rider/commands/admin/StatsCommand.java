package ws.nmathe.rider.commands.admin;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.utils.MessageUtilities;

/**
 */
public class StatsCommand implements Command
{
    @Override
    public String help(boolean brief)
    {
        return null;
    }

    @Override
    public boolean verify(String[] args, MessageReceivedEvent event)
    {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        String msg = "***Current Status***\n";
        msg += "Connected guilds: " + Main.getBotJda().getGuilds().size() + "\n";

        MessageUtilities.sendPrivateMsg(msg, event.getAuthor(), null);
    }
}
