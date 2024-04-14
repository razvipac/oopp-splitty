# Splitty

Welcome to Splitty. This application allows you to manage group expenses and debts.

## Getting Started

1. Open the project in the IDE of your choice.
2. Add Run configurations for server.Main and client.Main.
3. In the Run configuration for client.Main, add the following line in VM options

`--module-path="path/to/javafx-sdk-21.0.2/lib" --add-modules=javafx.controls,javafx.fxml`

4. Run server.Main. The server URL can be adjusted in _client/src/main/resources/userSettings/config.json_ and is set to _localhost:8080_ by default.

5. Run client.Main.

If the client gives an error, please check if the server has been started and that the provided URL in config.json is correct.

## Features

**Managing Events:**

- Name your event and create it
- Join existing events with an invite code
- Quickly open recently joined events

**Adding Participants**
- Add participants to your event
- Add and edit their banking information

**Adding Expenses**
- Create expenses in your event
- Enter what the expense was for, who paid for it and how much it cost
- Edit them if details change

**Calculate Debts**
- Splitty can compute from the expenses how much each person owes to whom
- Get an overview of the open debts, along with the creditor's banking information (if available)
- Mark debts as received and close them off

**Administrator Functions**
- View all created events as the administrator in the password-locked control panel
- Delete events at will
- Export events as JSON files, and import them back into the server

**Switch languages**
- Change the application's language on the fly
- Splitty currently supports English, Dutch and Romanian

## Keyboard Shortcuts
| Action | Shortcut |
| ------ | ------ |
| | |
| _Global:_ | |
| Back to previous page | ESC |
| Return to Start Screen | ALT+X |
| Submit form | ALT+ENTER |
| Cycle through dropdown boxes | ENTER |
| Focus next element | TAB |
| Focus previous element | SHIFT+TAB |
| | |
| _Start Screen:_ | |
| Jump to create event field | ALT+C |
| Jump to join event field | ALT+J |
| | |
| _Event Overview:_ | |
| Manage Participants | ALT+P |
| Add Expense | ALT+E |
| Open Debts | ALT+D |

## Implemented HCI Criteria

- Sufficient Color Contrast
    - Color contrast for important buttons has been checked using [this tool](https://webaim.org/resources/contrastchecker/).
- Keyboard Shortcuts
    - Essential functions, like creating an event, have keyboard shortcuts.
- Multi-Modal Visualization
    - More than three button elements contain corresponding icons
- Keyboard Navigation
    - The required flows are possible without mouse input.
- Error Messages
    - There are two types of errors, one for invalid entries in forms, and the other for server errors.
- Informative Feedback
    - Application includes pop-up success boxes for when a change has been applied successfully
- Confirmation for Key Actions
    - All delete actions require the user to confirm the action first

## Implemented Extensions

- Live Language Switch
- Detailed Expenses
    - Splitting expenses in a sub-group is unfinished. Expenses are split equally.
    - Partial settling of debts is not implemented.
- Open Debts
- Email Notification
    - Emails are sent from _ooppteam2@outlook.com_, which can not be configured.
    - Default emails for testing is not implemented.
    - Automatically adding invited participants is not implemented.
    - Sending payment notifications is not implemented, only inviting participants is.

## How to access admin controls

The administrator's control panel is locked behind a randomly-generated password.

**Retrieving the password**
1. Run the server
2. Copy the password that gets printed in the console terminal

Example:
`Admin password: rWLTcj!4CxYqvvRjNKff`

3. Run the client application
4. Click 'Control Panel' on the start screen
5. Enter the password and hit submit

## Location of Long Polling

Long Polling is implemented in:
- OpenDebtsCtrl
    - _client/src/main/java/client/scenes/OpenDebtsCtrl.java_
- ServerUtils
    - _client/src/main/java/client/utils/ServerUtils.java_
- DebtController
    - _server/src/main/java/server/api/DebtController.java_
