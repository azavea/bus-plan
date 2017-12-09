package com.azavea;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import com.azavea.Node;
import com.azavea.Bus;
import com.azavea.SourceOrSink;


@PlanningEntity
public interface SourceOrSinkOrAnchor {

    @InverseRelationShadowVariable(sourceVariableName = "previous")
    public SourceOrSink getNext();
    public void setNext(SourceOrSink next);

    public Node getNode();
    public void setNode(Node node);

    public Bus getBus();
    public void setBus(Bus bus);

    public int time(SourceOrSinkOrAnchor other);
    public int time(Node other);

    public double distance(SourceOrSinkOrAnchor other);
    public double distance(Node other);

    public int hashCode();
}
