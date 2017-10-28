package com.example;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;


@PlanningSolution
public class Plan implements Serializable {

    private List<Bus> busList = null;
    private List<Node> nodeList = null;
    private List<School> schoolList = null;
    private List<SourceOrSink> entityList = null;
    private HardSoftScore score = null;
    private int weight = 0;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "busRange")
    public List<Bus> getBusList() { return this.busList; }
    public void setBusList(List<Bus> busList) { this.busList = busList; }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "nodeRange")
    public List<Node> getNodeList() { return this.nodeList; }
    public void setNodeList(List<Node> nodeList) { this.nodeList = nodeList; }

    public List<School> getSchoolList() { return this.schoolList; }
    public void setSchoolList(List<School> schoolList) { this.schoolList = schoolList; }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "entityRange")
    public List<SourceOrSink> getEntityList() { return this.entityList; }
    public void setEntityList(List<SourceOrSink> entityList) { this.entityList = entityList; }

    @PlanningScore
    public HardSoftScore getScore() { return score; }
    public void setScore(HardSoftScore score) { this.score = score; }

    public int getWeight() { return this.weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public void display() {
	System.out.println("      PREV ←       THIS →       NEXT        BUS");
	System.out.println("===============================================");
	for (SourceOrSink entity : entityList) {
	    System.out.format("%10s ← %10s → %10s %10s\n",
			      entity.getPrevious(),
			      entity,
			      entity.getNext(),
			      entity.getBus());
	}

	System.out.println("\n       BUS →       NEXT");
	System.out.println("=========================");
	for (Bus bus : busList) {
	    System.out.format("%10s → %10s\n", bus, bus.getNext());
	}
    }

    public Plan() {
	int buses = 21;
	int schools = 7;
	int stops = 160;

	this.busList = new ArrayList<Bus>();
	this.entityList = new ArrayList<SourceOrSink>();
	this.nodeList = new ArrayList<Node>();
	this.schoolList = new ArrayList<School>();

	for (int i = 0; i < buses; ++i) {
	    Node node = new Node();
	    Bus bus = new Bus(node);
	    nodeList.add(node);
	    busList.add(bus);
	}

	for (int i = 0; i < schools; ++i) {
	    Node node = new Node();
	    School school = new School(node);

	    nodeList.add(node);
	    schoolList.add(school);
	    entityList.add(school);
	}

	for (int i = 0; i < stops; ++i) {
	    Node node = new Node();
	    Stop stop = new Stop(node, schoolList.get(i % schoolList.size()));
	    nodeList.add(node);
	    entityList.add(stop);
	}

	// Calculate liability
	for (Node node : nodeList) {
	    for (int w : node.getWeights()) {
		weight += w;
	    }
	}

    }

}
