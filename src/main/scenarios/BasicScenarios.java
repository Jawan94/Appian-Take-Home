package main.scenarios;

import main.api.*;
import main.api.exceptions.FireproofBuildingException;
import main.api.exceptions.NoFireFoundException;
import main.impls.CityImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BasicScenarios {
  @Test
  public void singleFire() throws FireproofBuildingException, NoFireFoundException {
    City basicCity = new CityImpl(5, 5, new CityNode(0, 0));

    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode fireNode = new CityNode(0, 1);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighers(fireNode);
    Assert.assertFalse(basicCity.getBuilding(fireNode).isBurning());
  }


  @Test
  public void singleFireDistanceTraveledDiagonal() throws FireproofBuildingException, NoFireFoundException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    // Set fire on opposite corner from Fire Station
    CityNode fireNode = new CityNode(1, 1);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighers(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(2, firefighter.distanceTraveled());
    Assert.assertEquals(fireNode, firefighter.getLocation());
  }

  @Test
  public void singleFireDistanceTraveledAdjacent() throws FireproofBuildingException, NoFireFoundException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    // Set fire on adjacent X position from Fire Station
    CityNode fireNode = new CityNode(1, 0);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighers(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(1, firefighter.distanceTraveled());
    Assert.assertEquals(fireNode, firefighter.getLocation());
  }

  @Test
  public void simpleDoubleFire() throws FireproofBuildingException, NoFireFoundException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
        new CityNode(0, 1),
        new CityNode(1, 1)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighers(fireNodes);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(2, firefighter.distanceTraveled());
    Assert.assertEquals(fireNodes[1], firefighter.getLocation());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[1]).isBurning());
  }

  @Test
  public void doubleFirefighterDoubleFire() throws FireproofBuildingException, NoFireFoundException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
        new CityNode(0, 1),
        new CityNode(1, 0)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(2);
    fireDispatch.dispatchFirefighers(fireNodes);

    List<Firefighter> firefighters = fireDispatch.getFirefighters();
    int totalDistanceTraveled = 0;
    boolean firefighterPresentAtFireOne = false;
    boolean firefighterPresentAtFireTwo = false;
    for (Firefighter firefighter : firefighters) {
      totalDistanceTraveled += firefighter.distanceTraveled();

      if (firefighter.getLocation().equals(fireNodes[0])) {
        firefighterPresentAtFireOne = true;
      }
      if (firefighter.getLocation().equals(fireNodes[1])) {
        firefighterPresentAtFireTwo = true;
      }
    }

    Assert.assertEquals(2, totalDistanceTraveled);
    Assert.assertTrue(firefighterPresentAtFireOne);
    Assert.assertTrue(firefighterPresentAtFireTwo);
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[1]).isBurning());
  }

  // Test below will confirm that a single fireFighter will retain their new location on grid
  // which will be the last building grid they were dispatched to inorder to extinguish fire.

  @Test
  public void tripleFirefighterSingleFire() throws FireproofBuildingException, NoFireFoundException {
    CityNode fireStation = new CityNode(0, 0);

    City basicCity = new CityImpl(4, 4, fireStation);
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
            new CityNode(0, 1),
            new CityNode(1, 0),
            new CityNode(3, 3)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighers(fireNodes);

    List<Firefighter> firefighters = fireDispatch.getFirefighters();
    int totalDistanceTraveled = 0;
    boolean firefighterPresentAtFireOne = false;
    boolean firefighterPresentAtFireTwo = false;
    boolean firefighterPresentAtFireThree = false;

    for (Firefighter firefighter : firefighters) {
      totalDistanceTraveled += firefighter.distanceTraveled();

      if (firefighter.getLocation().equals(fireNodes[0])) {
        firefighterPresentAtFireOne = true;
      }
      if (firefighter.getLocation().equals(fireNodes[1])) {
        firefighterPresentAtFireTwo = true;
      }
      if(firefighter.getLocation().equals(fireNodes[2])) {
        firefighterPresentAtFireThree = true;
      }
    }

    Assert.assertEquals(8, totalDistanceTraveled);
    Assert.assertFalse(firefighterPresentAtFireOne);
    Assert.assertFalse(firefighterPresentAtFireTwo);
    Assert.assertTrue(firefighterPresentAtFireThree);
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[1]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[2]).isBurning());
    Assert.assertTrue(basicCity.getBuilding(fireStation).isFireproof());


  }

  // test will ensure that firefighters are dispatched iteratively to fires, retaining total distance traveled.
  @Test
  public void DoubleFirefighterAllBuildingsOnFire() throws FireproofBuildingException, NoFireFoundException {
    // we will set fire-station to a non-(0,0) location
    CityNode fireStation = new CityNode(2, 1);
    City basicCity = new CityImpl(3, 3, fireStation);
    FireDispatch fireDispatch = basicCity.getFireDispatch();

  // testing fire at all buildings
    CityNode[] fireNodes = {
            new CityNode(0, 1),
            new CityNode(0, 2),
            new CityNode(1, 0),
            new CityNode(1, 1),
            new CityNode(1, 2),
            new CityNode(2, 0),
            new CityNode(0, 0),
            new CityNode(2, 2)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(2);
    fireDispatch.dispatchFirefighers(fireNodes);

    List<Firefighter> firefighters = fireDispatch.getFirefighters();
    int totalDistanceTraveled = 0;
    boolean firefighterPresentAtLastFire = false;
    boolean firefighterPresentAtSecondLastFire = false;
    boolean firefighterPresentAtThirdLastFire = false;

    for (Firefighter firefighter : firefighters) {
      totalDistanceTraveled += firefighter.distanceTraveled();

      if (firefighter.getLocation().equals(fireNodes[6])) {
        firefighterPresentAtSecondLastFire = true;
      }
      if (firefighter.getLocation().equals(fireNodes[7])) {
        firefighterPresentAtLastFire = true;
      }
      if (firefighter.getLocation().equals(fireNodes[5])) {
        firefighterPresentAtThirdLastFire = true;
      }

    }

    Assert.assertEquals(18, totalDistanceTraveled);
    Assert.assertTrue(firefighterPresentAtLastFire);
    Assert.assertTrue(firefighterPresentAtSecondLastFire);
    Assert.assertFalse(firefighterPresentAtThirdLastFire);
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[1]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[2]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[3]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[4]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[5]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[6]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[7]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isFireproof());
    Assert.assertTrue(basicCity.getBuilding(fireStation).isFireproof());

  }

}
