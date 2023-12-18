/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 *
 * @author aribd
 */
public class LoadingEvent {
    
    private boolean loading;
    private String loadingText;
    private LoadingState state;
    
    public LoadingEvent() {
        loading = false;
        loadingText = "";
        state = LoadingState.UNKNOWN;
    }
    
    public LoadingEvent(LoadingState state, String loadingText) {
        loading = (state == LoadingState.RUNNING);
        this.state = state;
        this.loadingText = loadingText;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean isLoading) {
        this.loading = isLoading;
    }

    public String getLoadingText() {
        return loadingText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public LoadingState getState() {
        return state;
    }

    public void setState(LoadingState state) {
        this.state = state;
    }
    
    //Enumeration definition for Theme
    public enum LoadingState {
        STARTING, RUNNING, FINISHED, UNKNOWN;
    }
    
}
