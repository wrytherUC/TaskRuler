# Task Ruler

---

Design Document

Authors:

- Aaron Kitelinger
- Wesley Ryther
- Keshawn Thomas

## Introduction

Is it hard for you to organize and concentrate on tasks you need to do? Do you find yourself focusing to hard on a task and lose track of time?

Task Ruler can help you:

- Create tasks and sub tasks.
- Add the tasks to your calendar.
- Set up alarms on when to start.
- Set timers for task to help take you out of a hyper focused state.
- Associate tasks to your Google calendar.
- Save tasks to use as templates.

Use your Android device to create tasks and sub tasks to help keep your organized.

## Storyboard

[Task Ruler Storyboard](https://projects.invisionapp.com/freehand/document/2fhhFHCLk)

![Task Ruler First Screen](https://github.com/wrytherUC/TaskRuler/blob/master/TaskRulerMainScreenStoryboard.png)

## Functional Requirements

### Requirement 100.0: Start timer

#### Scenario

- A user that wants to do specific tasks for a day. 

#### Dependencies

- Android timer feature is available.

#### Assumptions

- All tasks are entered in minutes.

- Multiple tasks are being done.

#### Examples

1.1

**Given** A daily task needs to be recorded, or user is curious about time spent doing daily task

**When** A user opens application, they would see task greeting

**Then** Press home button which should pull basic list and allow user to begin basic android timer for task


1.2  

**Given** A person has numerous task to accomplish in a hour or two before places close

**When**  User opens applications

**Then** A list view of user entered scheduled tasks will show ,from closet to deadline to furthest with active timer alerting them of deadline

### Requirement 101.0: Save Tasks for a future day

#### Scenario

- A user has plans to do tasks on a different day and wants to mark them down for that day

#### Dependencies

- A calendar feature is available on the phone

- App has permission to edit the calendar of the user

#### Assumptions

- User has a google account connected to a calendar

#### Examples

2.1  

**Given** a feed of plant data is available  
**Given** GPS details are available  
**When**

-	Select the plant Malus domestica ‘Fuji’
-	Take a photo of a Fuji apple seedling  
     **Then** when I navigate to the Specimen History view, I should see at least one Malus domestica ‘Fuji’ specimen with the a photo of a Fuji apple seedling.
         
#### Requirement 102.0 Add Subtasks to a task

#### Scenario 

- A user had subtasks to do within a single task

#### Dependencies

- A user has entered a task

#### Assumptions

- The user's subtasks timers will equal the total amount of time of the overhead task

- The subtasks are wanted to be timed separately 

#### Examples

3.1

**Given** a user has two homework assignments due for a week long module

**When** they need help setting up time to do them at separate times

**Then** a subtask can be set up for each assignment that will be associated to the main task for
the their weekly module homework task

3.2

**Given** a user needs to do laundry

**When** it will take multiple steps to do, including gathering the laundry, bringing the laundry to the
laundry area, adding the clothes to the dryer, switching them to the dryer, folding the clothes, and 
putting the clothes away

**Then** a subtask can be created for each step, each task being associated to the main task of doing laundry

#### Requirement 103.0 Have a reminder/alarm for tasks

#### Scenario

- A user is being reminded about their tasks for the day

#### Dependencies

- Notifications for the app are turned on

#### Assumptions

- The user wants to be reminded of the tasks that they want to do today

#### Examples

4.1 

**Given** a user wants to have a reminder for Friday at 8 PM

**When** they need to take the trash out for pickup on Saturday

**Then** an alarm can be added to the task that will go off on Friday at 8 PM

4.2

**Given** a user wants to have a reminder for Saturday at 3 PM

**When** there is a party to attend at 5 PM

**Then**  a Google calendar reminder can be created for Saturday at 3 PM

## Class Diagram

![ClassDiagram](https://github.com/wrytherUC/TaskRuler/blob/master/Copy%20of%20Executive%20Dysfunction%20App.drawio.png)

### Class Diagram Description

- **MainActivity:**  The first screen the user sees.  This will have a place to list their tasks. And a button to start the task timer that will appear on a sperate page. Also a button  that  will open a  task calendar

- **RetrofitInstance:** Boostrap class required for Retrofit.

- **Task:** Noun class that represents task

- **Timer:** Noun class that represents a timer.

- **ITaskDAO:** Interface for Retrofit to find and parse Task JSON.

- **ITimerDAO:** Interface for Room to persist Timer data

## Scrum Roles

- DevOps/Product Owner/Scrum Master: Aaron Kitelinger
- Frontend Developer: Keshawn Thomas
- Integration Developer: Wesley Ryther

## GitHub Repository 

[https://github.com/wrytherUC/TaskRuler](https://github.com/wrytherUC/TaskRuler)

## Weekly Meeting

- First Meeting: Tuesdays at 6:30 PM
- Second Meeting: Fridays 6:30 PM
- Meeting: Teams Group
