package ws.nmathe.rider.core.group;

import ws.nmathe.rider.Main;
import ws.nmathe.rider.utils.MessageUtilities;
import ws.nmathe.rider.utils.__out;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 */
class Group
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
        return time.until(ZonedDateTime.now(), ChronoUnit.SECONDS) >= Main.getBotSettings().getExpire();
    }

    boolean isFull()
    {
        return this.amount > 0 && (this.amount - this.joinees.size()) <= 0;
    }

    String getOwner()
    {
        return this.owner;
    }

    String getGroupName()
    {
        return this.groupName;
    }

    synchronized void addJoinee( String user )
    {
        this.joinees.add(user);
        MessageUtilities.editMsg( this.toMessageString(), this.message, null );
    }

    synchronized void removeJoinee( String user )
    {
        this.joinees.remove(user);
        MessageUtilities.editMsg( this.toMessageString(), this.message, null );
    }

    synchronized Collection<String> getJoinees()
    {
        return this.joinees;
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
            string += "Looking for <~~~~~~~~> players for [" + this.groupName + "](" +
                    this.time.until(ZonedDateTime.now(), ChronoUnit.MINUTES) + "min)```";

        else if( this.amount - this.joinees.size() <= 0 )
            string += "Looking for <  FULL  > players for [" + this.groupName + "](" +
                    this.time.until(ZonedDateTime.now(), ChronoUnit.MINUTES) + "min)```";

        else
            string += "Looking for < " + (this.amount - this.joinees.size()) + " more > players for [" + this.groupName +
                    "](" + this.time.until(ZonedDateTime.now(), ChronoUnit.MINUTES) + "min)```";

        return string;
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

    void update()
    {
        MessageUtilities.editMsg( this.toMessageString(), this.message, null );
    }

    void deleteMsg()
    {
        MessageUtilities.deleteMsg(this.message, null);
    }

}
