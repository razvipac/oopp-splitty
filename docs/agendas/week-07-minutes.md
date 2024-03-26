# Week 6 Minutes

| Key          | Value                      |
| ------------ |----------------------------|
| Date:        | 26/03/2024                 |
| Time:        | 14:45 - 15:30              |
| Location:    | Drebbelweg PC1 Cubicle 1   |
| Chair        | Vasil Georgiev             |
| Minute Taker | Razvan Paraschiv           |
| Attendees:   | Everyone  (Stanislaw late) |

## Approval of the agenda / Additional discussions
The following was added:
* There is an assignment with deadline this Friday. (Pitch draft)


## Organisation / Observations

* Next week's chair: Naman Choudhary
* Next week's minute taker: Mihai Bobeică

* When you finish a task, immediately close the issue on Git to ensure a better organization of "TODO" tasks.
* Review last week issues. Maybe some of them were fully finished and can be closed.
* "There is lack of vision of what we must do". (Personally, I consider we figured out what is ahead and planned for it
  every single time)
* Take a look at the requirements that have not been explored as much.
* We do not necessarily need design.


## How was this week? / State of the Project
Razvan:
* Managed the connection between frontend and backend regarding debts
* Multiple back buttons now function as they should
* Code cleanup and restructuring

Mihai:
* The language button finally works
* Wrote tests for it, however, using absolute path does not work

Vasil:
* Created the Admin page
* Also did most of the GUI, just missing one small aspect

Stan:
* Changed how the server class was organized (because of an issue)
* Implemented a variety of shortcuts for the client

Naman:
* Created multiple tests for the entities in commons folder
* Changed one endpoint for the Debt Controller such that it uses Long Polling

Amin:
* Worked on connecting the frontend and backend specifically for adding participants and expenses
* Issue: cannot retrieve the participants from the server correctly
* Also did some validations to check the fields


## Tasks for next week

* For this Friday we have an assignment, a draft for the pitch.
  It shall be structured as a PDF document with bullet-points. Mihai offered to write it.
* Split testing by folders (commons, server, client).
* We also swap the teams for testing to better understand the project overall.

Frontend:
* Shortcuts must be finished and fix all issues related to them as well
* We should make the start screen the actual starting page and maybe some sort of controller between the pages
* Include the password-protected page for the admin
* Include text for statistics
* Language button must be fixed
* Email notification is not a priority but might be considered (now or later)
* Foreign currencies (again, now or later)
* Update lastActivity value for everytime an Event is being accessed
* We need an undo button
* Fix all back buttons in the entire client (they do not close the popup and the initial stage is now in the main scene)

Backend:
* Reach higher test coverage for most of the implementations
* Add DTOs

Connecting Front End to Back End:
* Connecting WebSockets to the frontend


## Rubrics for the past assignments

**Testing** (Overall Insufficient)
* Coverage: Write as many unit tests as you can (mocks). Our tests were pretty straightforward
* Endpoint Testing: Controller tests (we need to mock the controller) to see whether
  the endpoint is giving the right answer.
* We need at least 80% test coverage for Good.

**HCI** (Overall Insufficient)
* Color Contrast: we have grey and white (use some kind of color contrast)
* Confirmation for Key Actions: must make sure we have a popup saying "Are you sure?"
* TODO: make a popup were it tells what keys can be used ( inform the user what shortcuts can be used)


## Product Pitch && Oral Examination
* Presentation schedule: Tuesday 10:30am
* Everybody is indeed available at that specific slot
* It is better that we meet just before the presentation physically (a couple of days beforehand)


## Assigning issues:

Razvan:
* Finalize commons test and do several server tests.
* Update lastActivity such that it actually tells the last activity of an event.

Naman:
* Connecting WebSockets to frontend.
* Adding a date attribute to Expense.
* Adding a many-to-many relation between Expense and Participant.
* Modify the Debt class (especially the settle debt methods) accordingly.

Mihai:
* Translate everything (regarding the language aspect).
* Finish the language button.
* Tests for server.

Stan:
* Restructuring the overall project.
* Construct the Undo button.

Vasil:
* Produce tests for the client side.
* Produce tests for the server side.

Amin:
* Build the password protection.
* Taking care of DTOs.

## End of Meeting.