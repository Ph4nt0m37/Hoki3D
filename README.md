# Hoki3D

# How to Run
After you run the program, go to <http://localhost:8080> to see the thin view.

Then, click "Upload Print" and upload an STL file. You should then see the print job be added to the queue.

# Reflection

During development, we made a few changes to the original specification. Our 'User' class uses a 'String' for the user ID instead of a 'long', and 'PrintJob' stores a user ID rather than a 'User' object. We also added methods to 'SLQueue', such as 'remove()', 'contains()', and 'getPosition()', to make managing print jobs easier. We created JUnit tests to check that our queue and user classes work correctly, including edge cases like empty queues and missing elements. The model can operate independently from the website because it handles the data and queue operations without depending on the view. We figured out how to implement the singly linked queue and organize the backend, but connecting the application to an actual 3D printer and tracking print progress are still unresolved. Printer integration, failed print handling, and the administrator features are not fully implemented yet.

# Next Steps

Our next step is to finish the MVP by completing print job submission, connecting the website to the queue, and displaying each job's queue position, estimated wait time, and print status. This will involve finishing the submission methods in PrintController, using methods such as getPosition(), toList(), and size() in SLQueue, and completing the status handling in PrintJob and PrintStatus. After that, we need to add tests for submitting print jobs, checking queue positions, updating print statuses, and handling invalid submissions. One additional feature we plan to work on is an administrator page that allows admins to manage the print queue. This would require changes to PrintController, User, and the website's view, while SLQueue should not need to change because it already has the necessary queue operations. We plan to keep administrator features and authentication as stretch goals, but will not implement actual 3D printer integration at this stage.

# Code Ownership

Paulo: Finish print job submission methods in PrintController and connect website submissions to the queue.

Larry: Complete the remaining SLQueue methods, including getPosition(), toList(), and size(), and write tests for queue operations and positions.

Phani: Complete print status handling in PrintJob and PrintStatus and write tests for status updates.

Aniket: Write tests for print job submissions and invalid submissions and help with integrating the website with the backend.

Group: Work together on the administrator page as a stretch goal, including the necessary changes to PrintController, User, and the website view.


# Attribution

| Class                | Person | Conribution                    |
|----------------------|--------|--------------------------------|
| LinkMappings.java    | Paulo  | Wrote code to handle website page mappings and connect the frontend to the appropriate backend pages.                   
| PrintController.java | Paulo  | Implemented the controller to handle print job submissions and coordinate communication between the website and backend.
| PrintJob.java        | Paulo  | Implemented the print job class, including its fields, constructors, getters, and setters.
| PrintJobTest.java    | Aniket/Larry | Worked to test the PrintJob.java methods; Used AI to resolve testing issues
| PrintQueue.java      | Paulo  | Implemented the print queue functionality for managing submitted print jobs.
| PrintQueueTest.java  | Aniket | Used GenAI to write test cases for the print queue.
| PrintStatus.java     | Paulo  | Implemented the print status functionality to represent the current state of a print job.
| SLQueue.java         | Phani  | Implemented the generic singly linked queue and its methods for adding, removing, finding, and retrieving elements.
| SLQueueTest.java     | Phani  | Wrote JUnit test cases to test the queue methods, including edge cases such as empty queues and missing elements.
| User.java            | Paulo  | Implemented the User class to represent users and store their information.
| UserTest.java        | Phani  | Wrote JUnit test cases to test the User constructor, getters, and setter.
| UserRole.java        | Paulo  | Implemented the UserRole enum to define the different roles a user can have.

# Gen AI Note

We used AI as a tool during development to help with writing and reviewing code, creating JUnit test cases, and understanding how certain parts of the project should work. For example, GenAI was used to help generate test cases for 'PrintQueueTest.java' and to get guidance on implementing and testing the singly linked queue. Along with this, we used AI to help understand why certain JUnit methods were not defined. We reviewed the generated suggestions and made changes as needed to fit our project requirements. The final code and implementation decisions were reviewed by our group.
