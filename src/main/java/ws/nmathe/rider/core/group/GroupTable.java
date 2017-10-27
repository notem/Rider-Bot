package ws.nmathe.rider.core.group;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.utils.MessageUtilities;
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

    public void addGroup(String owner, long amount, String groupName, TextChannel channel, Integer platform)
    {
        this.leaderToGroupMap.put( owner, new Group(owner, new ArrayList<>(), amount, groupName, ZonedDateTime.now(), channel, platform) );
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

    public void addMember(String key, String member)
    {
        if(this.isALeader( key ) && !this.getGroupByLeader( key ).isFull())
        {
            Group group = this.getGroupByLeader(key);
            String str = "<@" + member + "> has joined ``" + group.getGroupName() + "``!";
            group.getJoinees().forEach((userId)->
            {
                User user = Main.getBotJda().getUserById(userId);
                MessageUtilities.sendPrivateMsg(str, user, null);
            });
            User leader = Main.getBotJda().getUserById(group.getOwner());
            MessageUtilities.sendPrivateMsg(str, leader, null);
            group.addJoinee(member);
            this.memberToOwnerMap.put( member, key );

            if(this.getGroupByLeader(key).isFull())
            {
                String str2 = "``" + group.getGroupName() + "`` has been filled!";
                group.getJoinees().forEach((userId)->
                {
                    User user = Main.getBotJda().getUserById(userId);
                    MessageUtilities.sendPrivateMsg(str2, user, null);
                });
                MessageUtilities.sendPrivateMsg(str2, leader, null);
            }
        }
        else if( this.isAMember( key ) && !this.getGroupByMember( key ).isFull() )
        {
            Group group = this.getGroupByMember( key );
            String str = "<@" + member + "> has joined ``" + group.getGroupName() + "``!";
            group.getJoinees().forEach((userId)->
            {
                User user = Main.getBotJda().getUserById(userId);
                MessageUtilities.sendPrivateMsg(str, user, null);
            });
            User leader = Main.getBotJda().getUserById(group.getOwner());
            MessageUtilities.sendPrivateMsg(str, leader, null);
            group.addJoinee(member);
            this.memberToOwnerMap.put( member, key );

            if(this.getGroupByMember(key).isFull())
            {
                String str2 = "``" + group.getGroupName() + "`` has been filled!";
                group.getJoinees().forEach((userId)->
                {
                    User user = Main.getBotJda().getUserById(userId);
                    MessageUtilities.sendPrivateMsg(str2, user, null);
                });
                MessageUtilities.sendPrivateMsg(str2, leader, null);
            }
        }
        else if( this.isATitle( key ) && !this.getGroupByTitle(key).isFull() )
        {
            Group group = this.getGroupByTitle( key );
            String str = "<@" + member + "> has joined ``" + group.getGroupName() + "``!";
            group.getJoinees().forEach((userId)->
            {
                User user = Main.getBotJda().getUserById(userId);
                MessageUtilities.sendPrivateMsg(str, user, null);
            });
            User leader = Main.getBotJda().getUserById(group.getOwner());
            MessageUtilities.sendPrivateMsg(str, leader, null);
            group.addJoinee(member);
            this.memberToOwnerMap.put( member, this.titleToOwnerMap.get( key ) );

            if(this.getGroupByTitle(key).isFull())
            {
                String str2 = "``" + group.getGroupName() + "`` has been filled!";
                group.getJoinees().forEach((userId)->
                {
                    User user = Main.getBotJda().getUserById(userId);
                    MessageUtilities.sendPrivateMsg(str2, user, null);
                });
                MessageUtilities.sendPrivateMsg(str2, leader, null);
            }
        }

    }

    public void removeMember(String Member )
    {
        Group group = this.getGroupByMember(Member);
        group.removeJoinee( Member );
        String str = "<@" + Member + "> has left ``" + group.getGroupName() + "``!";
        group.getJoinees().forEach((userId)->
        {
            User user = Main.getBotJda().getUserById(userId);
            MessageUtilities.sendPrivateMsg(str, user, null);
        });
        User leader = Main.getBotJda().getUserById(group.getOwner());
        MessageUtilities.sendPrivateMsg(str, leader, null);
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
