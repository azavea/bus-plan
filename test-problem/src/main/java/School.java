package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;


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

    @CustomShadowVariable(variableListenerClass = SinkSizeUpdatingVariableListener.class,
                          sources = {@PlanningVariableReference(variableName = "previous"),
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
        int[] capacity = this.getBus().getWeights();
        SourceOrSink previous = null;
        SourceOrSink current = this.getBus().getNext();
        int time = 0;

        if (this.getBus().equals("dummy"))
            return 0;

        while (current != this && current != null) {

            if (current instanceof Stop) { // Stop
                Stop stop = (Stop)current;
                for (Student kid : stop.getStudentList())
                    kids.add(kid);
            }

            if (current instanceof School) { // School
                School school = (School)current;
                List<Student> newKids = new ArrayList<Student>();
                for (Student kid : kids)
                    if (!kid.getSchoolUuid().equals(school.getNode().getUuid()))
                        newKids.add(kid);
                kids = newKids;
            }

            if (previous != null)
                time += previous.getNode().time(current.getNode());
            previous = current;
            current = current.getNext();
        }

        int delivered = 0;

        if (time < 3600*1.5) { // 90 minute limit
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
