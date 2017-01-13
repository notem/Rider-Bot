package ws.nmathe.rider.commands.general;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.core.group.GroupTable;

/**
 */
public class KickCommand implements Command
{
    private static final String invoke = Main.getBotSettings().getCommandPrefix() + "kick";
    private static final String USAGE_BRIEF = "**"+invoke+" <arg>** - remove the user from your LFG group";
    private static final String USAGE_EXTENDED = "Ex. **"+invoke+" @noteless**";

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
        return args.length == 1;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        String member = args[0].replaceFirst("<@","").replaceFirst(">","");

        GroupTable gTable = Main.getGroupManager().getGroupTable( event.getGuild().getId() );

        if( gTable.isALeader(event.getAuthor().getId()) && gTable.isAMember( member, event.getAuthor().getId()))
            gTable.removeMember(member);
    }
}
