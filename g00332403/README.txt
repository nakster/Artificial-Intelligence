Name:		Naqi Ahmad 
Student ID:     G00332403 
Course:		Software Development L8
Module:		Artificial Intelligence

------------ Description --------------------

Controlling Game Characters with a Neural Network and Fuzzy Logic

We were required to create an AI controlled maze game containing the set of features.
The objective of the game is to escape from a maze and either avoid or fight off the 
enemy characters that move around in the game environment. 

-------------- Threads ---------------------

The player and enemy sprites execute in separate threads. I have the player and the 
Spider running on separate threads. Searches are preformed in threads by each node individually.

------------ Fuzzy Logic --------------------

In the resource folder I have defined the sets and linguistic variables for my fuzzy logic.
I use Fuzzy Logic to calculate the damage when the spider and player Combats. The players
health is determined by a fuzzy logic classifier that will return damage done by the spider.
Whenever the player goes near the spider he loses health depending on what type of
spider it is. The spider checks for the player using the Heuristic searches. When the spider
finds the player he attacks him and causes the player to lose the health. 

I am calling the fuzzy logic in FuzzyFight.java in the package ie.gmit.sw.ai.fuzzy. This is
then used in the NnFuzzyFight in ie.gmit.sw.ai.fight package.

All of the spiders are using the fuzzy logic to calculate the new health for the player.

--------------linguistic variables-------------------
-------------------Input-----------------------------
// depending on the weapon of spider the greater the attack would be 
FUZZIFY weapon
  TERM harmless := (0, 1) (20, 1) (60, 0);
  TERM dangerous := trian 20 50 80;	 
  TERM lethal := (40, 0) (80, 1) (100, 1);	
END_FUZZIFY
	
// if the enemy strenth is between 0 and 40 its going to be weak 
// if the its between 30 and 70 its going to be in the strong range 
// if the enemy is between 60 and 100 its going to be formiddable
// the stronger the enemy the greater the attack is going to be 
FUZZIFY enemy
 TERM weak := trian 0 20 40;	 
 TERM strong := trian 30 50 70;	 
 TERM formidable := trian 60 80 100;	
END_FUZZIFY

-------------------Output-----------------------------
//here i calculate the damage which is going to be ouput to determine the new health
// which will be used to calculate the new health of the player 
DEFUZZIFY damage
 TERM small := (19,0) (20, 1) (21, 0);	
 TERM partial := (49,0) (50, 1) (51, 0);	
 TERM great := (79,0) (80, 1) (81, 0);	
 METHOD : COG;	// improves the control performance of a fuzzy logic controller
 DEFAULT := 0;	
END_DEFUZZIFY

-------------------Rules-----------------------------
// RULE 1 is basically if the spider weapon is dangerous or enemy is strong then its going to damage players health partially 	
// RULE 2 if the spider weapon is lethal or enemy is formidable then the risk is also going to be partial
// RULE 3 if the spider weapon is harmless or enemy is weak then its going to be low damage to the player
// RULE 4 if the spider weapon is dangerous and enemy is strong then its going to damage players health it partially 
// RULE 5 if the spider weapon is lethal and enemy is formidable then damage to players health is going to be great
// RULE 6 if the spider weapon is harmless and enemy is weak then its going to be low damage to the players health
RULE 1 : IF weapon IS dangerous OR enemy IS strong THEN damage IS partial;
RULE 2 : IF weapon IS lethal OR enemy IS formidable THEN damage IS partial;
RULE 3 : IF weapon IS harmless OR enemy IS weak THEN damage IS small;
RULE 4 : IF weapon IS dangerous AND enemy IS strong THEN damage IS partial;
RULE 5 : IF weapon IS lethal AND enemy IS formidable THEN damage IS great;
RULE 6 : IF weapon IS harmless AND enemy IS weak THEN damage IS small;

------------ Neural Network --------------------

I created a very extensive data set for the spider to help the decision making either to panic
attack or hide from the player depending on what players health, weapon and strength of the spider are.

I am using the Sigmoid Network topology. The reason I chose the sigmoid was because it is easily differentiable
with respect to the network parameters. This type of Neural network is also very easy to train.
The Neural net is trained at the start of the game in the Game Runner and passed to the maze and from there
I use it to pass it to the NnFuzzyFight class. This way i don't have to train the neural network every time
spider makes a decision. This is done to insure that the game is not slow.

-------------------Train-----------------------------
I have included the data and expected data in a txt file in the resource folder explaining 
what each line does and what outcome should the neural network give.

nn = new NeuralNetwork(Activator.ActivationFunction.Sigmoid, 3, 3, 3);
Trainator trainer = new BackpropagationTrainer(nn);
trainer.train(data, expected, 0.2, 10000);

-------------------Outcome-----------------------------

The out come will be one of these [0,0,0] = Panic , Attack, Hide 

The spider uses the Neural Network to decide whether to attack or panic or hide from the player. 
This is determined by the neural network. The Neural Network returns an array and we pick the 
highest value out of the 3.

outcome = nnfight.action(health, weapon , spiderStrength);

When the player picks the one of the weapons his current weapon is set to that. so when the spider uses 
the Neural Network it uses the players current weapon and players current health and also the strength
of the spider. 

Weapon Type
Sword 			
Bomb			
H-Bomb             

------------ Heuristic Search --------------------

I use 5 different spiders to run different Heuristic searches. Spider nodes implement their own traverses 
to locate the player and to move either toward of away from the player. I am using these 3 searches to find
the player.The black, blue, brown, orange and yellow spiders are looking for the player. 

I am using these searches to find the player.

AStarTraversator
BasicHillClimbingTraversator
DepthLimitedDFSTraversator

when the player is 10 nodes away from the spider the spider will start following the player and attack him. 
The spider will also run the neural network to see if it should really attack the player. 
Not all the searches are perfect. I use AStarTraversator which is highly accurate works perfectly. 

------------ Everything together -------------------- 

- When the Spider is near the player(10 nodes away) the spider will run the neural network to decide what to do. 
- When the Spider decides to attack the player the fuzzy logic is used to give a new health to the player.
- The game is won when the player finds the door which is the colour pink on the map. 
- when the spiders find the player they try to kill him, when they do the game ends 
- Also the player can increase its health by picking up items 
- The run method is being used to execute the methods 

Spiders Damage 

spider      weapon, enemy = damage  Type of Traverse?                 Neural Network(panic, attack, hide)
Black spider  70       80   =  65     AStarTraversator                  Determines What to do when player is near
Blue spider   65       65   =  60.1   BasicHillClimbingTraversator      Determines What to do when player is near
Brown spider  55       60   =  44.1   DepthLimitedDFSTraversator        Determines What to do when player is near
Green spider  45       50   =  38.6   Will Move Randomly                Moves Randomly
Grey spider   40       40   =  36.3   Will Move Randomly                Moves Randomly
Orange spider 70       80   =  65     DepthLimitedDFSTraversator        Determines What to do when player is near
Red spider    30       20   =  30     Will Move Randomly                Moves Randomly
Yellow spider 20       20   =  20     BasicHillClimbingTraversator      Determines What to do when player is near


Weapon - When the player picks up the weapons the Health of the player also increase
The player can also pick up the weapons. 

Weapon Type -   Health
Sword           +10
Bomb            +20
H-Bomb          +30
Help            +50

------------------- EXTRAS -----------------------
- I made the maze into node[][] maze. 
- The player can pickup the items to gain more health. 
- I coloured all the spiders when z is pressed you can see all the coloured spiders 
- only displaying 11 spiders who search for the player 
- The player can also exit the game by finding the door which is pink.
- Redesign AStar traverser for it to be compatible with the maze and other searches.
- if the player loses health and is below zero the game automatically ends.
- Threads are designed to avoid deadlock 
- The big O Notation added 