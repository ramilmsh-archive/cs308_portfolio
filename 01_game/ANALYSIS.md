# CompSci 308: Game Analysis

> This is the link to the assignment: [Game](http://www.cs.duke.edu/courses/compsci308/current/assign/01_game/)
>
> [Code](https://github.com/ramilmsh-archive/cs308_bounceball) for the project 

## Project Journal

### Time Review

I have spent around 12 hours creating and debugging this project in total. Around 1 hour I dedicated to planning, before I started writing code, then I had to formalize it for Sprint 1: Planning, which took me ~20 minutes.

I started on September 1, then took a week-long break, and finished it mostly on September 7-8, added few features on September 10 and fixed two small, but crucial bugs on September 12, during and shortly after the class.

I have spent most of my time planning structure, of every piece of the game, coding new features and debugging took me a little less. At the very end I have spent around 30 minutes documenting main classes and the objects package.

I tried to commit every time I finished something of importance, for example, I finished creating collisions, and they work with bugs - commit, found and fixed a bug - commit.

I think, researching ball-of-corner collision took me way too long, despite that it wasn't that difficult. Creating a math package with Vector class - best use fo my time. This helped me make all of my position/velocity calculations much easier. I wish I had time to implement rotation matrices, however, it would take too long, so I had to leave it for now.

### Commits

I have made 30 commits in total, with 20-30 code-related insertions and slightly less deletions.

* I think my commits represent my project history very accurately. In fact, this is how I traced back the dates, when I was working on analysis. I tried to make commit messages represent what has actually happened. However, in my daily experience I normally only give names to commits, where something important and note-worthy happened, so that I can easily trace those back, instead of reading through the whole commit history.

* Commits:
  * [My favorite commit](https://coursework.cs.duke.edu/CompSci308_2017Fall/game_rs380/commit/3d7533e4f57b026dd6b743eeb8ad266f7c6fbd5f)
    * It has fixed a minor, but game-breaking bug, where I calculated ball-wall and ball-paddle collisions for every block, which cause a plethora of strange bugs
    * This was a very specific and very important change, which affected gamiing experience
    * It absolutely was, since it was really small and self-explanatory
  * [My not-so-favorite commit](https://coursework.cs.duke.edu/CompSci308_2017Fall/game_rs380/commit/d3e7c30c40fc810cb55360be0be8efc5c747261a)
    * It has introduced a new paradigma into the structure, creating Actions class and moving all the power-ups there, as well as triplets and tuples, etc.
    * After working on power-ups I realised I needed to restructure my code, however, I got carried away, and forgot to break this one up into maningful commits. I only remebered, when the new structure was somewhat working already.
    * No, it particularly. It has unrelated name, 255 insertions in 13 files, so it would take a lot of time and  effort to review it

### Conclusions

* In my experience it is really hard to overestimate a project, since in the process of implementing, you come up with something new and exciting. So I took it as a rule, never to care about the end goal, just the direction, and implement the most flexible structure I can from day 0, to avoid too much refactoring. I think I underestimated the power-ups, a little bit, so I had to remake the whole structure. In the future, I think I should try to stick to the rule above better, to avoid that
* Actions, I did not realise that different objects could trigger same power up, in the begining, so I had to completely redo it.
* I should keep trying to overestimate a project, trying to generalize the solution, instead of hard-coding, care about structure first and result - second. I should start making project structures with dependancies on paper first, before writing code, in order to be able to have a visual "map". I shoud stop getting caught up in small detail, trying to get something perfect, leaving less time for other parts.
* Text screens. They are terrible. I am ashamed of how bad they are right now. I would have a dedicated class to handle those and read them from a file or something

## Design Review

### Status

* Yes, I think the code is generally consistent in structure, style and naming conventions.
* I think the code is generally readable. I believe in polymorphic structure of the code:
    ```java
        Ball::collide(Paddle); // Collide ball with paddle
        Ball::collide(Block);  // Collide ball with block
    ```
  * it does require comments for me to remember and for others to understand sometimes, though, when I do something non-standard, like calculating the section in which collision happened in Ball::collide(Block), but I do not think it is a huge problem, since I cannot avoid doing it, and clear explanations are one way of going about doing it
  * I think dependancies are generally well-packaged
  * Actions.java
    * Manages the power-ups
    * the naming is pretty self-explanatory:
      ```java
      class Actions ...; // manages actions
      actions::scaleBallSpeed; // scales ball's speed
      actions::spawnBalls; // spawns balls
      ```
  * ActionPaddle.java
    * Adds power-ups to the paddle
    * It is implied, but never explained that the actions happens on collision, making it kind of weird. The actions accept Triplet<Block, Paddle, Ball>, but there is no ball in ball-to-paddle collision.
      ```java
      Ball Ball::addTo(Group);
      ```
      is a weird structure, since it returns self, after adding it to group, in order to be able to do:
      ```java
      balls.add(new Ball(...).addTo(Group));
      ```
      which really looks shifty to me, but I could find a more elegant solution, since:
      ```java
      new Ball(...).addTo(Group, ArrayList);
      ```
      seemed even shiftier, but in retrospective, I think, this is how I should have done it

### Design

* The main class handles acitons at every step, creating objects, altering their behavior and moving them. Objects handle low-level events, triggered by the main class.
* Simply add file without extention named [last existing level + 1] into ./data and populate it with arbitrary numbers of blocks
* I have hardcoded one paddle, which is easy to fix, but would require time, and I handle event-triggering in a very simplistic and primitive manner, this makes changing the gameplay, a little difficult. I should have used javafx.Events
* Say you want to process key actions: you would have to alter key handler, with a switch statement, it assumes you want to do something with the game itself, you cannot pass key presses to specific objects.

### Alternate Designs

* Text diplaying:
  * Design decisions:
    * Text. Dedicated class, reading text from file. Would take too long. Would be more flexible and readable
    * Triplet. Since collisions normally happen between only two objects at a time, it would be more logical to have tuples instead, however, this would make the structure less flexible, despite being more natural and readable
  * Bugs:
    * Game cannot handle sevral collisions at a time, e.g., if a ball hits two blocks, it will ignore of those (does not happen often)
    * Handling events weirdly
    * Text does not how up consistently
