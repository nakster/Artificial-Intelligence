package ie.gmit.sw.ai.fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;

public class FuzzyFight {
	//O(1)
	public double PlayerHealth(int weapon , int enemy) {
		// load the file 
		FIS fis = FIS.load("resources/fuzzy/fight.fcl", true);
		
		//Display the linguistic variables and terms
		//JFuzzyChart.get().chart(fis);
		fis.setVariable("weapon", weapon); //Apply a value to a variable
		fis.setVariable("enemy", enemy);
		fis.evaluate(); //Execute the fuzzy inference engine
		
		//JFuzzyChart.get().chart(fis.getVariable("damage").getDefuzzifier(),"damage",true);
		// output the result 
		return fis.getVariable("damage").getValue();
	}
}
