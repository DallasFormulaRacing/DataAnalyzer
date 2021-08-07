/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import dataanalyzer.DataAnalyzer;
import java.util.ArrayList;

/**
 *
 * @author aribdhuka
 */
public abstract class Command {
    
    String commandName;
    ArrayList<String> followers;
    
    public Command() {
        commandName = "";
        followers = new ArrayList<>();
    }
    
    public Command(String commandName, ArrayList<String> followers) {
        this.commandName = commandName;
        this.followers = followers;
    }
    
    public void addFollower(String follower) {
        followers.add(follower);
    }
    
    /**
     * Deletes the first instance of the argument provided in the list of followers
     * @param follower item to delete
     * @return true if an item was removed, false if no matching item found
     */
    public boolean removeFollower(String follower) {
        for(int i = 0; i < followers.size(); i++) {
            if(followers.get(i).equals(follower)) {
                followers.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public void clearFollowers() {
        followers.clear();
    }
    
    public String[] getFollowers() {
        String[] followersArray = new String[followers.size()];
        for(int i=0; i < followersArray.length; i++) {
            followersArray[i] = followers.get(i);
        }
        return followersArray;
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }
    
    public abstract void doAction(String[] cmd, DataAnalyzer da);
    
}
