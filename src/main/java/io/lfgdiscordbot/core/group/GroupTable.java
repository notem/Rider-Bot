package io.lfgdiscordbot.core.group;

import net.dv8tion.jda.core.entities.TextChannel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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
    }

    public void removeGroup( String owner )
    {
        Group groupToBeRemoved = this.ownerToGroupMap.get(owner);

        // remove from hashmaps and delete the message
        this.nameToOwnerMap.remove( groupToBeRemoved.getGroupName() );
        this.joineeToOwnerMap.remove( groupToBeRemoved.getGroupName() );
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
}
