# Hoki3D

# Reflection

## Reflection

During development, we made a few changes to the original specification. Our 'User' class uses a 'String' for the user ID instead of a 'long', and 'PrintJob' stores a user ID rather than a 'User' object. We also added methods to 'SLQueue', such as 'remove()', 'contains()', and 'getPosition()', to make managing print jobs easier. We created JUnit tests to check that our queue and user classes work correctly, including edge cases like empty queues and missing elements. The model can operate independently from the website because it handles the data and queue operations without depending on the view. We figured out how to implement the singly linked queue and organize the backend, but connecting the application to an actual 3D printer and tracking print progress are still unresolved. Printer integration, failed print handling, and the administrator features are not fully implemented yet.

# Next Steps

# Attribution

| Class                | Person | Conribution                    |
|----------------------|--------|--------------------------------|
| LinkMappings.java    | Paulo  | Wrote code                     |
| PrintController.java | Paulo  | Wrote code                     |
| PrintJob.java        | Paulo  | Wrote code                     |
| PrintJobTest.java    |        |                                |
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
