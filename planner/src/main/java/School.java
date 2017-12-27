package com.azavea;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import com.azavea.Plan;


@PlanningEntity
public class School extends SourceOrSink {

    private int sinkSize = 0;

    public School() {}

    public School(Node node) { this.node = node; }

    public boolean equals(Object other) {
        if (!(other instanceof School))
            return false;
        else {
            String uuid1 = this.getNode().getUuid();
            String uuid2 = ((School)other).getNode().getUuid();
            return uuid1.equals(uuid2);
        }
    }

    @Override public String getSchoolUuid() { return this.getNode().getUuid(); }
    @Override public void setSchoolUuid(String schoolUuid) { }

    @CustomShadowVariable(variableListenerClass = SinkSizeUpdatingVariableListener.class,
                          sources = {@PlanningVariableReference(entityClass = Stop.class, variableName = "studentList"),
                                     @PlanningVariableReference(variableName = "previous"),
                                     @PlanningVariableReference(variableName = "next"),
                                     @PlanningVariableReference(variableName = "bus")})
    public Integer getSinkSize() {
        return new Integer(this.sinkSize);
    }

    public void setSinkSize(Integer sinkSize) {
        this.sinkSize = sinkSize.intValue();
    }

    public int _sinkSize() {
        List<Student> kids = new ArrayList<Student>();
        SourceOrSink previous = null;
        SourceOrSink current = this.getBus().getNext();
        int time = 0;
        boolean overFull = false;
        boolean schoolSeen = false;

        if (this.getBus().equals("dummy"))
            return 0;

        while (current != this && current != null) {

            if (current instanceof Stop) { // Stop
                Stop stop = (Stop)current;
                time += Plan.SECONDS_PER_STOP;
                boolean firstStudent = true;
                for (Student kid : stop.getStudentList()) {
                    if(firstStudent) {
                      firstStudent = false;
                    } else {
                      time += Plan.SECONDS_PER_STUDENT_AT_STOP;
                    }

                    kids.add(kid);
                }
            }

            if (kids.size() > Plan.STUDENTS_PER_BUS) {
                overFull = true;
            }

            if (current instanceof School) { // School
                School school = (School)current;
                List<Student> newKids = new ArrayList<Student>();
                for (Student kid : kids) {
                    if (!kid.getSchoolUuid().equals(school.getNode().getUuid())) { // kids not delivered
                        newKids.add(kid);
                    }
                    else { // kids delivered
                        time += Plan.SECONDS_PER_STUDENT_AT_SCHOOL;
                    }
                }
                kids = newKids;
                schoolSeen = true;
            }

            if (previous != null)
                time += previous.getNode().time(current.getNode());
            previous = current;
            current = current.getNext();
        }

        int delivered = 0;

        time = (int)((1.0 + ((Plan.SIGMA_OVER_MU-1.0)*Plan.SIGMAS))*time);
        if ((time < 60*Plan.MAX_RIDE_MINUTES) &&
            !overFull &&
            !(Plan.NO_TIERING && schoolSeen)) {
            for (Student kid : kids) {
                if (kid.getSchoolUuid().equals(this.getNode().getUuid()))
                    delivered++;
            }
        }

        return delivered;
    }

    public String toString() {
        return "SINK[" + this.node.toString() + "]";
    }
}
