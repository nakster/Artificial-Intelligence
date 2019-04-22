package ie.gmit.sw.ai.nn;

import ie.gmit.sw.ai.nn.activator.*;

//Class contains the training data, expected output and actions 
public class NnFight {

	// variables 
	private NeuralNetwork nn = null;

	//health, weapon, spider strength 
	// the first value it takes is health 
	// the second value it takes is weapon
	// the third value it takes is the strength of the spider 

	// here are all the possible outcomes the player can get into 
	// for example if he has mid health and mid weapon and the spider he is going against has mid strength 
	// the spider is going to attack him
	// another example would be { 2, 1, 0 }
	// here the player has max health and has a mid range weapon
	// where as the spider is weak 
	// so the spider decides to run with the help of nn
	private final double[][] data = { 
			{ 0, 0, 0 },{ 1, 1, 1 },{ 2, 2, 2 },{ 2, 0, 0 }, 
			{ 0, 1, 0 },{ 0, 2, 0 },{ 0, 0, 0 },{ 0, 0, 1 },
			{ 0, 0, 2 },{ 1, 1, 0 },{ 1, 2, 0 },{ 2, 1, 0 },
			{ 2, 2, 0 },{ 2, 2, 1 },{ 1, 0, 1 },{ 1, 0, 2 },
			{ 2, 0, 1 },{ 2, 0, 2 },{ 0, 1, 1 },{ 0, 1, 2 },
			{ 0, 2, 1 },{ 0, 2, 2 },{ 1, 0, 0 }
	};
	//Panic, Attack, Hide
	// each of the outcome is explained in the text file 
	// which is in the resource folder
	private final double[][] expected = {
			{ 0.0, 1.0, 0.0 },{ 0.0, 1.0, 0.0 },{ 0.0, 1.0, 0.0 },{ 0.0, 0.0, 1.0 },
			{ 1.0, 0.0, 0.0 },{ 0.0, 0.0, 1.0 },{ 0.0, 1.0, 0.0 },{ 0.0, 1.0, 0.0 },
			{ 0.0, 1.0, 0.0 },{ 0.0, 0.0, 1.0 },{ 0.0, 0.0, 1.0 },{ 0.0, 0.0, 1.0 },
			{ 0.0, 0.0, 1.0 },{ 1.0, 0.0, 0.0 },{ 1.0, 0.0, 0.0 },{ 0.0, 1.0, 0.0 },
			{ 0.0, 0.0, 1.0 },{ 1.0, 0.0, 0.0 },{ 1.0, 0.0, 0.0 },{ 0.0, 1.0, 0.0 },
			{ 1.0, 0.0, 0.0 },{ 0.0, 1.0, 0.0 },{ 0.0, 0.0, 1.0 }
	};

	// train the net 
	public void train() {
		nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 3, 3, 3);
		Trainator trainer = new BackpropagationTrainer(nn);
		trainer.train(data, expected, 0.2, 10000);
	}

	// this method returns what the neural net thinks the spider should do //O(N)
	public int action(double health, double weapon, double angerLevel) throws Exception{

		//parameters  
		double[] params = {health, weapon, angerLevel};
		double[] result = nn.process(params);

		// get the max result
		// value that is more likely out of the 3 
		int choice = (Utils.getMaxIndex(result) + 1);

		// print out the prediction values 
		for(double val : result){
			System.out.println(val);
		}	

		// return the choice 
		return choice;
	}

}