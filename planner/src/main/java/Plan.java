package com.azavea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.azavea.Node;


@PlanningSolution
public class Plan implements Serializable {
    public static int COST_PER_BUS;
    public static int MAX_RIDE_MINUTES;
    public static int SECONDS_PER_STUDENT;
    public static int STUDENTS_PER_BUS;
    public static int STUDENTS_PER_STOP;
    public static double SIGMA_OVER_MU;
    public static double SIGMAS;
    public static boolean NO_TIERING;

    private List<Bus> busList = null;
    private List<Node> nodeList = null;
    private List<School> schoolList = null;
    private List<Stop> stopList = null;
    private List<Student> studentList = null;

    private HardSoftLongScore score = null;

    public void render(String routeCsv, String assignmentCsv) throws IOException {
        BufferedWriter routeWriter = new BufferedWriter(new FileWriter(routeCsv));
        BufferedWriter assignmentWriter = new BufferedWriter(new FileWriter(assignmentCsv));
        int i = 0;

        for (Bus bus : busList) {
            SourceOrSinkOrAnchor current = bus;

            if (bus.equals("dummy")) continue;

            routeWriter.write("" + i);
            while (current != null) {
                routeWriter.write("," + current.getNode().getUuid());
                current = current.getNext();
                if (current instanceof Stop) {
                    assignmentWriter.write("" + i + "," + current.getNode().getUuid());
                    for (Student student : ((Stop)current).getStudentList()) {
                        assignmentWriter.write("," + student.getNode().getUuid());
                    }
                    assignmentWriter.write("\n");
                }
            }
            routeWriter.write("\n");
            i++;
        }

        routeWriter.close();
        assignmentWriter.close();
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "busRange")
    public List<Bus> getBusList() { return this.busList; }
    public void setBusList(List<Bus> busList) { this.busList = busList; }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "nodeRange")
    public List<Node> getNodeList() { return this.nodeList; }
    public void setNodeList(List<Node> nodeList) { this.nodeList = nodeList; }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "schoolRange")
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
    public HardSoftLongScore getScore() { return score; }
    public void setScore(HardSoftLongScore score) { this.score = score; }

    public void display() {
        // System.out.println("      PREV ←       THIS →       NEXT        BUS");
        // System.out.println("===============================================");
        // for (SourceOrSink entity : entityList) {
        //     System.out.format("%10s ← %10s → %10s %10s\n",
        //                    entity.getPrevious(),
        //                    entity,
        //                    entity.getNext(),
        //                    entity.getBus());
        // }

        System.out.println("\n       BUS →       NEXT");
        System.out.println("=========================");
        for (Bus bus : busList) {
            System.out.format("%10s → %10s\n", bus, bus.getNext());
        }

        // for (Student student : studentList) {
        //     System.out.println("--- " + student + " " + student.getStop());
        // }
    }

    public Plan() {
        this.busList = new ArrayList<Bus>();
        this.nodeList = new ArrayList<Node>();
        this.schoolList = new ArrayList<School>();
        this.stopList = new ArrayList<Stop>();
        this.studentList = new ArrayList<Student>();
    }

    public Plan(String csvCostMatrixFile,
                String csvStudentFile,
                String csvStops,
                String csvGarages) throws IOException {
        HashSet<String> garageUuids = new HashSet<String>();
        HashSet<String> schoolUuids = new HashSet<String>();
        HashSet<String> stopUuids = new HashSet<String>();
        HashMap<String, Integer> timeMatrix = new HashMap<String, Integer>();
        HashMap<String, Double> distanceMatrix = new HashMap<String, Double>();
        HashMap<String, HashSet<String>> eligibilityMatrix = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> garageCountMatrix = null;

        this.busList = new ArrayList<Bus>();
        this.nodeList = new ArrayList<Node>();
        this.schoolList = new ArrayList<School>();
        this.stopList = new ArrayList<Stop>();
        this.studentList = new ArrayList<Student>();

        // Build matrices, remember UUIDs
        Reader in = new FileReader(csvCostMatrixFile);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
        for (CSVRecord record : records) {
            String originId = record.get("origin_id");
            String destinationId = record.get("destination_id");
            String key = originId + destinationId;
            int time = Integer.parseInt(record.get("time"));
            double distance = Double.parseDouble(record.get("distance"));

            if (originId.startsWith("garage_"))
                garageUuids.add(originId);
            else if (originId.startsWith("stop_"))
                stopUuids.add(originId);
            else if (originId.startsWith("school_"))
                schoolUuids.add(originId);

            if (destinationId.startsWith("garage_"))
                garageUuids.add(destinationId);
            else if (destinationId.startsWith("stop_"))
                stopUuids.add(destinationId);
            else if (destinationId.startsWith("school_"))
                schoolUuids.add(destinationId);

            timeMatrix.put(key, time);
            distanceMatrix.put(key, distance);
        }

        // Dummy Bus
        Node dummyNode = new Node("dummy");
        Bus dummyBus = new Bus(dummyNode, 0);
        nodeList.add(dummyNode);
        busList.add(dummyBus);

        // Read Buses-per-garage information
        int maximumTotalBuses = 0;
        if (csvGarages != null) {
            in = new FileReader(csvGarages);
            records = CSVFormat.EXCEL.withHeader().parse(in);
            garageCountMatrix = new HashMap<String, Integer>();
            for (CSVRecord record : records) {
                String uuid = record.get("uuid");
                int maximumBuses = Integer.parseInt(record.get("maximum"));
                garageCountMatrix.put(uuid, maximumBuses);
            }
        }

        // Buses
        for (String uuid : garageUuids) {
            int maximumBuses = -1;
            if (garageCountMatrix == null)
                maximumBuses = 1;
            else if (garageCountMatrix != null && garageCountMatrix.containsKey(uuid)) {
                maximumBuses = garageCountMatrix.get(uuid);
            }
            else if (garageCountMatrix != null)
                maximumBuses = 0;
            maximumTotalBuses += maximumBuses;

            Node node = new Node(uuid);
            nodeList.add(node);
            for (int i = 0; i < maximumBuses; ++i) {
                Bus bus = new Bus(node, i);
                busList.add(bus);
            }
        }

        // Schools
        for (String uuid : schoolUuids) {
            Node node = new Node(uuid);
            nodeList.add(node);
            for (int i = 0; i < maximumTotalBuses; ++i) {
                School school = new School(node);
                schoolList.add(school);
            }
        }

        // Stops
        for (String uuid : stopUuids) {
            Node node = new Node(uuid);
            nodeList.add(node);
            for (String schoolUuid : schoolUuids) {
                Stop stop = new Stop(node, schoolUuid);
                stopList.add(stop);
            }
        }

        // Read eligibility information
        if (csvStops != null) {
            in = new FileReader(csvStops);
            BufferedReader br = new BufferedReader(in);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(",");
                String student = splitLine[0];
                if (splitLine.length > 1) {
                    HashSet<String> stops = new HashSet<String>(Arrays.asList(Arrays.copyOfRange(splitLine, 1, splitLine.length-1)));
                    eligibilityMatrix.put(student, stops);
                }
                else {
                    eligibilityMatrix.put(student, new HashSet<String>());
                }
            }
        }

        // Read student data
        in = new FileReader(csvStudentFile);
        records = CSVFormat.EXCEL.withHeader().parse(in);
        for (CSVRecord record : records) {
            String firstName = record.get("Student.First.Name");
            String lastName = record.get("Student.Last.Name");
            String studentUuid = "student_" + record.get("compass_id");
            String schoolUuid = "school_" + record.get("School.Code");
            String stopUuid = "stop_" + record.get("stop_id_cm_reference");
            Node node = new Node(studentUuid);
            Stop stop = null;

            // Initial solution
            for (Stop _stop : stopList) {
                if (_stop.getNode().getUuid().equals(stopUuid)) {
                    stop = _stop;
                    break;
                }
            }
            if (eligibilityMatrix.get(studentUuid) == null)
                eligibilityMatrix.put(studentUuid, new HashSet<String>());
            eligibilityMatrix.get(studentUuid).add(stop.getNode().getUuid());
            Student student = new Student(node, firstName, lastName, schoolUuid);
            studentList.add(student);
            student.setStop(stop);              // In lieu of construction heuristic
            stop.getStudentList().add(student); // In lieu of construction heuristic
        }

        // Initial solution
        SourceOrSinkOrAnchor previous = dummyBus;
        for (SourceOrSink current : stopList) {
            current.setPrevious(previous); // In lieu of construction heuristic
            current.setBus(dummyBus);      // In lieu of construction heuristic
            previous.setNext(current);     // In lieu of construction heuristic
            previous = current;
        }
        for (SourceOrSink current : schoolList) {
            current.setPrevious(previous); // In lieu of construction heuristic
            current.setBus(dummyBus);      // In lieu of construction heuristic
            previous.setNext(current);     // In lieu of construction heuristic
            previous = current;
        }

        // Register cost matrices
        Node.setTimeMatrix(timeMatrix);
        Node.setDistanceMatrix(distanceMatrix);
        Student.setEligibilityMatrix(eligibilityMatrix);
    }
}
