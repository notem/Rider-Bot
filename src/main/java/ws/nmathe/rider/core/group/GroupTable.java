package ws.nmathe.rider.core.group;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.utils.__out;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 */
public class GroupTable
{
    private HashMap<String, Group> ownerToGroupMap;
    private HashMap<String, String> nameToOwnerMap;
    private HashMap<String, String> joineeToOwnerMap;

    GroupTable()
    {
        this.ownerToGroupMap = new HashMap<>();
        this.nameToOwnerMap = new HashMap<>();
        this.joineeToOwnerMap = new HashMap<>();
    }

    public boolean isAnOwner( String owner )
    {
        return this.ownerToGroupMap.containsKey( owner );
    }

    public boolean groupNameExists( String groupName )
    {
        return this.nameToOwnerMap.containsKey( groupName );
    }

    public boolean isAJoinee( String joinee )
    {
        return this.joineeToOwnerMap.containsKey( joinee );
    }

    public void addGroup(String owner, long amount, String groupName, TextChannel channel)
    {
        this.ownerToGroupMap.put( owner, new Group( owner, new ArrayList<>(), amount, groupName, ZonedDateTime.now(), channel ) );
        this.nameToOwnerMap.put( groupName, owner );
        __out.printOut(this.getClass(), "LFG group '" + groupName + "' created.");
    }

    public void removeGroup( String owner )
    {
        Group groupToBeRemoved = this.ownerToGroupMap.get(owner);

        // remove from hashmaps and delete the message
        this.nameToOwnerMap.remove( groupToBeRemoved.getGroupName() );
        for( String joinee : groupToBeRemoved.getJoinees())
        {
            this.joineeToOwnerMap.remove( joinee );
        }
        groupToBeRemoved.deleteMsg();
        this.ownerToGroupMap.remove(owner);
    }

    private Group getGroupByOwner( String owner )
    {
        return this.ownerToGroupMap.get( owner );
    }

    private Group getGroupByName( String name )
    {
        return this.ownerToGroupMap.get( this.nameToOwnerMap.get( name ) );
    }

    private Group getGroupByJoinee( String joinee )
    {
        return this.ownerToGroupMap.get( this.joineeToOwnerMap.get( joinee ) );
    }

    public void addJoinee( String key, String joinee )
    {
        String owner = key.replace("<@","").replace(">","");

        if( this.isAnOwner( owner ) && !this.getGroupByOwner( owner ).isFull() )
        {
            this.getGroupByOwner( owner ).addJoinee( joinee );
            this.joineeToOwnerMap.put( joinee, owner );
        }
        else if( this.groupNameExists( key ) && !this.getGroupByName(key).isFull() )
        {
            this.getGroupByName( key ).addJoinee( joinee );
            this.joineeToOwnerMap.put( joinee, this.nameToOwnerMap.get( key ) );
        }
    }

    public void removeJoinee( String joinee )
    {
        this.getGroupByJoinee( joinee ).removeJoinee( joinee );
        this.joineeToOwnerMap.remove( joinee );
    }

    public void renew( String owner )
    {
        this.getGroupByOwner(owner).renew();
    }

    void removeExpired(String guildId)
    {
        ArrayList<String> expired = new ArrayList<>();
        this.ownerToGroupMap.forEach( (owner, group) ->
        {
            if( group.isExpired() )
            {
                expired.add(owner);

                __out.printOut(this.getClass(), "LFG group '" + group.getGroupName() + "' expired.");

                Guild guild = Main.getBotJda().getGuildById( guildId );
                Member member = guild.getMemberById(group.getOwner());

                List<Role> roles = guild.getRolesByName(Main.getBotSettings().getChannel(), true);
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
            else
            {
                group.update();
            }
        });

        for( String owner : expired )
        {
            this.removeGroup( owner );
        }
    }
}
