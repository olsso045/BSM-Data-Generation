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
    private int RandomizedId;
    
    public Vehicle(int VehId, int RandomizedId)
    {
        this.VehId = VehId;
        this.Velocity = 0;
        this.Acceleration = 0;
        this.RandomizedId = RandomizedId;
    }
    
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
   
   public int getVehRandomizedId()
  {
      return this.RandomizedId;
        
    }
   
   public void setVehicleLocation(Cell loc) {
   
       this.Location = loc;
   }
   
   public void setVehicleVelocity(double vel) {
   
       this.Velocity = vel;
   }
   
   public void setVehicleAcceleration(double acc) {
   
       this.Acceleration = acc;
   }
}
