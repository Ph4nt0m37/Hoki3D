const printQueueDiv = document.getElementById("print-queue-div");
const printJobTemplate = document.getElementById("print-job-template");
const addPrintButton = document.getElementById("add-print-button");
const fileUploadBox = document.getElementById("stlUpload");

loadPrintQueue();

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
        loadPrintQueue();
    });
})


function loadPrintQueue() {
    printQueueDiv.replaceChildren();
    fetch("/api/get-print-queue").then(resp=>{
        if (resp.ok) return resp.json();
    }).then(data=>{
        console.log(data)
        for (const printJob of data) {
            const clonedPrintJob = printJobTemplate.cloneNode(true);
            clonedPrintJob.querySelector(".fileName").textContent = printJob['filePath'];
            clonedPrintJob.querySelector(".estPrintTime").textContent = `Est. Time: ${printJob['estimatedPrintTime']} min`;
            clonedPrintJob.classList.remove("template");
            printQueueDiv.appendChild(clonedPrintJob);
        }
    });
}