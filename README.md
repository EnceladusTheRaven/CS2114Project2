# CS2114Project2
Repository for Project 2 in CS2114

How To Use:
Run the Table class as a java application, from there the game is playable in the console.
Every Test class is a JUnit test, run each one as a JUnit test.

Reflection: 
Mostly everything stayed the same from the specs, we just had to add more methods to classes like Table, for example to ensure that the game is runnable and playable. In our LinkedChain, we added more functions to be able to find elements at indexes and add elements to indexes and such. We wanted to make our LinkedChain as flexible as possible. We were able to figure out all of our unknowns. For example, we were worried about deck shuffling but once we understood a simple way to do it, it became an easy method to create. We were able to reach everything in our MVP and we hope that we can realize some of our stretch goals over the next week.

Next Steps: 
Everything in our MVP has been completed. We will try to realize the stretch goals over the course of this week. We think a round history and saved games would be the most realistic to achieve over the next week, but we will attempt everything. We will need probably a class for round history and tests for that class, I am unsure how we will save games yet. We will all attempt to work with each other on these stretch goals.

Attribution: 

| Class           | Who worked on it | How did they work on it |
| :---:           | :---:            | :---:                   |
| Card            | Robert           | Wrote                   |
| LinkedChain     | Robert           | Wrote                   |
| Deck            | Jonathan         | Wrote                   |
| Bet             | Prabhav          | Wrote                   |
| Hand            | Robert           | Wrote, GenAI caught bugs|
| Player          | Shrey            | Wrote                   |
| Opponent        | Jonathan         | Wrote                   |
| Table           | Prabhav          | Wrote                   |
| CardTest        | Shrey            | Wrote                   |
| LinkedChainTest | Robert           | Wrote, GenAI assisted   |
| DeckTest        | Jonathan         | Wrote, GenAI assisted   |
| BetTest         | Prabhav          | Wrote, GenAI assisted   |
| HandTest        | Robert           | Wrote, GenAI assisted   |
| PlayerTest      | Shrey            | Wrote, GenAI assisted   |
| OpponentTest    | Jonathan         | Right, GenAI assisted   |
| TableTest       | Prabhav          | Right                   |

GenAI was used to mostly assist with the test cases, and in the case of Hand, help find bugs because of the complexity of the code.
