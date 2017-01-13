package ws.nmathe.rider.core.group;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.utils.MessageUtilities;
import ws.nmathe.rider.utils.__out;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manages the connected guilds and their guild specific group tables.
 * However, (under the current implementation) this class does not operate
 * on group tables. When adding a group to a group table, the guild's group
 * table can be obtained through the groupManager but the caller must
 * interface with the group table itself to add the group.
 */
public class GroupManager
{
    private ConcurrentHashMap<String, GroupTable> guildToGroupTableMap;
    final ExecutorService executor = Executors.newCachedThreadPool();

    public GroupManager()
    {
        this.guildToGroupTableMap = new ConcurrentHashMap<>();
    }

    public void init()
    {
        String chanName = Main.getBotSettings().getChannel();

        for( Guild guild : Main.getBotJda().getGuilds() )
        {
            // put the guild in the map
            this.guildToGroupTableMap.put( guild.getId(), new GroupTable() );

            // if the guild has a lfg channel, delete messages in the channel
            if( !guild.getTextChannelsByName(chanName, false).isEmpty() )
            {
                TextChannel lfgChannel = guild.getTextChannelsByName(chanName, false).get(0);

                Consumer<List<Message>> clearChannel = (list) ->
                {
                    for( Message message : list )
                    {
                        MessageUtilities.deleteMsg( message, null );
                    }
                };

                try
                {
                    lfgChannel.getHistory().retrievePast(50).queue(clearChannel);
                }
                catch( Exception e )
                {
                    __out.printOut(this.getClass(), "[" + guild.getId() + "] " + e.getMessage());
                }
            }
        }

        // start the timer that checks for expired LFG entries
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate( new GroupChecker( this.guildToGroupTableMap ), 0, 60, TimeUnit.SECONDS);
    }

    public void addGuild( String guildId )
    {
        this.guildToGroupTableMap.put( guildId, new GroupTable() );
    }

    public void removeGuild( String guildId )
    {
        this.guildToGroupTableMap.remove( guildId );
    }

    public GroupTable getGroupTable(String guildId )
    {
        if( !this.guildToGroupTableMap.containsKey( guildId ) )
        {
            this.guildToGroupTableMap.put( guildId, new GroupTable() );
        }
        return this.guildToGroupTableMap.get( guildId );
    }

    public long getTotalSize()
    {
        long size = 0;
        for( GroupTable table : this.guildToGroupTableMap.values() )
        {
            size += table.getSize();
        }
        return size;
    }
}
