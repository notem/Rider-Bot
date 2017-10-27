package ws.nmathe.rider.commands.admin;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.rider.Main;
import ws.nmathe.rider.commands.Command;

/**
 */
public class NowPlayingCommand implements Command
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
        String str = "";
        for( int i = 0; i<args.length-1 ;i++ )
        {
            str += args[i] + " ";
        }
        str += args[args.length-1];
        Main.getBotJda().getPresence().setGame(Game.of(str));
    }
}
