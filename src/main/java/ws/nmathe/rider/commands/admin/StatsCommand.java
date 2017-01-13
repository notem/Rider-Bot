package ws.nmathe.rider.commands.admin;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;
import ws.nmathe.rider.utils.MessageUtilities;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 */
public class StatsCommand implements Command
{
    @Override
    public String help(boolean brief)
    {
        return null;
    }

    @Override
    public boolean verify(String[] args, MessageReceivedEvent event)
    {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        String msg = "```python\n";
        msg += "Guilds    : " + Main.getBotJda().getGuilds().size() + "\n";
        msg += "Table size: " + Main.getGroupManager().getTotalSize() + "\n";
        Runtime rt = Runtime.getRuntime();
        msg += "Memory-total: " +rt.totalMemory()/1024/1024 + " MB\n" +
                "      -free : " + rt.freeMemory()/1024/1024 + " MB\n" +
                "      -max  : " + rt.maxMemory()/1024/1024 + " MB\n";
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        msg += "Uptime: " + rb.getUptime()/1000/60 + " minute(s)";
        msg += "```";

        MessageUtilities.sendPrivateMsg(msg, event.getAuthor(), null);
    }
}
