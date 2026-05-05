# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Input: Keyboard - **VALID**

I will test that I can move my selection on the board and place counters,
using the keyboard

### Test Data To Use

I will press the right arrow key 3x then the left arrow key 3x and then the up arrow key.

### Expected Test Result

The cursor (Highlighted cell) should shift right by 3 spaces, then left by 3 spaces
and then finally a red counter should be placed in the currently highlighted cell

---

## Input: Keyboard - **INVALID**

I will test my program's reaction to multiple invalid keypresses

### Test Data To Use

I will press every key on the keyboard

### Expected Test Result

There should be no visible changes

---

## Input: Selection - **VALID**

I will test if I can place counters on the board

### Test Data To Use

I will attempt to place a counter in a valid spot on the board

### Expected Test Result

A counter should be placed

---

## Input: Selection - **BOUNDARY**

I will test if I can select and place counters in the cells at the edges of the board

### Test Data To Use

I will attempt to place counters at either end of the board

### Expected Test Result

A counter should be placed in both cells

---

## Input: Selection - **INVALID**

I will test if the code prohibits me from attempting to select a cell outside the bounds of the board

### Test Data To Use

I will use the arrowkeys to attempt to navigate past the righthandside and lefthandside of the board.

### Expected Test Result

Once the cursor reaches the edge of the board there should be no further movement to that side
(Prohibited movments should be indicated as a screen refresh with no visible change)

---

## Input: Selection - **INVALID**

I will test if the game prohibits me from placing a counter in an invalid spot.

### Test Data To Use

I will attempt to place a counter between 2 opponent counters and
attempt to place a counter on top of a prexisiting counter.

### Expected Test Result

A counter should not be placed in both cases.
(Indicated by a screen refresh with no visible change)

---

## Gameplay: Counter removal when bordered by opponent counters

I will test if the game correctly removes counters that become surrounded by opponent counters

### Test Data To Use

I will place a counter next to an opponent counter and allow the opponent to place a counter to surround me

### Expected Test Result

My counter should be surrounded by opponent and removed

---

## Gameplay: Scoring

I will test if the code correctly recognises counter chains and assigns points

### Test Data To Use

I will create a chain of 3+ counters

### Expected Test Result

My counters should be removed and the amount added onto my score total.

---

## Gameplay: Scoring

I will test if the code correctly recognises counter chains and assigns points

### Test Data To Use

I will create a chain of 3+ counters

### Expected Test Result

My counters should be removed and the amount added onto my score total.

---

## Gameplay: Win condition

I will test if the game triggers the win condition upon either:
A player gets >= 10 points or the current player has no move

### Test Data To Use

I will trigger a stalemate then
I will allow myself to lose, and finally
I will win a game

### Expected Test Result

upon stalemate the player with the most points should win.
when I lose it should say that "Blue won"
when I win it should say that "Red won"

---





