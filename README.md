# AI Image Generation & Editing Playground

Welcome to the **AI Image Generation & Editing Playground**, a Spring Boot-based application that allows users to generate images using Artificial Intelligence and apply image masks to edit those generated images. This project leverages OpenAI's powerful AI models to provide an experimental, yet exciting environment to explore AI-driven image creation and manipulation.

---

## Key Features

- **AI-Powered Image Generation**: Create images from textual prompts using cutting-edge AI technology.
- **Image Editing with Masks**: Apply custom image masks to edit specific parts of the generated images.
- **User-Friendly Interface**: View and interact with generated images through a web interface at `localhost:9090/views/`.
- **Persistent H2 Database**: The application uses a self-contained H2 database that persists data between runs, allowing you to maintain a history of generated and edited images.

---

## Prerequisites

Before you start, ensure you have the following:

- **Java 17+**: The application is built using Java 17 or higher. Please make sure you have the correct Java version installed.
- **OpenAI API Key**: This project integrates with OpenAI for image generation, and requires an API key. You can obtain an API key by signing up at [OpenAI's official site](https://beta.openai.com/signup/).
- **Maven**: The project uses Maven for dependency management and running the application.

---

## Installation and Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/sergiotorreslozano/i2generator.git

2. **Configure OpenAI API Key**:  
You'll need to pass your OpenAI API key when running the application. It is required to generate images using AI.

---

## Running the Application
To run the application, use the following Maven command, replacing your-api-key with your actual OpenAI API Key:
   ```bash
   cd image-generator
   mvn spring-boot:run -DOPENAI_API_KEY=your-api-key  
   ```
Once the application is running, you can access the image generation and editing interface by navigating to:
   
   ```
   http://localhost:9090/views/
   ```
--- 

## Persistent Database
This project uses an embedded H2 database for storing data related to the generated and edited images. The database is persistent, meaning the data will not be lost between application restarts. No additional setup is required for the database, and all database files are self-contained within the project.

---

## Limitations
- **Not Production-Ready**: This project is intended as a playground for AI image generation and editing. It is not ready for deployment in a production environment as is.  
- **OpenAI API**: Since this project depends on OpenAI's API, ensure that your usage is in line with OpenAI's API limits and pricing structure.

---

## Future Improvements
- Implement more advanced image editing features.
- Enhance security and performance for production deployment.
- Expand AI capabilities to support other models and more advanced features.
- Move away from storing the images in H2
---

## Contributing
We welcome contributions to the project! Feel free to submit issues or pull requests on the GitHub repository.
