# Week 9 Agenda

| Key          | Value                    |
| ------------ | ------------------------ |
| Date:        | 09/04/2024               |
| Time:        | 14:45 - 15:30            |
| Location:    | Drebbelweg PC1 Cubicle 1 |
| Chair        | Amin Abid                |
| Minute Taker | Vasil Georgiev           |
| Attendees:   | Everyone                 |

## Checklist for Basic Requirements

As a user, I want..
- [ ] To connect to a Splitty server of my choice without recompilation, so I can manage expenses
(The server url should be added to a config file, to avoid a hard-coding and make it changeable)

- [ ] To connect multiple clients at once to one server, so two users can use Splitty simultaneously
- [ ] To create or join an event, so I can manage expenses for that event
- [ ] To see an invite code in the overview of an event that I can give to others, so they can join
- [ ] To add/edit/remove participants to an event, so I can create an complete and accurate list
- [ ] To give an event a title, so it is easy to differentiate two events
- [ ] To edit the title of an event, so I can fix typos
- [ ] To see all created expenses for the current event, so I see what has already been entered
- [ ] To add new expenses to the event, so I can add missing items or activities.
(In this basic form, an expense only requires a paying participant, an amount, and a title,
expenses are assumed to be split equally among all participants)
- [ ] To edit or remove existing expenses, so I can correct mistakes
- [ ] To see the total sum of all expenses, the share per person and how much each person owes
to/is owed by the group, so all participants can meet and settle their debts.
(This is the bare minimum to settle the debt and inconvenient. It will be extended later)
- [ ] To switch between events, so I can manage expenses of multiple events in the same session
- [ ] To switch between English and Dutch, so I can use my preferred language.
(In this basic form, the language should be configured in the config file and must only be
considered once when starting of the application)

As an admin, I want...
- [ ] To login into a password-protected management overview, so I can manage my server instance

- [ ] To see a random password in the server output, so I can login to the management overview.
- [ ] To see all events of the server, so I can explore the existing data.
- [ ] To order all existing events by title, creation date, and last activity, so I can find specific events
- [ ] To delete events, so I can clean up the database
- [ ] To download a JSON dump of a selected event, including all details, so I can create backups
- [ ] To import an event from a JSON dump, so I can restore backups.
(the handling of repeated imports or import conflicts remains undefined)
