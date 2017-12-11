package com.azavea;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;

import com.azavea.Student;
import com.azavea.School;


@PlanningEntity
public class Stop extends SourceOrSink {

    private List<Student> studentList = new ArrayList<Student>();

    public Stop() {}

    public Stop(Node node, String schoolUuid) {
        this.node = node;
        this.schoolUuid = schoolUuid;
    }

    @InverseRelationShadowVariable(sourceVariableName = "stop")
    public List<Student> getStudentList() { return this.studentList; }
    public void setStudentList(List<Student> studentList) { this.studentList = studentList; }

    public String toString() {
        return "METASOURCE[" + this.node.toString() + "(" + studentList.size() + ")]";
    }
}
