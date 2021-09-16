package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  // TODO: add more unit tests of your own

  @Test
  public void testIntOperations(){
    MathBot mathDude = new MathBot();
    double addOutput = mathDude.add(20, 30);
    assertEquals(50, addOutput, 0.01);
    double subOutput = mathDude.subtract(20, 30);
    assertEquals(-10, subOutput, 0.01);
  }

  @Test
  public void testDoubleOperations(){
    MathBot mathDude = new MathBot();
    double addOutput = mathDude.add(1.0, 0.33);
    assertEquals(1.33, addOutput, 0.01);
    double subOutput = mathDude.subtract(1, 0.33);
    assertEquals(0.67, subOutput, 0.01);
  }
}
