package ws.nmathe.rider.utils;

import ws.nmathe.rider.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

/**
 */
public class VerifyUtilities
{
    public static boolean verifyPermissions( Guild guild )
    {
        Member botAsMember = guild.getMember(Main.getBotSelfUser());

        List<Permission> perms = Arrays.asList( // required permissions
                Permission.MESSAGE_HISTORY, Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE, Permission.MESSAGE_MANAGE
        );

        List<TextChannel> chans = guild.getTextChannelsByName(Main.getBotSettings().getChannel(),true);

        return !(chans.isEmpty() || !botAsMember.hasPermission(chans.get(0), perms));
    }
}
