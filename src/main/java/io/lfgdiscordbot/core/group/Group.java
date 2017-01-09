package io.lfgdiscordbot.core.group;

import io.lfgdiscordbot.Main;
import io.lfgdiscordbot.utils.MessageUtilities;
import io.lfgdiscordbot.utils.__out;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.ZonedDateTime;
import java.util.Collection;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 */
public class Group
{
    private String owner;
    private Collection<String> joinees;
    private long amount;
    private String groupName;
    private ZonedDateTime time;
    private Message message;

    Group(String owner, Collection<String> joinees, long amount, String groupName, ZonedDateTime time, TextChannel chan )
    {
        this.owner = owner;
        this.joinees = joinees;
        this.amount = amount;
        this.groupName = groupName;
        this.time = time;

        try
        {
            this.message = chan.sendMessage( this.toMessageString() ).block();
        }
        catch (Exception e)
        {
            __out.printOut(this.getClass(),e.getMessage());
        }
    }

    boolean isExpired()
    {
        return time.until(ZonedDateTime.now(), SECONDS) >= Main.getBotSettings().getExpire();
    }

    boolean isFull()
    {
        return this.amount > 0 && (this.amount - this.joinees.size()) <= 0;
    }

    void deleteMsg()
    {
        MessageUtilities.deleteMsg(this.message, null);
    }

    long getAmmount()
    {
        return this.amount;
    }

    String getOwner()
    {
        return this.owner;
    }

    Collection<String> getJoinees()
    {
        return this.joinees;
    }

    String getGroupName()
    {
        return this.groupName;
    }

    void setAmount( long amount )
    {
        this.amount = amount;
    }

    void renew()
    {
        this.time = ZonedDateTime.now();

        MessageChannel chan = this.message.getChannel();

        MessageUtilities.deleteMsg(this.message, (ignored) ->
                MessageUtilities.sendMsg( this.toMessageString(), chan, (message) ->
                        this.message = message)
        );
    }

    void addJoinee( String user )
    {
        this.joinees.add(user);
        MessageUtilities.editMsg( this.toMessageString(), this.message, null );
    }

    void removeJoinee( String user )
    {
        this.joinees.remove(user);
        MessageUtilities.editMsg( this.toMessageString(), this.message, null );
    }

    private String toMessageString()
    {
        String string = "";

        string += "<@" + this.owner + ">";
        for( String joinee : this.joinees)
        {
            string += ", <@" + joinee + ">";
        }
        string += "\n```md\n";

        if( this.amount <= 0 )
            string += "Looking for <~~~~~~~~> players for [" + this.groupName + "](" + this.time.getHour() + ":" + this.time.getMinute() + ")```";

        else if( this.amount - this.joinees.size() <= 0 )
            string += "Looking for <  FULL  > players for [" + this.groupName + "](" + this.time.getHour() + ":" + this.time.getMinute() + ")```";

        else
            string += "Looking for < " + (this.amount - this.joinees.size()) + " more > players for [" + this.groupName +
                    "](" + this.time.getHour() + ":" + this.time.getMinute() + ")```";

        return string;
    }
}
