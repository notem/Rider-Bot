package ws.nmathe.rider.core.group;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.utils.MessageUtilities;
import ws.nmathe.rider.utils.__out;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
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
    private Integer platform;

    Group(String owner, Collection<String> joinees, long amount, String groupName, ZonedDateTime time, TextChannel chan, Integer platform )
    {
        this.owner = owner;
        this.joinees = joinees;
        this.amount = amount;
        this.groupName = groupName;
        this.time = time;
        this.platform = platform;

        MessageUtilities.sendMsg(this.toMessage(), chan, (Message msg) -> {this.message = msg;});
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
        MessageUtilities.editMsg( this.toMessage(), this.message, null );
    }

    synchronized void removeJoinee( String user )
    {
        this.joinees.remove(user);
        MessageUtilities.editMsg( this.toMessage(), this.message, null );
    }

    synchronized Collection<String> getJoinees()
    {
        return this.joinees;
    }

    private Message toMessage()
    {
        String top = "";

        top += "<@" + this.owner + ">";
        for( String joinee : this.joinees)
        {
            top += ", <@" + joinee + ">";
        }

        String string = "```md\n";

        if( this.amount <= 0 )
            string += "Looking for <~      ~> players for [" + this.groupName + "](" +
                    this.time.until(ZonedDateTime.now(), ChronoUnit.MINUTES) + "min)```";

        else if( this.amount - this.joinees.size() <= 0 )
            string += "Looking for <  FULL  > players for [" + this.groupName + "](" +
                    this.time.until(ZonedDateTime.now(), ChronoUnit.MINUTES) + "min)```";

        else
            string += "Looking for < " + (this.amount - this.joinees.size()) + " more > players for [" + this.groupName +
                    "](" + this.time.until(ZonedDateTime.now(), ChronoUnit.MINUTES) + "min)```";

        Color color;
        String platform;
        switch(this.platform)
        {
            case 0:
                color = Color.blue;
                platform = "PS4";
                break;
            case 1:
                color = Color.green;
                platform = "XB1";
                break;
            case 2:
                color = Color.MAGENTA;
                platform = "PC";
                break;
            default:
                color = Color.lightGray;
                platform = "Unspecified";
                break;
        }

        return (new MessageBuilder().append(top).setEmbed(new EmbedBuilder().setDescription(string).setColor(color).setFooter("Platform: "+platform, null).build())).build();
    }

    void renew()
    {
        this.time = ZonedDateTime.now();

        MessageChannel chan = this.message.getChannel();

        MessageUtilities.deleteMsg(this.message, (ignored) ->
                MessageUtilities.sendMsg( this.toMessage(), chan, (message) ->
                        this.message = message)
        );
    }

    void update()
    {
        MessageUtilities.editMsg( this.toMessage(), this.message, null );
    }

    void deleteMsg()
    {
        MessageUtilities.deleteMsg(this.message, null);
    }

}
