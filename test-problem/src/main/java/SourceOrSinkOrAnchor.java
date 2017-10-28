package com.example;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import com.example.Node;
import com.example.Bus;
import com.example.SourceOrSink;


@PlanningEntity
public interface SourceOrSinkOrAnchor {

    @InverseRelationShadowVariable(sourceVariableName = "previous")
    public SourceOrSink getNext();
    public void setNext(SourceOrSink next);

    public Node getNode();
    public void setNode(Node node);

    public Bus getBus();
    public void setBus(Bus bus);
}
