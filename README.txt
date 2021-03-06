/******************************************************************************************************************/
                              Submission by: Max Lee and Shreyash Patodia.
                              Student Number: 719577 and 767336
                                          File: comments.txt
/******************************************************************************************************************/
                                            Introduction:
------------------------------------------------------------------------------------------------------------------------

In this comments.txt file we talk about how we went about making an agent that plays the Slider game and does an
extremely good job at beating it's makers at it (without using too much space at that).

In our final submission we have a myriad of agents, strategies and scorers. Our final agent Shreya uses three
strategies, AlphaBetaGreedy, AlphaBetaBlocking and AlphaBetaEnd and also two scorers, BlockingScorer and EndGameScorer.
We decided that using different approaches at different stages of the game would be best because then we could leverage
domain specific knowledge in order to score the game keeping in mind the aspects that affect the game the most in those
stages. We will first detail the file structure of our submission to make it a little bit easier for you to be able to
go through it and then explain in detail the creative aspects of not just our final submission but also other things
that we tried but didn't work so well for us.

Our main focus in this project was to find a winning strategy for the game, and as we progressed through the creation of
our AI the best strategy in our head changed from just move forward, to whoever makes more lateral moves loses and
finally came down to trying to make the opponent make more lateral moves than us by blocking their forward direction.
We will explain the working of our strategies and heuristics in the creativity section that follows.

                                            File Structure:
------------------------------------------------------------------------------------------------------------------------

We have a number of packages and files which help us produce the most modular and re-usable project possible. This
allowed us to create many try out many different things before arriving at our final submission. The varies levels of
our file structure are detailed below:

        |__com.teammaxine
           |__agents - This package contains all our agents.
              |__Agent - This was the abstract super class for all our agents, the init and update
                         operations for our agents were defined in this class allowing us to be able
                         to create new agents on the fly without having to deal with the tedious aspects
                         of doing so. We think this is one of the first creative aspects of our projects,
                         because it allows for us to be creative in other ways.
              |__JungEun - This was our first agent that just played random (kind of) moves.
              |__Shreya - Our final AI (more about this later)
              |__ShreyaCache - An AI that used transposition tables, not our final submission due to memory
                               constraints.
              |__Shreyundo - Our first AI that did not deep copy the board, allowed us to make significant
                             improvements in terms of speed and helped us build our final AI.
              |__UpMan - An agent that makes the best forward move, if there are no forward moves then makes
                         the best lateral move. Surprisingly hard to beat.
              |__UserAgent - An agent that allows us (users) to play against our AI, another one of the creative
                             things we did in order to aid development.
              |__Xena - An AI that uses MonteCarlo Tree Search, helped us come up with a greedy strategy for the
                        start of the game.
           |__board - Contains different components of the board representation in our game.
              |__ actions
                  |__ AgentAction - A class that extends Move in order to give us more information about the move,
                                    something creative that we did but underutilized due to the simple nature of the
                                    game and we reckon such an extension could be useful for games that have different
                                    types of pieces for each player.
              |__ elements
                  |__Board
                  |__BoardAgent
                  |__Cell
                  |__CompressedBoard - A creative approach that did not make it's way into our final submission
                                       (explanation later)
                  |__Horizontal - The horizontal player's info as seen by the board.
                  |__Vertical - The vertical player's info as seen by the board.
              |__ helpers
                  |__BoardCompressor - Another creative approach that did not pay off.
                  |__Compressor - The generic parent class of BoardCompressor
                  |__CompressionTestDriver
                  |__Generator - Generates random board.
                  |__LRUCache - Our attempt at doing transposition tables, that did not meet the memory requirements of
                                the project.
                  |__MathHelper
                  |__Permute
                  |__State - Part of our implementation of the transposition table
                  |__Vector2 - Our coordinate system
              |__ scorer - Contains the different scorers we created to evaluate the game.
                  |__AlphaScorer
                  |__BetsScorer
                  |__BlockingScorer - One of our final scorers.
                  |__DeltaScorer
                  |__EndGameScorer - The other one of our final scorers.
                  |__MonteCarloScorer
                  |__MontyPythonScorer
                  |__ProximityScorer
                  |__Scorer - The parent class all scorers derive from.
                  |__ScorerTestDriver

                                            Creative points:
------------------------------------------------------------------------------------------------------------------------

Algorithmic creativity:

We chose to use "minimax with alpha beta pruning" as our final algorithm for the assignment, but we included elements
of "MonteCarlo Tree Search" for our early game policy. We also tried an iterative deepening version of alpha beta
pruning called MTD(f) algorithm (https://en.wikipedia.org/wiki/MTD-f) which was implemented in AlphaBetaCache but was too
resource hungry and wouldn't work within the limits of the running time of our assignment, we tried to remove the
iterative deepening aspect from it so that it would take less time (this is the state that file is in right now) but
the memory requirements of the Transposition table was far too much causing our program to run really slow. But the
algorithm did prune a lot of our nodes and if not for the memory restrictions of the project would have been part of
our final submission.

The alpha beta search(es) that we use is also modified - The first, AlphaBetaGreedy is modified to be greedy,
in that it only evaluates some of the branches reducing the branching factor and thus the search time (similar
to how a monte-carlo search would work). Our other two implementations AlphaBetaBlocking and AlphaBetaEnd
are close to being pure alpha beta but third one also counts lateral moves made by our player
in order to send them to the EndGameScorer to penalise those moves (lateral moves are bad!).


Early Game Policy (uses AlphaBetaGreedy and BlockingScorer):

Early on in the game, we know that both agents are going to make forward moves (move right if we are H and 
up if we are V). This is because making those moves drives them closer to the goal of winning the game and it is very
unlikely to encounter a situation early on in the game wherein one of the players cannot make forward moves.
With this knowledge we choose only "optimistically" good moves to evaluate in our AlphaBetaGreedy i.e. it only looks at
the forward moves and expands the tree on these optimistically good nodes (like a Monte-Carlo tree search would). We
not only pick forward moves but we pick the most un-congested forward moves, i.e. ones that don't have too many other
pieces in it's proximity. This allows us to decrease the branching factor from a possible 3*(n - 1) to less than (n - 1)
most of the time.

We have played the game a number of times, and our greedy approach does not lead to any loss in performance on the
contrary it allows us to be able to search a bit deeper during the more critical times in the game. We think this is
a great use of domain-specific knowledge and allows us to prune off a lot of nodes (and time).


Mid-Game Policy (uses AlphaBetaBlocking adn BlockingScorer):

We use a mid-game policy that focuses mainly on blocking the other player because as we see the game it is just us
being able to make the other player make more lateral moves than us in order to win. In this part of the game we not
just consider moving towards the goal but also focus on being able to block the other player to set-up the game in such
a way that the opponent has no forward moves left. This allows us to be able to choke the opponents player and if the
other player is not created to counter this, we will win games even when we go second. We also don't mind making some
lateral moves at this stage in order to move pieces that aren't blocking any opposing pieces. This strategy may look
like it is contradicting our notion of how to win the game, but we focus on making the opposing player make more lateral
moves than us rather than making the least lateral moves possible. This strategy works really well in practice and
our AI wins most of the games it has played.

Our heuristic function in this part of the does not value taking a piece off a board too much, since blocking is best
with more pieces and it also doesn't penalise lateral moves made. This strategy slightly contrasts our end game policy
as we will explain in the next paragraph.


End-Game Policy (uses AlphaBetaEnd and EndGameScorer):

At the end of the game, making lateral moves in order to block someone because it could be the difference between
winning and losing. We try to consider a few more aspects than our mid-game policy in our end-game policy. First of all
we weight actions that take a piece off the board a lot more here. We also penalize any lateral moves made by our
player in order to promote moving forward and finishing off the game. Our blocking strategy still exists in this scorer
but the value of block is far less, because by this time we would have set up the game by making the opposition do
lateral moves. We also differentiate between lateral moves because there is one sensible lateral move and one that is
just bad. We will use the mock board below to explain what we mean by this:

                            + + + H + +
                            + + + V + +

Say we are V in this example, moving left is more sensible for us than moving right because the H piece can follow us
if we move right meaning we made a lateral move to no avail. We tax the left move a little bit less than the right move
in order to promote our pieces to move in the right lateral direction. If the left-side is also blocked then it would
reflect in the scorer through the blocking value in our scorers, allowing us to make the best lateral move when we are
our of forward moves. We also use the information from the root node of the search to count how many moves we have taken
off the board in order to value that to a higher extent.

Design:

We believe that our design is extremely creative since in our framework, an agent has a strategy which can be swapped
up every time the agent needs to make a move. Each strategy use a scorer for it's evaluation and these scorers can
also be swapped around in order to provide a polymorphic design. We leverage this by switching our strategy from
AlphaBetaGreedy to AlphaBetaBlocking when transitioning from early game to the middle of the game and then transition
to AlphaBetaEnd at the end of the game. Such a design means that in the future if we wanted to use NegaMax as our
strategy all we would need to do is write the algorithm, pick a scorer and then plug it into one of our agents, making
the process of extension as painless as possible. This is especially important in a project like this where we were
iterating on our design and had to try different things to find the best combination.

Performance:

We wrote extremely good code for Part-A where counting the number of legal moves for us was an O(n) operation rather
than being an O(n^2) operation. For this part we need to get legal moves a potentially hundreds of times each game, and
we believe that our efficiency of our part-A code allows us to go deeper into the tree and be faster for this
part of our project.

Other creative aspects:

If we ever reach a terminal state where we win the game then we return INT_MAX - max_depth + depth, meaning that
a game state where we won without having to travel deeper would be given a higher value. Similarly we return
INT_MIN + max_depth - depth to return a higher value for a state where we lost after travelling the most depth. This
allows us to distinguish between winning states and take how quickly we win into consideration.

We also have in our scorer a routine that taxes the player if the next move is not their move since the other player
will probably move closer to the goal in the next move, this allows us to almost simulate looking one level deeper than
we actually do, giving us more insight into the game.

One last point for this section is that we don't really ever create a new copy of board or a snapshot of the state of
the game, we make moves on a board and undo them before returning form the sections saving us a significant amount of
memory (this memory would have been required if we chose to deep copy the board).

------------------------------------------------------------------------------------------------------------------------

Things we tried that didn't make it to our final submission:

Monte-Carlo Search and Machine Learning:

Monte Carlo search was utilized in early versions of the AI agent. It essentially counted number of wins, loss and draws
after certain number of random branches deep down are visited. This strategy was highly effective in the early stages of
our development however it was quickly over-taken by smart heuristics and we ended up purely using Minimax with Alpha
Beta pruning. Potential improvement here can be made such that it does not play 'random' games but instead make logical 
choices.

Learner class was developed to tweak Monte Carlo agent by playing against itself or other agent however we ended up not
doing this due to time constraints.


Iterative Deepening and Transposition Tables:

We tried to implement move ordering using iterative deepening and Transposition Tables implemented using a LRU Cache,
a Compressed Board as key (compressed board is just a byte array, which is much smaller in size than the actual board)
and a state class to store the score, depth, lowerbound and upperbound associated with the board. We successfully got
this to work but the memory requirements of the Cache were far too high for it to be able to function well, meaning
that we couldn't submit it as part of our final AI. Even though, we don't submit these classes as part of our final
submission it required a significant amount of effort to make these classes and there was also a lot of creative
thinking involved creating an LRU Cache instead of a standard hash table, and in compressing the board in order to
optimise memory usage but sadly we couldn't get the memory usage of a large enough size cache to be low enough to
include in our final submission.

Sorting one level deep:

We also tried looking one level deeper in the tree in order to sort the moves according to the value of the board at
the next level. But this lead to a decrease in speed and didn't allow us to look any deeper so we ended up not using
this idea.


------------------------------------------------------------------------------------------------------------------------
