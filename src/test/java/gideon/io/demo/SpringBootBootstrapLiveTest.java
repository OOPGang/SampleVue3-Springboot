package gideon.io.demo;
import java.util.*;

import oop.io.demo.pass.Pass;


public class SpringBootBootstrapLiveTest {
    private static final String API_ROOT
    = "http://localhost:8081/api/Pass";

  private Pass createZooPass() {
      Pass pass = new Pass();
      pass.SetPasses("Zoo");
      pass.SetAvailability(12);
      return pass;
  }
  


    
}
