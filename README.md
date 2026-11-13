# Hoki3D

# Reflection

## Reflection

During development, we made a few changes to the original specification. Our 'User' class uses a 'String' for the user ID instead of a 'long', and 'PrintJob' stores a user ID rather than a 'User' object. We also added methods to 'SLQueue', such as 'remove()', 'contains()', and 'getPosition()', to make managing print jobs easier. We created JUnit tests to check that our queue and user classes work correctly, including edge cases like empty queues and missing elements. The model can operate independently from the website because it handles the data and queue operations without depending on the view. We figured out how to implement the singly linked queue and organize the backend, but connecting the application to an actual 3D printer and tracking print progress are still unresolved. Printer integration, failed print handling, and the administrator features are not fully implemented yet.

# Next Steps

# Attribution

Hoki3D.java
LinkMappings.java
PrintController.java
PrintJob.java
PrintJobTest.java
PrintQueue.java
PrintQueueTest.java
PrintStatus.java
SLQueue.java
SLQueueTest.java
User.java
UserTest.java
UserRole.java

# Gen AI Note