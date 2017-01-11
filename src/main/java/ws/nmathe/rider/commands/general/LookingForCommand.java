package ws.nmathe.rider.commands.general;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.core.group.GroupTable;
import ws.nmathe.rider.utils.__out;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 */
public class LookingForCommand implements Command
{
    private static final String USAGE_BRIEF = "**;lfg <arg>** - creates a LFG entry with no player limit.\n" +
            "**;lf[x]m <arg>** - creates a LFG entry with player limit equals to whatever [x] is";
    private static final String USAGE_EXTENDED = "<arg> should be some description of what you are forming" +
            "a group for. If another group already exists with the same name, a 'x' is appended to your LFG group" +
            "name. If your guild has a 'lfg' role and has this bot has the Role Management permission, 'lfg' will be" +
            " added to your role. The role will later be removed when the entry is closed or expires.\n\n" +
            "Ex1. **;lfg expert trials roulette**\n" +
            "Ex2. **;lf4m expert trials roulette**";

    private String chanName = Main.getBotSettings().getChannel();

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
        return args.length >= 2;
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
        if( !args[index].equals("g") )  // if 'lfg' then amount = 0, otherwise 'lf[x]m' amount = x
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

        if( gTable.groupNameExists( groupName ) ) // if a group already has that name, append an 'x'
        {
            groupName += "x";
        }

        // get the lfg text channel and add the group
        TextChannel channel = event.getGuild().getTextChannelsByName(chanName,false).get(0);
        gTable.addGroup( owner, amount, groupName, channel );

        Guild guild = event.getGuild();
        Member member = guild.getMember(event.getAuthor());

        List<Role> roles = guild.getRolesByName(chanName, true);
        if( !roles.isEmpty() && guild.getMember(Main.getBotSelfUser()).hasPermission(Permission.MANAGE_ROLES) )
        {
            try
            {
                guild.getController().addRolesToMember(member, roles.get(0)).queue();
            }
            catch( Exception e )
            {
                __out.printOut(this.getClass(), e.getMessage());
            }
        }
    }
}
