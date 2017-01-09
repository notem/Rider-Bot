package io.lfgdiscordbot.commands.general;

import io.lfgdiscordbot.Main;
import io.lfgdiscordbot.commands.Command;
import io.lfgdiscordbot.core.group.GroupTable;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 */
public class RenewCommand implements Command
{
    private static final String USAGE_BRIEF = "**;renew** - renews the LFG entry";
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

        if( gTable.isAnOwner(event.getAuthor().getId()) )
        {
            gTable.renew( event.getAuthor().getId() );
        }
    }
}
