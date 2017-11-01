## Routing with Open Trip Planner

To set up, follow these steps:

#### Download the Open Street Map extract for your city.

You can do this by using [Mapzen Metro Extracts](https://mapzen.com/data/metro-extracts/)

You need the extract in the OSM PBF format.

### Create OpenTripPlanner graph from the OSM extract

There's a command in the source code to build this graph.

In the sbt console, under the otp project, run e.g.

```
otp > run /home/person/philadelphia_pennsylvania.osm.pbf ./graph-with-students.obj
```

And choose the number next to the graph building command.

## Testing

You can test the graph object by routing two points. Do this by running:

```
otp > run ./graph-with-students.obj [LAT1] [LON1] [LAT2] [LON2]
```


And choose the "test points" command.
