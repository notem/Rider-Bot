package ws.nmathe.rider.commands.general;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.core.group.GroupTable;
import ws.nmathe.rider.utils.__out;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 */
public class JoinCommand implements Command
{
    private static final String USAGE_BRIEF = "**;join <arg>** - joins a group if the player" +
            " limit hasn't been reached.";
    private static final String USAGE_EXTENDED = "<arg> may be the group leader's name or the group name";
    private static final String EXAMPLES = "Ex1. **;join @noteless**" +
            "\nEx2. **;join expert trials roulette**";

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
        return args.length >= 1;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        String key = "";
        for( int i = 0; i < args.length - 1 ; i++)
        {
            key += args[i] + " ";
        }
        key += args[args.length - 1];

        GroupTable gTable = Main.getGroupManager().getGroupTable( event.getGuild().getId());

        if( !gTable.isAJoinee( event.getAuthor().getId() ) )
        {
            gTable.addJoinee( key, event.getAuthor().getId() );
        }

        if( gTable.isAnOwner( event.getAuthor().getId() ) )
        {
            gTable.removeGroup( event.getAuthor().getId() );

            Guild guild = event.getGuild();
            Member member = guild.getMember(event.getAuthor());

            List<Role> roles = guild.getRolesByName(chanName, true);
            if( !roles.isEmpty() && guild.getMember(Main.getBotSelfUser()).hasPermission(Permission.MANAGE_ROLES) )
            {
                try
                {
                    guild.getController().removeRolesFromMember(member, roles.get(0)).queue();
                }
                catch( Exception e )
                {
                    __out.printOut(this.getClass(), e.getMessage());
                }
            }
        }
    }
}
