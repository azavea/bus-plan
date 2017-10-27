package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.example.Node;

import java.util.Arrays;


public class Bus {

    private Node depot;

    public Bus() {}

    public Bus(Node depot) { this.depot = depot; }

    public Node getDepot() { return this.depot; }
    public void setDepot(Node depot) { this.depot = depot; }

    public String toString() { return "BUS" + this.depot.toString(); }
}
