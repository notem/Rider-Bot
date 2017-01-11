package ws.nmathe.rider.core.command;

import ws.nmathe.rider.commands.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import ws.nmathe.rider.commands.admin.StatsCommand;
import ws.nmathe.rider.commands.general.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public class CommandHandler
{
    private final CommandParser commandParser = new CommandParser();      // parses command strings into containers
    private final ExecutorService commandExec = Executors.newCachedThreadPool(); // thread pool for running commands
    private HashMap<String, Command> commands;         // maps Command to invoke string
    private HashMap<String, Command> adminCommands;    // ^^ but for admin commands

    public CommandHandler()
    {
        commands = new HashMap<>();
        adminCommands = new HashMap<>();
    }

    public void init()
    {
        // add bot commands with their lookup name
        commands.put("lf", new LookingForCommand() );
        commands.put("close", new CloseCommand() );
        commands.put( "join", new JoinCommand() );
        commands.put( "renew", new RenewCommand() );
        commands.put( "leave", new LeaveCommand() );
        commands.put( "help", new HelpCommand() );

        // add administrator commands with their lookup name
        adminCommands.put( "stats", new StatsCommand() );
    }

    public void handleCommand( MessageReceivedEvent event, Integer type )
    {
        CommandParser.CommandContainer cc = commandParser.parse( event );
        if( type == 0 )
        {
            handleGeneralCommand( cc );
        }
        else if( type == 1 )
        {
            handleAdminCommand( cc );
        }

    }

    private void handleGeneralCommand(CommandParser.CommandContainer cc)
    {
        // if the invoking command appears in commands
        if(commands.containsKey(cc.invoke))
        {
            boolean valid = commands.get(cc.invoke).verify(cc.args, cc.event);

            // do command action if valid arguments
            if(valid)
            {
                commandExec.submit( () -> {
                    try
                    {
                        commands.get(cc.invoke).action(cc.args, cc.event);
                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void handleAdminCommand(CommandParser.CommandContainer cc)
    {
        // for admin commands
        if(adminCommands.containsKey(cc.invoke))
        {
            boolean valid = adminCommands.get(cc.invoke).verify(cc.args, cc.event);

            // do command action if valid arguments
            if (valid)
            {
                commandExec.submit( () -> {
                    try
                    {
                        adminCommands.get(cc.invoke).action(cc.args, cc.event);
                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public Collection<Command> getCommands()
    {
        return commands.values();
    }

    public Command getCommand( String invoke )
    {
        // check if command exists, if so return it
        if( commands.containsKey(invoke) )
            return commands.get(invoke);

        else    // otherwise return null
            return null;
    }

}
