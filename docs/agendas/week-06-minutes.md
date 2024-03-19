# Week 6 Minutes

| Key          | Value                    |
| ------------ | ------------------------ |
| Date:        | 19/03/2024               |
| Time:        | 14:45 - 15:30            |
| Location:    | Drebbelweg PC1 Cubicle 1 |
| Chair        | Stanisław Malinowski     |
| Minute Taker | Amin Abid                |
| Attendees:   | -                        |

## Approval of the agenda
* Discussing formative feedback of tasks

## How was this week?
Razvan:
* Issue with Spring Server

Mihai:
* Backbone of Language button

Vasil:
* Doing websockets

Stan:
* Doing websockets

Naman:
* GUI Add/Edit Expense, no real issues

Amin:
* Some issues with connecting front and backend
* Task not finished

## Organisation

Next week's chair: Vasil
Next week's minute taker: Razvan

Using issues more:
* Things we decide now, create issue
* When you finish it, close the issue
* Attach each MR to the issue
* Feedback: issues not having 'deadlines' and estimations

## State of the project

Front End
* Missing Admin page
* Updating JSON dumps wiht missing stuff
* Connecting frontend to itself
* Making windows bigger
* Adding key-binds shortcut

Back End
* Delete WebSocket stuff
* (Final version of) schema
* (Change something to) long polling

Connecting Front End to Back End
* Implement Adding/Editing Expense
* Implementing Settling Debts
* Implement Adding/Editing Participants
* Implementing invitations (sending emails?)
* Listening for live changes

## Team reshuffling
* Are we switching teams? Or do we get rid of teams?
* We choose to assign individual issues to individuals instead of teams

## Wrapping up
...

## Questions

* How to send JSON Request Body

# TA Rubrics

* Traceability: Add more labels
* Put all issues to backlog, make scrum board, grab issues from scrum board to current week
* Time tracking: put time estimations on issues (PASS/FAIL, important)
* Add weights to issues
* Improving time tracking and weights will improve whole rubric
* Use nested feature branches
* Areas of Expertise: swap teams

* Dependency Injection: Improvement: To get higher grade look at @Component @ConfigurationProperties(prefix = "app"),  javax.validation.constraints @NotNull, I am very happy  that you use @RequestBody
* Communication
* Data Transfer

* Focused Commits: more than 200 300, then not excellent
* Isolation: ?? MRs that ween't merged on time?
* Reviewability: approve MR within a day!
* Code Review: long back and forth thread of discussion in MR
