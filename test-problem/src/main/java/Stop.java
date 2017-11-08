package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.Student;
import com.example.School;


@PlanningEntity
public class Stop extends SourceOrSink {

    private List<Student> studentList = new ArrayList<Student>();

    public Stop() {}

    public Stop(Node node) { this.node = node; }

    @InverseRelationShadowVariable(sourceVariableName = "stop")
    public List<Student> getStudentList() { return this.studentList; }
    public void setStudentList(List<Student> studentList) { this.studentList = studentList; }

    public String toString() {
	return "METASOURCE" + this.node.toString() + "(" + studentList.size() + ")";
    }
}
