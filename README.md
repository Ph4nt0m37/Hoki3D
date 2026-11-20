# Hoki3D

# How to Run
After you run the program, go to <http://localhost:8080> to see the thin view.

Then, click "Upload Print" and upload an STL file. You should then see the print job be added to the queue.

If you click "Start Next Print" you can start the next print.

# Reflection
For this deliverable, we added the ability to start prints and view the statuses of prints. Instead of just uploading prints, you can now start prints and they move through the queue.

### How did your design absorb the change?
For this deliverable, we primarily modified the PrintController and PrintQueue classes to be able to start prints and show current prints. We also added the DatabaseController class to store the print queue persistently.

### Did the change cross the model/view line?
Both the view and model only interact with each other through the PrintController class which acts as a controller between them. The view makes REST requests to the controller, who can then relay information from the model back to the view.

### What changed from your D3 plan?
The D3 plan did not include adding persistent data storage through a database. We added a database so that queue information does not get lost when re-running the program.

### What doesn't work yet?
You can't specify print settings yet and we haven't been able to add an admin view yet. We are also missing a couple of test cases for some of our classes.

# Next Steps
### What is left for the final build?
We need to add the ability to specify print settings. We also need to add some test cases. If we have time, we also want to add the admin view.

### Which stretch goals are you committing to, and which are you dropping?
We still want to be able to add the admin view, but if we run out of time we may have to make it a lot smaller in scope. For example, we initially wanted admins to be able to move prints around in the queue and change print settings. These may take a while to implement, so we might only be able to add the ability to start or cancel prints.

### What will your 3-minute demo show?
In our 3 minute demo, we begin by showing the UI as a regular user. We will upload 2 files and show them appearing in the queue. Then, we will switch to the admin view and start the first print. We will then switch back to the regular user view to show that our print is now printing and we can see its progress. Finally, we will switch back to the admin view to show us cancelling a print.

### Who does what?
Paulo: Add print settings feature. Also work on adding admin view
Larry: 
Phani: 
Aniket: 

# Attribution

| Class                   | Person       | Conribution                               |
|-------------------------|--------------|-------------------------------------------|
| DatabaseController.java | Paulo        | Wrote code                                |
| PrintController.java    | Paulo, Phani | Paulo: Added methods Phani: Added methods |
| PrintQueue.java         | Paulo        | Wrote new methods for starting print      |

# Gen AI Note

We used AI as a tool during development to help fixing bugs, creating JUnit test cases, and understanding how certain parts of the project should work. For example, GenAI was used to solve issues with connecting SQLite, as it would often crash the program. It was also used to suggest fixes for strange HTML formatting which was difficult to fix.
