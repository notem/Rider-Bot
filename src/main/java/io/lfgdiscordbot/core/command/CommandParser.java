package io.lfgdiscordbot.core.command;

import io.lfgdiscordbot.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * used to create a CommandContainer object which contains the parsed tokens of the user input
 * along with the originating event object
 */
class CommandParser
{
    CommandContainer parse(MessageReceivedEvent e)
    {
        String raw = e.getMessage().getRawContent();

        /// trim off the prefix
        String trimmed;
        if( raw.startsWith( Main.getBotSettings().getAdminPrefix() ))
            trimmed = raw.replaceFirst(Main.getBotSettings().getAdminPrefix(), "");
        else
            trimmed = raw.replaceFirst( Main.getBotSettings().getCommandPrefix(), "");

        // split the trimmed string into arguments
        String[] splitTrimmed = trimmed.split(" ");

        // store splitTrimmed into an ArrayList
        ArrayList<String> split = new ArrayList<>();
        Collections.addAll(split, splitTrimmed);

        String invoke = split.get(0);
        ArrayList<String> args = new ArrayList<>();
        if( invoke.startsWith("lf") )
        {
            args.add(0,invoke.replace("lf",""));
            invoke = "lf";
        }
        args.addAll(split.subList(1, split.size()));

        return new CommandContainer(invoke, args, e);
    }

    /** an object that holds the parsed user input in the MessageReceivedEvent e. **/
    class CommandContainer
    {
        final String invoke;         // the first argument in the user's input
        final String[] args;         // all arguments after the initial argument
        final MessageReceivedEvent event;    // the originating event

        // constructor for CommandContainer
        CommandContainer(String invoke, ArrayList<String> args, MessageReceivedEvent e)
        {
            this.invoke = invoke;
            this.args = args.toArray(new String[args.size()]);
            this.event = e;
        }
    }
}
