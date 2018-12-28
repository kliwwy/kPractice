package me.wilk3z.kpractice.queues;

import me.wilk3z.kpractice.kits.Kit;

public class Queue
{
    public Kit kit;
    public boolean ranked;
    public int[] eloRange;

    public Queue(Kit kit, boolean ranked)
    {
        this.kit = kit;
        this.ranked = ranked;
    }

    public Queue(Kit kit, boolean ranked, int[] eloRange)
    {
        this.kit = kit;
        this.ranked = ranked;
        this.eloRange = eloRange;
    }

    public Kit getKit()
    {
        return kit;
    }

    public boolean isRanked()
    {
        return ranked;
    }

    public int[] getEloRange()
    {
        return eloRange;
    }

    public void setEloRange(int[] eloRange)
    {
        this.eloRange = eloRange;
    }

    public boolean isInEloRange(int elo)
    {
        if(elo >= eloRange[0] && elo <= eloRange[1]) return true;
        else return false;
    }

    public boolean isSame(Queue queue)
    {
        if(kit.getName().equals(queue.getKit().getName()) && ranked == queue.isRanked()) return true;
        else return false;
    }
}
