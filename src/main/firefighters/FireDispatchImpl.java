package main.firefighters;

import java.util.List;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.exceptions.NoFireFoundException;
import main.impls.FireStation;
import main.api.Building;


import java.util.ArrayList;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;



public class FireDispatchImpl implements FireDispatch {

  private City cityGrid;
  private List<Firefighter> fireFighters;
  private FireStation fireStationLoc;
  int fireFighterIndex;

// expand FireDispatchImpl to take in the fire-station CityNode so we can init fireFights to that location
  public FireDispatchImpl(City city, FireStation fireStation) {
    this.cityGrid = city;
    this.fireStationLoc = fireStation;
    this.fireFighterIndex = 0;
    this.fireFighters = new ArrayList<>();

  }

  @Override
  public void setFirefighters(int numFirefighters) {
    // Init our ArrayList of fireFighters to size numFireFighters
    this.fireFighters = new ArrayList<>(numFirefighters);

    while(numFirefighters != 0) {
      // Push into our ArrayList, every fireFighter will be located at our fireStation at first
      Firefighter fighter = new FirefighterImpl(this.fireStationLoc.getLocation().getX(), this.fireStationLoc.getLocation().getY());
      this.fireFighters.add(fighter);
      numFirefighters--;
    }
  }

  @Override
  public List<Firefighter> getFirefighters() {
    return this.fireFighters;
  }

  @Override
  public void dispatchFirefighers(CityNode... burningBuildings) throws NoFireFoundException {
    // Idea is to loop through our burning CityNodes and send a Firefighter out to it.
    // If we've exhausted all of our current firefighters, we will loop through our ArrayList again,
    // starting with the 0th index fireFighter in our ArrayList

    for (CityNode location : burningBuildings) {
      if(this.fireFighterIndex >= this.fireFighters.size()) {
        this.fireFighterIndex = 0;
      }

      Building currBuilding = this.cityGrid.getBuilding(location);

      // to handle our fireProof Exceptions, if a building already burning or is a firestation, do not extinguish fire.
      // If a building is burning, we'll want to dispatch firefighter and calculate distance and update the X,Y points of Firefighter

      if(currBuilding.isBurning() && !currBuilding.isFireproof()) {
        currBuilding.extinguishFire();
        Firefighter currFireFighter = this.fireFighters.get(this.fireFighterIndex);
        int cityX = location.getX();
        int cityY = location.getY();

        // we'll pass the X,Y points of our burning building to firefighter so we can calc. distance they have to travel
        currFireFighter.updateDistanceTraveled(cityX, cityY);

        this.fireFighterIndex++;

      }
    }
  }
}
