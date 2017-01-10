package ws.nmathe.rider.commands.general;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.core.group.GroupTable;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 */
public class LeaveCommand implements Command
{
    private static final String USAGE_BRIEF = "**;leave** - leaves the LFG group you are in";
    private static final String USAGE_EXTENDED = "";

    @Override
    public String help(boolean brief)
    {
        if( brief )
            return USAGE_BRIEF;
        else
            return USAGE_BRIEF + "\n" + USAGE_EXTENDED;
    }

    @Override
    public String verify(String[] args, MessageReceivedEvent event)
    {
        return "";
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        GroupTable gTable = Main.getGroupManager().getGroupTable( event.getGuild().getId() );

        if( gTable.isAJoinee( event.getAuthor().getId() ) )
        {
            gTable.removeJoinee( event.getAuthor().getId() );
        }
    }
}
