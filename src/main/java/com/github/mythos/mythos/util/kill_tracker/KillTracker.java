package com.github.mythos.mythos.util.kill_tracker;

public class KillTracker implements IKillTracker{
    private int awakenedKills = 0;

    public int getAwakenedKills() {
        return this.awakenedKills;
    }

    public void addAwakenedKills() {
        this.awakenedKills++;
    }


    public void setAwakenedKills(int awakenedKills) {
        this.awakenedKills = awakenedKills;
    }
}
