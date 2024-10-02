function openModal(imageId) {
    console.log ("modal6 " + imageId);
    // Find the modal element by ID or class (adjust as needed)
    var imageModal = document.getElementById("image-modal");
    console.log ("image-modal" + imageModal);
    // Set the image inside the modal (replace this with actual modal content)
    var modalImage = document.getElementById("modal-image");
    console.log("modal-image " + modalImage);
    var clickedImage = document.getElementById("image-" + imageId).src;
    console.log ("clickedImage" + clickedImage);
    modalImage.src = clickedImage;

   setDownloadUrl(imageId);


    // Show the modal
    imageModal.classList.add("is-active");
}

// Separate function to set the download URL for the modal
function setDownloadUrl(imageId) {
    const downloadButton = document.getElementById('modal-download-button');
    // Set the download button URL using the API download endpoint
    downloadButton.href = `/api/images/download/${imageId}`;  // Dynamic URL for downloading
}

// Close modal logic (if needed)
function closeModal() {
    var modal = document.getElementById("image-modal");
    modal.classList.remove("is-active");
}
