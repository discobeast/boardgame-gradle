# Results of Testing

The test results show the actual outcome of the testing, following the [Test Plan](test-plan.md)

---

## Input: Keyboard - **VALID**

I will test that I can move my selection on the board and place counters,
using the keyboard

### Test Data Used

I will press the right arrow key 3x then the left arrow key 3x and then the up arrow key.

### Test Result

![Keyboard_testing.gif](screenshots/Keyboard_testing.gif)

Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Input: Keyboard - **INVALID**

I will test my program's reaction to multiple invalid keypresses

### Test Data Used

I will press every key on the keyboard

### Test Result

![Keyboard_testing.gif](screenshots/Keyboard_testing.gif)

Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Input: Selection - **VALID**

I will test if I can place counters on the board

### Test Data Used

I will attempt to place a counter in a valid spot on the board

### Test Result


Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Input: Selection - **BOUNDARY**

I will test if I can select and place counters in the cells at the edges of the board

### Test Data Used

I will attempt to place counters at either end of the board

### Test Result


Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Input: Selection - **INVALID**

I will test if the code prohibits me from attempting to select a cell outside the bounds of the board

### Test Data Used

I will use the arrowkeys to attempt to navigate past the righthandside and lefthandside of the board.

### Test Result


Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Input: Selection - **INVALID**

I will test if the game prohibits me from placing a counter in an invalid spot.

### Test Data Used

I will attempt to place a counter between 2 opponent counters and
attempt to place a counter on top of a preexisting counter.

### Test Result



Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Gameplay: Counter removal when bordered by opponent counters

I will test if the game correctly removes counters that become surrounded by opponent counters

### Test Data Used

I will place a counter next to an opponent counter and allow the opponent to place a counter to surround me

### Test Result



Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Gameplay: Scoring

I will test if the code correctly recognizes counter chains and assigns points correctly

### Test Data Used

I will create a chain of 3+ counters

### Test Result



Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## Gameplay: Win condition

I will test if the game triggers the win condition upon either:
A player gets >= 10 points or the current player has no legal move

### Test Data Used

I will trigger a stalemate then
I will allow myself to lose, and finally
I will win a game

### Test Result



Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---

## D

Description

### Test Data Used

Data

### Test Result


Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result. Comment on test result.

---
