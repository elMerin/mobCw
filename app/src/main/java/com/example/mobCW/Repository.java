package com.example.mobCW;

import java.util.LinkedList;

/**
 * Class that stores data as linked lists
 * @author Merin Haslacher, S1624420
 */
public class Repository {

    private static LinkedList<Incident> incidents = new LinkedList<Incident>();
    private static LinkedList<Roadwork> currentRoadworks =  new LinkedList<Roadwork>();
    private static LinkedList<Roadwork> plannedRoadworks =  new LinkedList<Roadwork>();

    public static LinkedList<Incident> getIncidents() {
        return incidents;
    }

    public static void setIncidents(LinkedList<Incident> incidents) {
        Repository.incidents = incidents;
    }

    public static LinkedList<Roadwork> getCurrentRoadworks() {
        return currentRoadworks;
    }

    public static void setCurrentRoadworks(LinkedList<Roadwork> currentRoadworks) {
        Repository.currentRoadworks = currentRoadworks;
    }

    public static LinkedList<Roadwork> getPlannedRoadworks() {
        return plannedRoadworks;
    }

    public static void setPlannedRoadworks(LinkedList<Roadwork> plannedRoadworks) {
        Repository.plannedRoadworks = plannedRoadworks;
    }
}
