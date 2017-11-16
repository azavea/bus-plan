package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;


public class School extends SourceOrSink {

    public School() {}

    public School(Node node) { this.node = node; }

    public boolean equals(Object other) {
        if (!(other instanceof School))
            return false;
        else
            return this.getNode().equals(((School)other).getNode());
    }

    public int sink() {
        List<Student> kids = new ArrayList<Student>();
        int[] capacity = this.getBus().getWeights();
        SourceOrSink current = this.getBus().getNext();
        double distance = 0.0;
        int time = 0;

        while (current != this && current != null) {

            if (current instanceof Stop) {
                Stop stop = (Stop)current;
                for (Student kid : stop.getStudentList())
                    kids.add(kid);
            }

            // if (current instanceof Stop) { // Stop
            //  Stop stop = (Stop)current;
            //  for (Student kid : stop.getStudentList()) {
            //      if (kid.distance(stop) < walkLimit) {
            //          int[] weights = kid.getWeights();
            //          kids.add(kid);
            //          for (int i = 0; i < 2; ++i)
            //              inFlow[i] += weights[i];
            //      }
            //  }
            // }

            if (current instanceof School) { // School
                School school = (School)current;
                List<Student> newKids = new ArrayList<Student>();
                for (Student kid : kids)
                    if (!kid.getSchool().equals(school))
                        newKids.add(kid);
                kids = newKids;
            }

            // else if (current instanceof School) { // School
            //  School school = (School)current;

            //  // Tally delivered kids
            //  if (distance < bellTime) {
            //      for (Student kid : kids) {
            //          if (kid.getSchool().equals(school)) {
            //              int[] weights = kid.getWeights();
            //              for (int i = 0; i < 2; ++i) {
            //                  outFlow[i] -= weights[i];
            //                  delivered += weights[i];
            //              }
            //          }
            //      }
            //  }
            current = current.getNext();
        }

        int delivered = 0;
        for (Student kid : kids) {
            if (kid.getSchool().equals(this))
                delivered++;
        }

        return delivered;
    }

    public String toString() {
        return "SINK[" + this.node.toString() + "]";
    }
}
