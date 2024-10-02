// menu-navigation.js

// Function to switch between fragments
function switchContent(option) {
    // Hide all fragments
    document.getElementById("generate-image-fragment").classList.add("is-hidden");
    document.getElementById("edit-image-fragment").classList.add("is-hidden");
    document.getElementById("list-images-fragment").classList.add("is-hidden");

    // Switch to show the selected fragment
    switch (option) {
        case 'generate':
            document.getElementById("generate-image-fragment").classList.remove("is-hidden");
            break;
        case 'edit':
            document.getElementById("edit-image-fragment").classList.remove("is-hidden");
            break;
        case 'list':
            document.getElementById("list-images-fragment").classList.remove("is-hidden");
            break;
        default:
            document.getElementById("generate-image-fragment").classList.remove("is-hidden");
    }

    // Set active menu item
    setActiveMenuItem(option);
}

// Function to highlight the active menu item
function setActiveMenuItem(option) {
    // Remove 'is-active' from all menu items
    document.getElementById("menu-generate").classList.remove("is-active");
    document.getElementById("menu-edit").classList.remove("is-active");
    document.getElementById("menu-list").classList.remove("is-active");

    // Add 'is-active' to the selected menu item
    switch (option) {
        case 'generate':
            document.getElementById("menu-generate").classList.add("is-active");
            break;
        case 'edit':
            document.getElementById("menu-edit").classList.add("is-active");
            break;
        case 'list':
            document.getElementById("menu-list").classList.add("is-active");
            break;
    }
}

// Load the default fragment (Generate Image) when the page first loads
document.addEventListener("DOMContentLoaded", function() {
    switchContent('generate');
});
