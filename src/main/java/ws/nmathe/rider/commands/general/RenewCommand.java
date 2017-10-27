package ws.nmathe.rider.commands.general;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.core.group.GroupTable;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 */
public class RenewCommand implements Command
{
    private static final String invoke = Main.getBotSettings().getCommandPrefix() + "renew";
    private static final String USAGE_BRIEF = "``"+invoke+"`` ~ renews your LFG entry for another " +
            Main.getBotSettings().getExpire()/60 + " minutes";
    private static final String USAGE_EXTENDED = "Ex. **"+invoke+"**";

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
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        GroupTable gTable = Main.getGroupManager().getGroupTable( event.getGuild().getId() );

        if( gTable.isALeader(event.getAuthor().getId()) )
        {
            gTable.renew( event.getAuthor().getId() );
        }
    }
}
