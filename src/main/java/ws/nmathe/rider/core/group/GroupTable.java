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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class GroupTable
{
    private ConcurrentHashMap<String, Group> leaderToGroupMap;
    private ConcurrentHashMap<String, String> titleToOwnerMap;
    private ConcurrentHashMap<String, String> memberToOwnerMap;

    GroupTable()
    {
        this.leaderToGroupMap = new ConcurrentHashMap<>();
        this.titleToOwnerMap = new ConcurrentHashMap<>();
        this.memberToOwnerMap = new ConcurrentHashMap<>();
    }

    public boolean isALeader(String leader )
    {
        return this.leaderToGroupMap.containsKey( leader );
    }

    public boolean isATitle(String title )
    {
        return this.titleToOwnerMap.containsKey( title );
    }

    public boolean isAMember(String member )
    {
        return this.memberToOwnerMap.containsKey( member );
    }

    public boolean isAMember(String member, String leader )
    {
        return this.memberToOwnerMap.containsKey(member) && this.memberToOwnerMap.get(member).equals(leader);
    }

    public void addGroup(String owner, long amount, String groupName, TextChannel channel)
    {
        this.leaderToGroupMap.put( owner, new Group( owner, new ArrayList<>(), amount, groupName, ZonedDateTime.now(), channel ) );
        this.titleToOwnerMap.put( groupName, owner );
    }

    public void removeGroup( String owner )
    {
        Group groupToBeRemoved = this.leaderToGroupMap.get(owner);

        // remove from hashmaps and delete the message
        this.titleToOwnerMap.remove( groupToBeRemoved.getGroupName() );
        for( String joinee : groupToBeRemoved.getJoinees())
        {
            this.memberToOwnerMap.remove( joinee );
        }
        groupToBeRemoved.deleteMsg();
        this.leaderToGroupMap.remove(owner);
    }

    private Group getGroupByLeader(String leader )
    {
        return this.leaderToGroupMap.get( leader );
    }

    private Group getGroupByTitle(String title )
    {
        return this.leaderToGroupMap.get( this.titleToOwnerMap.get( title ) );
    }

    private Group getGroupByMember(String member )
    {
        return this.leaderToGroupMap.get( this.memberToOwnerMap.get( member ) );
    }

    public void addMember(String key, String member )
    {
        if( this.isALeader( key ) && !this.getGroupByLeader( key ).isFull() )
        {
            this.getGroupByLeader( key ).addJoinee( member );
            this.memberToOwnerMap.put( member, key );
        }
        else if( this.isAMember( key ) && !this.getGroupByMember( key ).isFull() )
        {
            this.getGroupByMember( key ).addJoinee( member );
            this.memberToOwnerMap.put( member, key );
        }
        else if( this.isATitle( key ) && !this.getGroupByTitle(key).isFull() )
        {
            this.getGroupByTitle( key ).addJoinee( member );
            this.memberToOwnerMap.put( member, this.titleToOwnerMap.get( key ) );
        }
    }

    public void removeMember(String Member )
    {
        this.getGroupByMember( Member ).removeJoinee( Member );
        this.memberToOwnerMap.remove( Member );
    }

    public void renew( String leader )
    {
        this.getGroupByLeader(leader).renew();
    }

    void removeExpired(String guildId)
    {
        ArrayList<String> expired = new ArrayList<>();
        this.leaderToGroupMap.forEach( (leader, group) ->
        {
            if( group.isExpired() )
            {
                expired.add(leader);

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

    public long getSize()
    {
        return this.leaderToGroupMap.size();
    }
}
