package main.firefighters;

import main.api.CityNode;
import main.api.Firefighter;

public class FirefighterImpl implements Firefighter {

  private int x;
  private int y;
  private int distance;

  public FirefighterImpl(int x, int y) {
    this.x = x;
    this.y = y;
    this.distance = 0;
  }

  @Override
  public CityNode getLocation() {
    return new CityNode(this.x, this.y);
  }

  @Override
  public int distanceTraveled() {
    return this.distance;
  }

  @Override
  public int updateDistanceTraveled() {
    return this.distance;
  }

  @Override
  public int updateDistanceTraveled(int newX, int newY) {
    int newDis = Math.abs(this.x - newX) + Math.abs(this.y - newY);
    this.x = newX;
    this.y = newY;
    this.distance += newDis;
    return this.distance;
  }
}
