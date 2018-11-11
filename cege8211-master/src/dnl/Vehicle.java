/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnl;

import dnl.link.Cell;

/**
 *
 * @author olsso045
 */
public class Vehicle {
    
    private int VehId;
    private Cell Location;
    private double Velocity;
    private double Acceleration;
    
     public int getVehId()
    {
        return this.VehId;
        
    }
     
    public Cell getVehLocation()
  {
      return this.Location;
        
    }
  
   
   public double getVehVelocity()
  {
      return this.Velocity;
        
    }
   
   public double getVehAcceleration()
  {
      return this.Acceleration;
        
    }
}
