package io.lfgdiscordbot.core.group;

import io.lfgdiscordbot.Main;
import io.lfgdiscordbot.utils.MessageUtilities;
import io.lfgdiscordbot.utils.__out;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 */
public class GroupManager
{
    private HashMap<String, GroupTable> guildToGroupTableMap;
    final ExecutorService executor = Executors.newCachedThreadPool();

    public GroupManager()
    {
        this.guildToGroupTableMap = new HashMap<>();
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
}
