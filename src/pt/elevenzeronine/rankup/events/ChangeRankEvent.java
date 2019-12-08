package pt.elevenzeronine.rankup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pt.elevenzeronine.rankup.factory.Rank;


public class ChangeRankEvent extends Event {

    private Player p;

    private Rank r;

    private static final HandlerList handlers = new HandlerList();

    public ChangeRankEvent(Player p, Rank r) {
        this.p = p;
        this.r = r;
    }

    public Player getPlayer() {
        return this.p;
    }

    public void setPlayer(Player p) {
        this.p = p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
