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

  @Test
  public void testNesting(){
    MathBot mathDude = new MathBot();
    double result = mathDude.add(105, mathDude.subtract(100, 5));
    assertEquals(result, 200, 0.01);
    result = mathDude.subtract(105, mathDude.add(100, 5));
    assertEquals(result, 0, 0.01);
  }

  @Test
  public void testMultiplicationAndDivision(){
    MathBot mathDude = new MathBot();
    double result = mathDude.add(mathDude.add(20, 20), mathDude.add(20, 20));
    assertEquals(result, 4 * 20, 0.01);
    result = mathDude.subtract(mathDude.add(25, 5), mathDude.add(5, 5));
    assertEquals(result, 80/4, 0.01);
  }
}
