package utils;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class KdeAngleTest {

  @Test
  public void test() {
    Map<Double, Integer> map = new HashMap<>();
    map.put(1d, 1);


    assertFalse(KdeAngle.getDominantDirection(map).isPresent());
  }

}
