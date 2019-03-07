# reinforcement_learning

In this project, Reinforcement learning is employed to for an agent to learn and adapt “promising paths” in robot-style grid world. 
The grid world is a 3*3 grid with pickup and drop off rooms. Pickup rooms have blocks and drop off rooms are where the robot needs to drop
them. Once all the blocks are dropped in dropoff rooms, termination state is reached. Using Q Learning and SARSA, the agent carries out
this task by learning the right paths to reach pickup and dropoff rooms when left without any information initially in this unknown
environment.

Different experiments are conducted using different parameters and policies in order to interpret the experimental results. 
Developed visualization techniques that show what paths are taken by the agent from obtained Q tables. Three different policies have been
used for the experiments- PRANDOM, PEXPLOIT and PGREEDY. In all the policies, in a particular room if pickup and dropoff action is 
applicable we choose that else:

PRANDOM: Applicable operator chosen randomly, PEXPLOIT: Applicable operator with highest q-value is chosen with probability 0.85 and 
with 0.15 probability a random operator is chosen, PGREEDY: Applicable operator with highest q - value is chosen.

Experiment results are saved in a file which shows rewards received by the agent at different steps and also the q tables after each 
experiment using different policies. Th paths learned by the agent in each experiment are visualized(shown with arrows).
