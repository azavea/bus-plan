package sdpBusRoutingCostFunction;

public class RouteCost {
  private int duration = 0;
  private double distance = 0.0;

  public RouteCost(int duration, double distance) {
    this.duration = duration;
    this.distance = distance;
  }

  public int getDuration() { return this.duration; }
  public double getDistance() { return this.distance; }
}
