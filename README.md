# Stepper Application - User Guide

## Introduction
The Stepper Application is a Java 8-based project designed to execute flows of operations on the user's local machine. Each operation is referred to as a "step," and the system allows users to add their own custom steps. This application is built to collect, export, delete files, run command-line commands, send HTTP calls, and perform various other tasks as needed. The system uses a combination of server-side (HttpServlet) and client-side (JavaFX) technologies, along with Gson and JAXB for data serialization. The flows of operations are described in an XML file, which users can upload to the system for execution. Multi-threading is employed to improve performance and handle concurrent operations efficiently.

## Prerequisites
Before using the Stepper Application, ensure that you have the following components set up:

1. Java 8 or later installed on your local machine.

## Installation and Setup
Follow these steps to install and set up the Stepper Application:

1. **Server Setup:**
   - Download the `stepper_app.war` file and deploy it using Apache Tomcat (tested with Tomcat 9).
   - The server provides the API for managing flows, users, user roles, and execution history.

2. **Client Setup:**
   - Download the `ClientApp.jar` and `AdminApp.jar` files to your local machine.
   - The ClientApp is a JavaFX-based desktop GUI for users to interact with the Stepper Application and execute flows.
   - The AdminApp is a JavaFX-based desktop GUI for administrators to manage flows, users, and user roles.

## Using the Stepper Application

### Step 1: AdminApp - Uploading Flows, Managing Users, and User Roles
The AdminApp provides administrators with the ability to manage flows, users, and user roles. User roles determine the flows a user can see and execute.

1. Execute the AdminApp JAR file on your local machine.

2. Use the graphical interface to:
   - Upload XML flow files to define new flows for users to execute.
   - Manage user accounts and their associated roles, granting specific flow access based on roles.
   - View statistics and data of all past flow executions.

### Step 2: ClientApp - Viewing, Executing Flows, and Monitoring Executions
The ClientApp is designed for regular users who can view and execute flows assigned to their roles.

1. Execute the ClientApp JAR file on your local machine.

2. Use the graphical interface to:
   - View the list of available flows based on your assigned user roles.
   - Select a flow from the list and initiate its execution.
   - Monitor the progress of ongoing executions and view results of completed executions.
   - Access a history of your past flow executions.

Please note that the AdminApp does not have execution capabilities; it is solely for administrative tasks. Users can execute flows and monitor their own executions through the ClientApp.

## Multi-Threads
The Stepper Application utilizes multi-threading to enhance performance and efficiency. Multi-threading allows the application to execute multiple steps concurrently, reducing the overall execution time for flows containing multiple operations. This approach ensures that time-consuming operations do not hinder the execution of subsequent steps, leading to faster and more responsive flow execution.

## Conclusion
Congratulations! You have successfully set up and used the Stepper Application to execute flows of operations on your local machine. Administrators can manage flows, users, and user roles using the AdminApp, while regular users can execute flows and monitor their executions through the ClientApp.

For any issues, questions, or feedback, please contact "roni230698@gmail.com". We hope you find the Stepper Application helpful in streamlining your operations and improving productivity!
