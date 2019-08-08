# CellSociety

> We have created a working implementation of cell society for the first sprint, however, after we learned about extra points, we had to do some dramatic changes on the backend, however, since we were not able to integrate it, it remains in, in new_grid_layout, while the visualizer code is most up-to-date on master

## Overall Design

### Overall

- Simulation creates a Grid from Config
- Config can be initialized from an XML file
- Grid consists of Containers
- Containers may contain Cell
- Cell performs actions

### Not implemented

Simulation.java: mostly: semantically good naming scheme, methods are not excessively long, easy to follow, each method does exactly what the name implies

```java
public void createGameButton(String string, double multiplier, int xLocation, int yLocation)
```

Some code is not well optimized:

```java
createStartButtons(myResources.getString("game1"), 0, 0);
createStartButtons(myResources.getString("game2"), 0, 1);
createStartButtons(myResources.getString("game3"), 0, 2);
createStartButtons(myResources.getString("game4"), 0, 3);
createStartButtons(myResources.getString("game5"), 0, 4);
```

Could be optimized:

```java
for (int i = 0; i < 5; ++i) {
    createStartButtons(myResources.getString("game" + (i + 1)), 0, i);
}
```

Or better yet, refactored. It is not well encapsulated, it is almost procedural code. It only has functions and no classes, despite that some funcitonality could be better implemented using OOP paradig. For example, button creation, could be encapsulated into a separate class.

It is not very consistent in terms of structure. However, it is consistent in naming: the method names are descriptive.

## My Design

- Simulation creates a Grid from Config
- Config can be initialized from an XML file
- Grid consists of Containers
- Containers may contain Cell
- Cell performs actions

The Component-Cell system allows me to create a graph, which is a very efficient structure for neighbour finding and flexibility, by moving cells within the components, which is simply changing references, allowing me to create very efficient and flexible system for grid calculation/visualization.

The Config.java file is terrible. I could not find a good way to create a serialazable object in java. It ended up being a collection of public instance variables.

## Flexibility

- Having a dedicated actions package, which allows to create a new action easily, as well as to reuse the code. Having component class allows to create any shape of the cells, with variable sizes, they do not even have to be adjacent or in a strict grid pattern.
- Toroidal. Thanks to using linked lists, we can simply make edge components each other's neighbours
- Infinite. Dynamically creating missing components around cells

## Alternate Desings

- The original design assumed rectangular grid and rectangular cells, with a very strict neighbour definition. This also created a problem of finding neighoburs that satisfy certain conditions, such as empty positions.
- To solve all of those problems simultaneously, we have made a decision to refactor the code, so that there is a separate immovable class container, which hadles visualization, while cell itself will handle calculations. Containers have a collection of neighbours. This allows us to have an arbitrary definitions for neighbours, visualization rules. We lose, on the other hand, random access to the grid, e.g., to navigate, one would have to iterate over all of the subsequent cells. New implementation is better in almost every way, allowing to avoid unnecessary computations and only store essential informations, which will ultimately allow to create bigger cell societies and simplify the API

## Conclusions

The separate actions package. It allows to minimize unrelated code the user has to write, while creating a new simulation, and have more degrees freedom, when creating configuration files, as in having more control over the simulation, diretly from the XML file. Important part is to think of the end-goal: if the simulation is entended as a platform, it shoud be optimized for mmaximum flexibility, while requiring to change only a few pieces of code.

The configuration creator/XMLParser. The idea was to have a flexible object, changing which would cause the simulation to adapt. One would be able to control it, using controls in UI, while the properties would be updated in real time, in the grid. However, we never got around to creating an API to update it, as well as an event chain, to propagate the changes to the simulation, making it just an out-of-place, static, unprotected object, which is not very efficient for storing data. Before starting off with implementing a feature, it is essential to do research on ways to implement it.

- Start communicating with my team more effectively
- Keep focusing on the structure, rather than content
- Stop rushing into implementation, before research and full specification are finalized with all the team members.