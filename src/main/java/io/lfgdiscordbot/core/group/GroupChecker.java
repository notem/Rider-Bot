package io.lfgdiscordbot.core.group;

import io.lfgdiscordbot.Main;

import java.util.HashMap;

/**
 */
public class GroupChecker implements Runnable
{
    private HashMap<String, GroupTable> mapOfGroupTables;

    GroupChecker(HashMap<String, GroupTable> map)
    {
        this.mapOfGroupTables = map;
    }

    @Override
    public void run()
    {
        this.mapOfGroupTables.forEach( (guildId, gTable) ->
                Main.getGroupManager().executor.submit(gTable::removeExpired)
        );
    }
}
