public class Component
{
  public int Pin;
  public boolean State = false;
  
  public Component(int pin)
  {
    this.Pin = pin;
  }
  
  public void set(boolean state)
  {
    System_Function.exec("gpio write " + this.Pin + (state ? " 0" : " 1"));//0 pour allumé, 1 pour éteint car les relais sont en mode normalement ouvert
    this.State = state;
  }
  
  public int getPin()
  {
    return this.Pin;
  }
  
  public boolean getState()
  {
      return System_Function.exec_result("gpio read "+this.Pin).contains("0");
  }
}
