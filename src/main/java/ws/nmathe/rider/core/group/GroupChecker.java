package ws.nmathe.rider.core.group;

import ws.nmathe.rider.Main;

import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class GroupChecker implements Runnable
{
    private ConcurrentHashMap<String, GroupTable> mapOfGroupTables;

    GroupChecker(ConcurrentHashMap<String, GroupTable> map)
    {
        this.mapOfGroupTables = map;
    }

    @Override
    public void run()
    {
        this.mapOfGroupTables.forEach( (guildId, gTable) ->
                Main.getGroupManager().executor.submit(() -> gTable.removeExpired(guildId))
        );
    }
}
