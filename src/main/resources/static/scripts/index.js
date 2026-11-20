import { userIdPromise } from "./user_id.js";

const printQueueDiv = document.getElementById("print-queue-div");
const printJobTemplate = document.getElementById("print-job-template");
const addPrintButton = document.getElementById("add-print-button");
const fileUploadBox = document.getElementById("stlUpload");

const printerImage = document.getElementById("printer-img");
const printerStatusText = document.getElementById("printing-status");
const printingFileDiv = document.getElementById("printing-file");
const startNextPrintButton = document.getElementById("start-print-button");

const yourPrintsDiv = document.getElementById("your-prints-div");
const yourPrintTemplate = document.getElementById("your-print-template");

const userId = await userIdPromise;

loadPrintQueues();
loadCurrentPrint();

addPrintButton.addEventListener("click",()=>{
    fileUploadBox.click()
});

fileUploadBox.addEventListener("change",()=>{
    const formData = new FormData();
    formData.append('file', fileUploadBox.files[0]);
    console.log(fileUploadBox.files[0]);
    fetch(`/api/add-print?file=${fileUploadBox.files[0].name}`,{
        method: "POST",
        body: formData
    }).then(resp=>{
        if (resp.ok) return resp.json();
    }).then(data=>{
        loadPrintQueues();
    });
})


function loadPrintQueues() {
    printQueueDiv.replaceChildren();
    yourPrintsDiv.replaceChildren();
    fetch("/api/get-completed-prints").then(resp=>{
        if (resp.ok) return resp.json();
    }).then(data=>{
        for (const completedPrint of data) {
            // Your Completed Prints
            if (completedPrint['userId'] === userId) {
                const clonedYourPrint = yourPrintTemplate.cloneNode(true);
                clonedYourPrint.querySelector(".fileName").textContent = completedPrint['filePath'];
                clonedYourPrint.querySelector(".estPrintTime").style.display="none";

                const printStatus = completedPrint['status'];
                const printStatusText = clonedYourPrint.querySelector(".print-status");
                printStatusText.textContent = `Status: ${printStatus}`;
                if (printStatus==="COMPLETED") {
                    printStatusText.style.color="#22eb51";
                }else if (printStatus==="CANCELLED") {
                    printStatusText.style.color="#e23333";
                }else if (printStatus==="PRINTING") {
                    printStatusText.style.color="#2555e6";
                }
                
                clonedYourPrint.classList.remove("template");
                yourPrintsDiv.appendChild(clonedYourPrint);
            }
        }
    });
    fetch("/api/get-print-queue").then(resp=>{
        if (resp.ok) return resp.json();
    }).then(data=>{
        console.log(data)
        let queuePos = 1;
        for (const printJob of data) {
            // General Print queue
            const clonedPrintJob = printJobTemplate.cloneNode(true);
            clonedPrintJob.querySelector(".fileName").textContent = printJob['filePath'];
            clonedPrintJob.querySelector(".estPrintTime").textContent = `Est. Time: ${printJob['estimatedPrintTime']} min`;
            clonedPrintJob.classList.remove("template");
            printQueueDiv.appendChild(clonedPrintJob);

            // Your Prints queue
            if (printJob['userId'] === userId) {
                clonedPrintJob.style.backgroundColor = "#a9ff87";

                const clonedYourPrint = yourPrintTemplate.cloneNode(true);
                clonedYourPrint.querySelector(".fileName").textContent = printJob['filePath'];
                clonedYourPrint.querySelector(".estPrintTime").textContent = `Est. Time: ${printJob['estimatedPrintTime']} min`;
                clonedYourPrint.querySelector(".print-status").textContent = `Queue Position: ${queuePos}`;
                clonedYourPrint.classList.remove("template");
                yourPrintsDiv.appendChild(clonedYourPrint);
            }

            queuePos++;
        }

        const isQueueEmpty = (data.length===0)
        startNextPrintButton.disabled = isQueueEmpty;
    });
}

startNextPrintButton.addEventListener("click",()=>{
    fetch(`/api/start-next-print`,{
        method: "POST",
    }).then(resp=>{
        if (resp.ok) {
            loadPrintQueues();
            loadCurrentPrint();
        }
    });
});

function loadCurrentPrint() {
    fetch(`/api/get-current-print`).then(async resp=>{
        if (resp.ok) {
            const data = await resp.text()
            if (data.length > 0) {
                return JSON.parse(data);
            }else {
                printerStatusText.textContent = "Idle";
                printerImage.src = "images/frozen_printer.gif";
                printingFileDiv.classList.add("template");
            }
        }
    }).then(printJob=>{
        if (!printJob) return;
        printingFileDiv.querySelector(".fileName").textContent = printJob['filePath'];
        printingFileDiv.querySelector(".estPrintTime").textContent = `Time Remaining: ${printJob['estimatedPrintTime']} min`;
        printingFileDiv.classList.remove("template");
        printerStatusText.textContent = "Printing...";
        printerImage.src = "images/printer.gif";
    });
}