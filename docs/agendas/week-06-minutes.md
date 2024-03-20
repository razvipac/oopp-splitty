# Week 6 Minutes

| Key          | Value                    |
| ------------ | ------------------------ |
| Date:        | 19/03/2024               |
| Time:        | 14:45 - 15:30            |
| Location:    | Drebbelweg PC1 Cubicle 1 |
| Chair        | Stanisław Malinowski     |
| Minute Taker | Amin Abid                |
| Attendees:   | Everyone                 |

## Approval of the agenda
The following was added:
* Discussing the feedback of the formative tasks of the last two weeks

## How was this week?
Razvan:
* Created controller for debts
* Some issues with running Spring Server

Mihai:
* Created backbone of Language options
* Adding flag images and testing is left

Vasil:
* Creating websocket endpoints

Stan:
* Creating websocket endpoints

Naman:
* Created GUI for Add/Edit Expense

Amin:
* Started connecting frontend and backend
* Client can create and join events
* Difficulties with passing HTTP body through Java

Any problems faced are to be looked at after the meeting.

## Organisation

* Next week's chair: Vasil Georgiev
* Next week's minute taker: Razvan Paraschiv

Let's use GitLab issues more effectively:
* We create issues based on the tasks we decide on in the meeting
* When you finish your assigned task, please close the issue
* Attach your MRs to your assigned issue
* This was also in the Tasks & Planning feedback

## Tasks for next week

Frontend
* Admin page GUI needs to be modeled
* Updating the JSON dumps
* Connecting frontend to frontend: making the GUI navigatable
* Adding key-binds/shortcuts to the GUI (important for the upcoming HCI assignment)

Backend
* Send WebSocket messages when deletions cascade
* Creating the schema
* Implementing long polling (project requires it to be used)

Connecting Front End to Back End
* Implement Adding/Editing Expense
* Implement Settling Debts
* Implement Adding/Editing Participants
* Implement invitations (sending emails to participants)
* Client listening for live changes (WebSockets)

## Team reshuffling
* Moving forward, we no longer have a frontend/backend teams.
* We assign tasks/issues to individuals
* Everyone gets to work on all parts of the application

## Rubrics for the past assignments

**Tasks & Planning**
* Traceability: Add more labels, and put all issues in backlog (which we don't have yet)
* Time tracking: put time estimations on all issues. Important, as this is a pass/fail. Also add weights to all issues.
* Feature isolation: use nested feature branches, so branches have sub branches.
* Areas of Expertise: swap teams


**Technology**
* Dependency Injection: "To get higher grade look at @Component @ConfigurationProperties(prefix = "app"),  javax.validation.constraints @NotNull, I am very happy  that you use @RequestBody"
* Communication: Use long polling too
* Data Transfer: "Use more dtos, use jackson implicitly for higher grade"

**Code contributions and Code reviews**
* Focused Commits: keep commits to around 100 lines
* Reviewability: approve MR within a day
* Code Review: reviews should be a long back and forth thread of discussion in MR
