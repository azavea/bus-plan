# School Bus Route Optimization

Generate optimal school bus routes for an existing set of students, stops and school locations. Routes are optimized for minimal bus drive time and can be constrained to limit maximum student ride time. This tool uses uses [OpenTripPlanner](https://github.com/opentripplanner/OpenTripPlanner) to generate route costs and [OptaPlanner](https://github.com/kiegroup/optaplanner) as a constraint solver.  

## Getting Started

### Create graphs for OpenTripPlanner routing
Download Open Street Map extract(s) for your city of interest from [Mapzen](https://mapzen.com/data/metro-extracts)

Open sbt console and OpenTripPlanner project from the root directory:

`$ sbt`

`> project otp`

Generate graphs, run e.g.

`> run  ./osm_metro_extract.osm.pbf output_metro_area_graph.obj`

Select the `GenerateRouteGraph` option.

This will create two graphs: one that allows highway access (for buses without any passengers) and one that doesn't (for buses transporting students). 

### Create cost matrix 

Next, create a cost matrix with the travel times/distances (i.e. costs) among all nodes (i.e. garages, bus stops, schools).

Prepare a csv of nodes in the following fields:

* *id*: uuid
* *count*: count of students picked up or dropped off at location
* *X*: latitude
* *Y*: longitude
* *type*: type of node (e.g. one of 'garage', 'stop', or 'school')
* *time*: unix timestamp of five minutes prior to school bell time (value of 0 for all non-school nodes)

Generate cost matrix CSV from nodes:

`run ./cost_matrix_nodes.csv ./graph_withStudents.obj ./graph_withoutStudents.obj ./cost_matrix_output.csv`

Choose the number corresponding to the `GenerateCostMatrix` class.

### Match students to existing bus stops

Next prepare an additional dataset of "student nodes" with the locations of all students in the problem. This dataset should be in the form of a csv with the following fields:

* *id*: uuid
* *grade*: student's grade level
* *X*: latitude
* *Y*: longitude
* *type*: in this case all 'student'
* *time*: unix timestamp of five minutes prior to school bell time (value of 0 for all non-school nodes)
* *stop_id*: the uuid (from main cost matrix nodes dataset) of the stop that the student is currently assigned to

### Optimize bus plan

### Route solver output

### Analyze route



