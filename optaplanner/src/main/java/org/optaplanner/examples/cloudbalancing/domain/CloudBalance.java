/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cloudbalancing.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class CloudBalance implements Serializable {

    private List<CloudComputer> computerList = new ArrayList();

    private List<CloudProcess> processList = new ArrayList();

    private HardSoftScore score;

    public CloudBalance() {
    }

    public CloudBalance(int cs, int ps) {
	for (long i = 0; i < cs; ++i) {
	    computerList.add(new CloudComputer(i, 1000, 2048, 1000, 50));
	}
	for (long i = cs; i < (cs+ps); ++i) {
	    int cpu;

	    if (i % 3 == 0)
		cpu = 163;
	    else if (i % 3 == 1)
		cpu = 167;
	    else
		cpu = 173;

	    processList.add(new CloudProcess(i, cpu, 2048 / (int)((i%4)+1), 10));
	}
    }

    public CloudBalance(long id, List<CloudComputer> computerList, List<CloudProcess> processList) {
        this.computerList = computerList;
        this.processList = processList;
    }

    @ValueRangeProvider(id = "computerRange")
    @ProblemFactCollectionProperty
    public List<CloudComputer> getComputerList() {
        return computerList;
    }

    public void setComputerList(List<CloudComputer> computerList) {
        this.computerList = computerList;
    }

    @PlanningEntityCollectionProperty
    public List<CloudProcess> getProcessList() {
        return processList;
    }

    public void setProcessList(List<CloudProcess> processList) {
        this.processList = processList;
    }

    public Collection<? extends Object> getProblemFacts() {
	List<Object> facts = new ArrayList<Object>();
	// nothing to add because the only facts are already added automatically
	// by planner
	return facts;
    }
    
    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
