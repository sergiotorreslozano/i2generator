document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('imageGenForm');
    const panel = document.getElementById('imageGenPanel');
    const loader = document.getElementById('imageLoader');
    const imageElement = document.getElementById('imagePreview');
    const generatedImageDiv = document.getElementById('generatedImage');
    const statusElement = document.getElementById('status');
    const createdTimeElement = document.getElementById('createdTime');
    const promptTextElement = document.getElementById('promptText');

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        const formData = new FormData(form);

        // Show panel and loader
        panel.style.display = "block";
        loader.style.display = "block";
        generatedImageDiv.style.display = "none";

        // Submit the form via AJAX
        fetch('/api/simpleimagegen', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            // Populate panel with image data
            createdTimeElement.textContent = new Date(data.createdTime).toLocaleString();
            promptTextElement.textContent = data.prompt;
            statusElement.textContent = data.status;

            // Poll the server to check for the image
            pollForImage(data.imageId);
        });
    });

    function pollForImage(imageId) {
        const pollInterval = setInterval(function () {
            fetch(`/api/simpleimagegen/status/${imageId}`)
                .then(response => response.json())
                .then(data => {
                    // Update status in the panel
                    statusElement.textContent = data.status;

                    if (data.status === 'GENERATED') {
                        // Stop polling once the image is ready
                        clearInterval(pollInterval);

                        // Display the generated image
                        loader.style.display = "none";
                        generatedImageDiv.style.display = "block";
                        imageElement.src = 'data:image/jpeg;base64,' + data.image;
                    }
                });
        }, 3000); // Poll every 3 seconds
    }

    // Handle panel close button
    document.getElementById('closePanelBtn').addEventListener('click', function () {
        panel.style.display = "none";
        form.reset();
    });
});
