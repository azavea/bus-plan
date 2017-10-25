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

    private List<Node> nodeList = null;
    private List<Bus> busList = null;
    private List<SourceOrSink> entityList = null;
    private HardSoftScore score = null;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "nodeRange")
    public List<Node> getNodeList() { return this.nodeList; }
    public void setNodeList(List<Node> nodeList) { this.nodeList = nodeList; }

    public List<Bus> getBusList() { return this.busList; }
    public void setBusList(List<Bus> busList) { this.busList = busList; }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "entityRange")
    public List<SourceOrSink> getEntityList() { return this.entityList; }
    public void setEntityList(List<SourceOrSink> entityList) { this.entityList = entityList; }

    @PlanningScore
    public HardSoftScore getScore() { return score; }
    public void setScore(HardSoftScore score) { this.score = score; }

    public Plan() {
	int buses = 21;
	int schools = 7;
	int stops = 160;

	List<School> schoolList = new ArrayList<School>();
	this.busList = new ArrayList<Bus>();
	this.entityList = new ArrayList<SourceOrSink>();
	this.nodeList = new ArrayList<Node>();

	for (int i = 0; i < buses; ++i) {
	    Node node = new Node();
	    Bus bus = new Bus(node);
	    nodeList.add(node);
	    busList.add(bus);
	}

	for (int i = 0; i < schools; ++i) {
	    Node node = new Node();
	    School school = new School(node);
	    if (i == 0) {
		school.setNext(school); // XXX ("initialized solution")
	    }
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

    }

}
