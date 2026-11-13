# Hoki3D

## Reflection

During development, we made a few changes to the original specification. Our 'User' class uses a 'String' for the user ID instead of a 'long', and 'PrintJob' stores a user ID rather than a 'User' object. We also added methods to 'SLQueue', such as 'remove()', 'contains()', and 'getPosition()', to make managing print jobs easier. We created JUnit tests to check that our queue and user classes work correctly, including edge cases like empty queues and missing elements. The model can operate independently from the website because it handles the data and queue operations without depending on the view. We figured out how to implement the singly linked queue and organize the backend, but connecting the application to an actual 3D printer and tracking print progress are still unresolved. Printer integration, failed print handling, and the administrator features are not fully implemented yet.

# Next Steps

Our next step is to finish the MVP by completing print job submission, connecting the website to the queue, and displaying each job's queue position, estimated wait time, and print status. This will involve finishing the submission methods in PrintController, using methods such as getPosition(), toList(), and size() in SLQueue, and completing the status handling in PrintJob and PrintStatus. After that, we need to add tests for submitting print jobs, checking queue positions, updating print statuses, and handling invalid submissions. One additional feature we plan to work on is an administrator page that allows admins to manage the print queue. This would require changes to PrintController, User, and the website's view, while SLQueue should not need to change because it already has the necessary queue operations. We plan to keep administrator features and authentication as stretch goals, but will not implement actual 3D printer integration at this stage.

# Attribution

| Class                | Person | Conribution                    |
|----------------------|--------|--------------------------------|
| LinkMappings.java    | Paulo  | Wrote code                     |
| PrintController.java | Paulo  | Wrote code                     |
| PrintJob.java        | Paulo  | Wrote code                     |
| PrintQueue.java      | Paulo  | Wrote code                     |
| PrintQueueTest.java  | Paulo  | Used GenAI to write test cases |
| PrintStatus.java     | Paulo  | Wrote Code                     |
| SLQueue.java         | Phani  | Wrote                          |
| SLQueueTest.java     | Phani  | Wrote                          |
| User.java            | Paulo  | Wrote Code                     |
| UserTest.java        | Phani  |                                |
| UserRole.java        |        |                                |

# Gen AI Note

# How to Run
After you run the program, go to <http://localhost:8080> to see the thin view.

Then, click "Upload Print" and upload an STL file. You should then see the print job be added to the queue.
