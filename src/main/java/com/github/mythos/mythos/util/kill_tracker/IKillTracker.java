package com.github.mythos.mythos.util.kill_tracker;

public interface IKillTracker {
    int getAwakenedKills();
    void addAwakenedKills();
    void setAwakenedKills(int count);
}