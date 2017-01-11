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
public class CloseCommand implements Command
{
    private static final String USAGE_BRIEF = "**;close** - removes your active LFG entry if you have one";
    private static final String USAGE_EXTENDED = "Ex. **;close**";

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
        // arguments aren't used action. extra args do not matter
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        GroupTable gTable = Main.getGroupManager().getGroupTable( event.getGuild().getId() );

        if( gTable.isAnOwner( event.getAuthor().getId() ) )
        {
            gTable.removeGroup( event.getAuthor().getId() );
        }

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
