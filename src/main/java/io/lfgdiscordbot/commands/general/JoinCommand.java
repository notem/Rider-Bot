package io.lfgdiscordbot.commands.general;

import io.lfgdiscordbot.Main;
import io.lfgdiscordbot.commands.Command;
import io.lfgdiscordbot.core.group.GroupTable;
import io.lfgdiscordbot.utils.__out;
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
    private static final String USAGE_BRIEF = "**;join [GROUP NAME or GROUP LEADER]** - joins a group if the player" +
            " limit hasn't been reached.";
    private static final String USAGE_EXTENDED = "";

    private String chanName = Main.getBotSettings().getChannel();

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
