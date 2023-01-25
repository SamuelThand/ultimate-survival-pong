package models;

import controllers.Observer;

/**
 * Lays out the required functionality for a Subject in the Observer Design Pattern.
 *
 * @author Samuel Thand
 */
public interface Subject {

    /**
     * Add an Observer to this Subject.
     *
     * @param observer The Observer to add to this Subject.
     */
    void addObserver(Observer observer);

    /**
     * Remove an Observer from this Subject.
     *
     * @param observer The Observer to remove from this Subject.
     */
    void removeObserver(Observer observer);

    /**
     * Notify all Observers that there has been a change of state.
     */
    void notifyObservers();
}
