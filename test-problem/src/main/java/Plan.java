package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;


@PlanningSolution
public class Plan implements Serializable {

    private List<Bus> busList = null;
    private List<Node> nodeList = null;
    private List<School> schoolList = null;
    private List<SourceOrSink> entityList = null;
    private List<Stop> stopList = null;
    private List<Student> studentList = null;

    private HardSoftScore score = null;
    private int weight = 0;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "entityRange")
    public List<SourceOrSink> getEntityList() { return this.entityList; }
    public void setEntityList(List<SourceOrSink> entityList) { this.entityList = entityList; }

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
    @ValueRangeProvider(id = "stopRange")
    public List<Stop> getStopList() { return this.stopList; }
    public void setStopList(List<Stop> stopList) { this.stopList = stopList; }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "studentRange")
    public List<Student> getStudentList() { return this.studentList; }
    public void setStudentList(List<Student> studentList) { this.studentList = studentList; }

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
	this(1);
    }

    public Plan(int factor) {
	int buses = factor * 19;
	int schools = factor * 2;
	int students = factor * buses * 25;
	int stops = factor * 300;
	SourceOrSink next = null;

	this.busList = new ArrayList<Bus>();
	this.entityList = new ArrayList<SourceOrSink>();
	this.nodeList = new ArrayList<Node>();
	this.schoolList = new ArrayList<School>();
	this.stopList = new ArrayList<Stop>();
	this.studentList = new ArrayList<Student>();

	// Random buses
	for (int i = 0; i < buses; ++i) {
	    Node node = new Node();
	    Bus bus = new Bus(node);
	    nodeList.add(node);
	    busList.add(bus);
	}

	// Random schools
	for (int i = 0; i < schools; ++i) {
	    Node node = new Node();
	    nodeList.add(node);
	    for (int j = 0; j < buses; ++j) {
		School school = new School(node);
		schoolList.add(school);
		entityList.add(school);

		// Initial
		if (next != null) next.setPrevious(school);
		school.setNext(next);
		school.setBus(busList.get(0));
		next = school;
	    }
	}

	// Random stops
	for (int i = 0; i < stops; ++i) {
	    Node node = new Node();
	    Stop stop = new Stop(node);
	    nodeList.add(node);
	    entityList.add(stop);
	    stopList.add(stop);

	    // Initial
	    if (next != null) next.setPrevious(stop);
	    stop.setNext(next);
	    stop.setBus(busList.get(0));
	    next = stop;
	}

	// Random students
	for (int i = 0; i < students; ++i) {
	    Node node = new Node();
	    Student student = new Student(node, schoolList.get(i % schoolList.size()));
	    nodeList.add(node);
	    studentList.add(student);

	    // Initial
	    student.setStop((Stop)next);

	    weight += 1;
	}

	// Initial
	next.setPrevious(busList.get(0));
	busList.get(0).setNext(next);
	((Stop)next).setStudentList(studentList);
    }
}
