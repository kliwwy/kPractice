package me.wilk3z.kpractice.teams.events;

import java.util.List;

public class TeamEvent
{
    public String eventName;
    public List<String> players;

    public TeamEvent(String eventName, List<String> players)
    {
        this.eventName = eventName;
        this.players = players;
    }

    public String getEventName()
    {
        return eventName;
    }

    public List<String> getPlayers()
    {
        return players;
    }
}
