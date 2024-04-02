# Week 8 Agenda

| Key          | Value                    |
| ------------ |--------------------------|
| Date:        | 02/04/2024               |
| Time:        | 14:45 - 15:30            |
| Location:    | Drebbelweg PC1 Cubicle 1 |
| Chair        | Naman Choudhary          |
| Minute Taker | Mihai Bobeică            |
| Attendees:   | All                      |

## Opening
Greetings to everyone that is present! (1 min)

## Approval of the agenda
Is there something you would like to add to the agenda? (1 min)

## Points of action
- How was this week for you? How did each of you contribute to the project this week? (5 min)
  - Mihai: Implemented the language button switch, needs to implement it in a live fashion
  - Razvan: Implemented the last activity updates and tests for commons
  - Stan: Refactored the front end so that we use singletons now. Standardized the process of creating new scenes.
  - Vasil: Finished the password protection
  - Amin: Added the ability to get a password from the server for the admin page
  - Naman: Solved n-n relationships. Working on connecting the websockets
- Organisation (3-4 min)
    - Last (mandatory) meeting of this project? Otherwise pick chair and minute taker (last time) (2 min)
      - This meeting is not the last one. Next meeting the Chair is Amin and the Minute Taker is Vasil.
    - Deadlines this friday Self reflection draft 3 pages (2200-2700 words) (What is the difference actual self-reflection?) (1 min)
      - The self reflection draft is formative, the final one is summative.
    - Next friday deadlines code freeze, Self reflection full, Buddycheck 2 (link not up yet) (Only talk code freeze if next week meeting) (1 min)
      - Buddycheck is summative, does not have repair option.
    - We can submit code until Friday at midnight
- From the TA
  - We should check with the TA that we have all basic requirements by next meeting.
  - Pay a lot of attention to the self reflection
  - For the oral presentation they look on the meetings' rubrics and on the product's. The questions will be about the product. You will be asked questions about what you lac
k. For example, if you have done more Front-End than Back-End, you will be asked Back-End questions.
- State of the project (25 min)
    - What is the state of the front-end right now? (2 min)
      - We should connect the front-end to front-end, but looks fine
    - What is the state of the back-end right now? (2 min)
      - Added more DTOs. We need to separate the logic better. We want to exchange as little data as possible. The DTOs need refactoring.
      - The retrieval of expenses and participants is fixed.
    - How is the connecting the front and back end going? (2 min)
      - It's mostly done.
    - Bugs (2 min)
      - Fix the language button so there can be only one instance of the language select menu.
      - A lot of exception drawn form the server
      - We need to fix bugs
    - What are the next steps that our development should take (2 min)
      - We need to finish all basic requirements by the end of this week
    - Creating issues with the things that have to be done this week (15 min)
      - Edit/ expense, participant - Amin
      - Edit & Remove Participant - Amins
      - Add ability to change event title - Amin
      - Edit & Remove Expense - Amin
      - listeners websockets polling - Naman
      - Show recent events in StartScreen - Naman
      - refactor DTOs - Stan
      - info shortcuts -Stan
      - Add shortcuts information - Stan
      - Connect FE To FE - Mihai
      - Refactoring Frontend to use FXML - Mihai
      - add color contrast - Mihai
      - Show invite code in EventOverview - Răzvan
      - Show total sum of expenses - Răzvan
      - Tests - Vasil, Răzvan
      - Finalize the algorithm for settling debts - Răzvan
      - Client error messages - TBD (Can be left out for this week)
      - undo button - TBD (Can be left out for this week)
- Tasks for next week (5 min)
    - Taking into account the state of the project, what are the most important tasks for this week (3 min)
    - Assigning specific issues and deadlines to people. (2 min)
- Wrapping up (1 min)
    - Do you anticipate any hurdles? Can we help you in overcoming them? (1 min)
- Closing (2 min)
    - Any questions? (1 min)
    - Summarizing + Closure (1 min)
