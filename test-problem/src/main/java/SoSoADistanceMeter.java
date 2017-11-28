package com.azavea;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

import com.azavea.SourceOrSinkOrAnchor;


public class SoSoADistanceMeter implements NearbyDistanceMeter<SourceOrSinkOrAnchor, SourceOrSinkOrAnchor> {

    @Override
    public double getNearbyDistance(SourceOrSinkOrAnchor origin, SourceOrSinkOrAnchor destination) {
        return origin.distance(destination);
    }

}
