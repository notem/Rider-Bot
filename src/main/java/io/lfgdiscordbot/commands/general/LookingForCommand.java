package io.lfgdiscordbot.commands.general;

import io.lfgdiscordbot.Main;
import io.lfgdiscordbot.commands.Command;
import io.lfgdiscordbot.core.group.GroupTable;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 */
public class LookingForCommand implements Command
{
    private static final String USAGE_BRIEF = "**;lfg [GROUP NAME]** - creates a LFG entry with no player limit.\n" +
            "**;lf[x]m [GROUP NAME]** - creates a LFG entry with player limit equals to whatever [x] is";
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
        String owner = event.getAuthor().getId();
        GroupTable gTable = Main.getGroupManager().getGroupTable( event.getGuild().getId() );

        if( gTable.isAnOwner( owner ) )
        {
            gTable.removeGroup( owner );
        }
        if( gTable.isAJoinee( owner ) )
        {
            gTable.removeJoinee( owner );
        }

        int index = 0;

        // get the group size
        long amount = 0;
        if( !args[index].equals("g") )
        {
            amount = Integer.parseInt(args[index].replace("m",""));
        }

        index++;

        // get the group name
        String groupName = "";
        for( ; index < args.length - 1 ; index++)
        {
            groupName += args[index] + " ";
        }
        groupName += args[args.length-1];

        // get the lfg text channel and add the group
        TextChannel channel = event.getGuild().getTextChannelsByName("lfg",false).get(0);
        gTable.addGroup( owner, amount, groupName, channel );
    }
}
