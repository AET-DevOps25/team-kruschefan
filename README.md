# FormsAI

FormsAI is a tool de
A form generating platform using AI

## Architecture Overview
Below are the UML diagrams for the project for an overview of the project architecture.

**Use Case Diagram**  
<img src="/resources/UML/use_case_diagram.png" alt="Use Case Diagram" style="height: 20vw;"/>

**Top Level Architecture**  
<img src="/resources/UML/top_level_architecture.png" alt="Top Level Architecture" style="height: 25vw;"/>

**Subsystem Decomposition**  
<img src="/resources/UML/subsystem_decomposition.png" alt="Subsystem Decomposition" style="height: 25vw;"/>

**Sequence Diagram (for GenAI usage)**  
<img src="/resources/UML/sequence_diagram.png" alt="Sequence Diagram" style="height: 18vw;"/>

**Microservices**
The exact documentation of the microservices can be found in the [README](./services/README.md) file in the folder of services, where the microservice design, introduction and GenAI usage are discussed.

## Local Deployment
The docker system is developed, so that the application can be started within a few commands. Here are the exact steps of how to start the application locally.

1. **Clone the repository**
   Execute the following commands:
   ```bash
   git clone https://https://github.com/AET-DevOps25/team-kruschefan
   cd team-kruschefan
   ```

2. **Set up OpenAPI config**
   The OpenAPI configuration is currently stored inside the [api](./api) folder and needs to be cloned for all microservices. Execute the following command in the project root:
   ```bash
   ./api/scripts/setup.sh
   ```

3. **Set up environment variables**
   Environment variables need to be set for the application to behave normally, since the docker-compose file require these environment variables for the services. First, set all the environment variables in a file called _.env.secret_ in the project root. The following environment variables are required:
   ```env
   # Keycloak Config
   KEYCLOAK_USER=
   KEYCLOAK_PASSWORD=
   KEYCLOAK_ADMIN=
   KEYCLOAK_ADMIN_PASSWORD=
   KEYCLOAK_FORMSAI_USER=
   KEYCLOAK_FORMSAI_PASSWORD=
   KEYCLOAK_MOCK_USER=
   KEYCLOAK_MOCK_USER_PASSWORD=
   KEYCLOAK_MOCK_USER_EMAIL=
   KEYCLOAK_MOCK_USER_FIRST_NAME=
   KEYCLOAK_MOCK_USER_LAST_NAME=

   #  Postgresql config
   KC_DB_USERNAME= # Keycloak username for connection to Postgres 
   KC_DB_PASSWORD= # Keycloak password for connection to Postgres 
   POSTGRES_USER=
   POSTGRES_PASSWORD=
   POSTGRES_URI= # default: jdbc:postgresql://postgres:5432/forms-ai
   POSTGRES_DB= # default: forms-ai

   # MongoDB config    
   MONGO_INITDB_ROOT_USERNAME=
   MONGO_INITDB_ROOT_PASSWORD=
   MONGO_INITDB_DATABASE= # default: forms-ai
   MONGO_USERNAME=
   MONGO_PASSWORD=
   MONGO_DATABASE= # default: forms-ai
   MONGO_URI= # default: mongodb://mongo:mongo-secret@localhost:27017

   # GitHub credentials
   GHCR_USERNAME=
   GHCR_EMAIL=
   GITHUB_TOKEN=

   # OpenWebUI credential
   OPENUI_API_KEY=
   ```

   **Remember to delete all comments and blank rows in the template.**

   After setting up the environment variables, simply execute [sourcelocal.sh](sourcelocal.fish) in the project root. This shell script will inject all environment variables inside the .env.secret file into the working shell.
   ```bash
   source sourcelocal.sh
   ```

4. **Deployment using docker-compose**
   After the set up of the configurations above, the docker-compose file can be executed to start the application. Simply execute the following command in the project root.
   ```bash
   docker-compose up --build
   ```
   The application will be started in a short while.  
  
5. **Shut the application down**
   For shutting down the application, use the docker-compose down command in the project root.
   ```bash
   docker-compose down
   ```

## Cloud/Kubernetes Deployment

## CI/CD Pipeline

## Monitoring
TODO

## Team
Our team consists of three members, each focus on and is responsible for different part of the development. The exact progress of the application development can be found under this Confluence page: https://confluence.aet.cit.tum.de/pages/viewpage.action?pageId=258581347&spaceKey=DO25WR&title=Team%2BKruschefan

**Stefan Dimitrov**  
Stefan worked diligently on the microservices design and architecture, having implemented a large portion of microservices. He plays a crucial role in the integration of the front end and the back end of the application, while maintaining the configurations on the application side. On the other hand, he helped deploy the app as well, having mainly developed the Terraform-Ansible code to deploy the app on the cloud and also contributed to local and Kubernetes deployment, as well as the monitoring system.

_Responsibilities: Microservices development, Application integration, Database setup, Cloud/Local deployment, Monitoring, CI/CD, ..._

**Vishavjeet Ghotra (Vishav)**  
Vishav is mainly responsible for the client/front-end development of the project. He designed the UI of the client side, which gives users a clean and. He also implemented the front-end logic and integrated it with the microservices, creating a seamless interaction between both sides. Besides, he is also involved in microservices development, consultation of architecture design, monitoring, testing and CI/CD (for code linting), making sure that the user experience is top-notch.

_Responsibilities: Client/Front-end development, microservices development, Database setup, Monitoring, Testing, CI/CD, ..._


**Chengjie Zhou (Jay)**  
Jay is the solution architect of the team, mainly contributing to the DevOps side. He created the docker system to allow the app to be locally deployed, deployed and orchestrated the app up to Kubernetes by writing the Helm charts, developed the CI/CD pipeline, and helped develop the Terraform script for cloud deployment. Apart from that, he is involved in microservices development, having developed the user authentication system and integrated GenAI system. He also set up the testing suite and the monitoring suite.

_Responsibilities: Solution architect, GenAI integration, Local/Kubernetes/Cloud deployment, Terraform deployment, User authentication suite, Testing, Monitoring, CI/CD, ..._

!!! chmod



