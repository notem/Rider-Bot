package io.lfgdiscordbot.core.group;

import io.lfgdiscordbot.Main;
import io.lfgdiscordbot.utils.MessageUtilities;
import io.lfgdiscordbot.utils.__out;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.function.Consumer;

/**
 */
public class GroupManager
{
    private HashMap<String, GroupTable> guildToGroupTableMap;

    public GroupManager()
    {
        this.guildToGroupTableMap = new HashMap<>();
    }

    public void init()
    {
        for( Guild guild : Main.getBotJda().getGuilds() )
        {
            this.guildToGroupTableMap.put( guild.getId(), new GroupTable() );

            if( !guild.getTextChannelsByName("lfg", false).isEmpty() )
            {
                TextChannel lfgChannel = guild.getTextChannelsByName("lfg", false).get(0);

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
            return null;
        }
        return this.guildToGroupTableMap.get( guildId );
    }
}
