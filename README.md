# FormsAI

FormsAI is an intelligent, modular tool designed to simplify the creation and customization of dynamic forms. Using modern web technologies, FormsAI enables users to generate structured form interfaces, using the provided web interface by the application, where users can create forms by using drag and drop. Users can also opt to use the GenAI function, where they submit a prompt, using which the GenAI will automatically create the forms for them. Upon publishing forms, user can gather responses from sending the survey link to other users.

This documentation records the system design of the application, how to use and deploy the application as well as the development team behind the application.

## Architecture Overview

Below are the UML diagrams for the project for an overview of the project architecture.

1. **Use Case Diagram**     
   <img src="/resources/UML/use_case_diagram.png" alt="Use Case Diagram" style="height: 25vw;"/>    
   This use case diagram shows the main interactions users have with FormsAI. Form Creators can create, view, delete, and share forms. They can also use the AI-powered form generation feature to generate forms. Form Accessers can view and fill out forms that have been shared with them.

---

2. **Top Level Architecture**     
   <img src="/resources/UML/top_level_architecture.png" alt="Top Level Architecture" style="height: 40vw;"/>    
   This diagram gives a overview of the entire FormsAI platform. At the core, the Application is made up of a Server that routes requests through an API Gateway to several microservices. Among them, GenAI relies on the OpenUI API for text generation, which is then forwarded to form service. Each of these components interacts with databases and handles distinct aspects of the application's logic. The client then interacts with the back end and displays the UI. Authentication is managed through Keycloak, while CI/CD ensures seamless deployment. Monitoring tools like Prometheus, AlertManager, and Grafana keep everything observable and reliable.

---

3. **Subsystem Decomposition**   
   <img src="/resources/UML/subsystem_decomposition.png" alt="Subsystem Decomposition" style="height: 30vw;"/>    
   This diagram breaks down the architecture of FormsAI into key subsystems. The Client interacts with the backend via an API Gateway, which handles routing, authentication, and token checks. The backend is divided into multiple microservices, including User, Form, Template, and GenAI (Langchain). Keycloak handles authentication and user management, while Monitoring (via Prometheus & Grafana) and CI/CD pipelines (via GitHub Actions) ensure integrity and automation of the system.

---

4. **Sequence Diagram (for GenAI usage)**  
   <img src="/resources/UML/sequence_diagram.png" alt="Sequence Diagram" style="height: 25vw;"/>    
   The diagram shows how a user prompt becomes a full form. The Form Service sends the prompt to the GenAI Service, which uses RAG and looks up similar examples from the Vector DB. It then uses an LLM API to generate a form based on the prompt and context. The generated form must be in JSON format is sent back through the services to be parsed and shown to the user.

---

> Other architecture details are documented in separate README files. Here is a list of them:
>
> - [Microservices](./services)
> - [GenAI Usage](./services/GenAI)
> - [Web Client](./web-client)
> - [Infrastructure](./infra)

## App Usage

After deploying the app, the app can be used on the web interface using a browser. The deployment methods are well documented below. In this section,

1. **Log in**
   Log in the app using a mock user

   ```bash
   username: mock-user
   password: mock-user-secret
   ```

2. **Form Editor**
   In the Form Editor section, users can create a form using GenAI or drag and drop functionality. After creating the form, users can either save the form as template, or immediately export the form.

3. **User Management**
   In the User Management section, users can view all the forms that are created by the user. Users have the option to edit the form or export it. Underneath there is the submitted forms section, where users can view all the submitted surveys filled by other users.

4. **Profile**
   In the Profile section, users can view their profile, edit them and update them.

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
   #  Postgresql config
   KC_DB_USERNAME= # Keycloak username for connection to Postgres
   KC_DB_PASSWORD= # Keycloak password for connection to Postgres
   POSTGRES_USER=
   POSTGRES_PASSWORD=
   POSTGRES_URI= # default: jdbc:postgresql://postgres:5432/forms-ai
   POSTGRES_DB= # default: forms-ai

   # MongoDB config
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
   
   The following environment variables are optional (have default values provided), but recommended to be set:

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
   KEYCLOAK_MOCK_ADMIN=mock-admin
   KEYCLOAK_MOCK_ADMIN_PASSWORD=
   KEYCLOAK_MOCK_ADMIN_EMAIL=
   KEYCLOAK_MOCK_ADMIN_FIRST_NAME=
   KEYCLOAK_MOCK_ADMIN_LAST_NAME=
   
   # MongoDB Config
   MONGO_INITDB_ROOT_USERNAME=
   MONGO_INITDB_ROOT_PASSWORD=
   MONGO_INITDB_DATABASE= # default: forms-ai
   ```
   
   In production it is important to set the mock user data to a secure value, so that the mock user cannot be used to access the application. The mock user is only used for local development and testing purposes.

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

5. **Visit the website**
   The app can be found under the following endpoint:

   ```
   http://localhost:4200
   ```

6. **Shut the application down**
   For shutting down the application, use the docker-compose down command in the project root.
   ```bash
   docker-compose down
   ```

## Kubernetes Deployment

Apart from the local deployment, two cloud deployment variants are provided using CI/CD pipelines implemented in GitHub actions. The Kubernetes is deployed through helm (.infra/helm) onto the rancher Kubernetes cluster provided by TUM (https://rancher.ase.cit.tum.de) under the namespace "team-kruschefan-project". Ingress is applied, so that users can access the running instance by configuring local DNS resolution via the /etc/hosts file as described below.

1. **Set the local network hostnames**
   Open the following file locally:

   ```
   /etc/hosts
   ```

   and add the lines at the botton of the file

   ```
   # Kubernetes network set up for team-kruschefan-project
   131.159.88.14 team-kruschefan.local keycloak.team-kruschefan.local
   ```

2. **Visit the website**
   The app can be found under the following endpoint:
   ```
   http://team-kruschefan.local
   ```

## CI/CD Pipeline

There are two CI/CD pipelines, all instantiated using GitHub actions. Both of them are used for deployments, one for cloud and one for Kubernetes.

**EC2 Deploy**
The EC2 deploy pipeline is developed as file [ec2-deploy.yml](.github/workflows/ec2-deploy.yml). The objective of the pipeline is to develop the local application to an AWS EC2 instance, so that the application can be hosted on the cloud. The pipeline contains the following stages:

1. **Checkout & Configure**

   - Retrieves the latest code from the repository
   - Sets up necessary configurations including OpenAPI

2. **Testing**

   - Perform tests to make sure the integrity of the development
   - Tests of microservices, GenAI services and client will be executed

3. **Terraform Deploy**

   - Deploy Terraform to the AWS instance
   - Terraform brings up the EC2 instance where the app will be running
   - Terraform init, Terraform plan and apply

4. **Ansible Deploy**

   - Deploy Ansible playbook
   - Ansible configures the EC2 instance and brings the app to life

5. **Post-Deployment Validation**
   - Verifies successful deployment
   - Optionally runs smoke tests or health checks

**K8s Deploy**
The EC2 deploy pipeline is developed as file [k8s-deploy.yml](.github/workflows/k8s-deploy.yml). The objective of the pipeline is to develop the local infrastructure to the Kubernetes cluster hosted by TUM, so that container orchestration can be performed. The pipeline contains the following stages:

1. **Checkout & Configure**

   - Retrieves the latest code from the repository
   - Sets up necessary configurations including OpenAPI

2. **Testing**

   - Perform tests to make sure the integrity of the development
   - Tests of microservices, GenAI services and client will be executed

3. **Configure Kubernetes**

   - Set up the Kubernetes-related infrastructure, including Kubectl, Helm and GHCR
   - Configure kubectl so that it can retrieve the correct context
   - Configure GHCR, where the docker containers will be pushed

4. **Build Docker Images and Push to Container Registry**

   - Go through all Docker images that needs to be built and build them
   - Pushes built images for later use in the cluster

5. **Create Kubernetes Secrets**

   - Create secrets that needs to be used for Kubernetes deployment

6. **Deploy to Kubernetes**

   - Applies Kubernetes manifests to the TUM cluster
   - Uses kubectl and helm charts with context configured via GitHub Secrets
   - Applies Ingress configuration to the Kubernetes cluster

7. **Post-Deployment Validation**
   - Verifies successful deployment
   - Optionally runs smoke tests or health checks

> Tip: These pipelines are triggered on push to `main`, or manually via the GitHub Actions UI.

## Monitoring

We use **Prometheus** to collect detailed metrics from each of our services, and **Grafana** helps us visualize these metrics through insightful dashboards. This setup is great for keeping track of how well everything's performing in real-time.

- **Prometheus Dashboard**: `http://localhost:9090`
- **Grafana Dashboard**: `http://localhost:3001` (Default login: `admin`/`admin`)

### Grafana Dashboards

The following Grafana dashboards are available for monitoring different aspects of the microservices:

| Dashboard Name                       | Description                                                                                                                                                            |
| ------------------------------------ | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Spring Security Auth Flows Dashboard | Provides insights into the authentication and authorization processes within Spring Security, tracking durations and counts of security events.                        |
| Prometheus Self-Monitoring Dashboard | Monitors the health and performance of the Prometheus server itself, including scrape duration, samples scraped, and configuration reloads.                            |
| Microservices Surveillance Dashboard | Offers an overview of various microservices, tracking key metrics for overall system health and performance, including API Gateway, User, Form, and Template services. |
| JVM Health Dashboard                 | Monitors the health and performance of the Java Virtual Machine across services, including memory usage, garbage collection, and thread counts.                        |
| GenAI Service Dashboard              | Tracks specific metrics related to the GenAI service, such as LLM request counts, failures, and payload sizes.                                                         |
| Garbage Collection Dashboard         | Focuses on JVM garbage collection activity, showing pause durations, live data size after GC, and allocated memory.                                                    |
| App Performance Dashboard            | Provides general application performance metrics, including HTTP request duration sums.                                                                                |

### Prometheus Alert Rules

Prometheus is configured with the following alert rules to notify about critical service states and resource usage:

- **Service Availability Alerts**: These alerts trigger if a core service becomes unreachable.

  - **FormServiceDown**: A `critical` alert that fires if the `form-service` has been unreachable for more than 30 seconds, indicating that users may not be able to create or submit forms.
  - **TemplateServiceDown**: A `critical` alert that fires if the `template-service` has been unreachable for more than 30 seconds, indicating issues with managing form templates.

- **Resource Usage Alerts**: These alerts monitor the resource consumption of services to prevent performance bottlenecks.
  - **HighMemoryUsage**: A `warning` alert that triggers if the `user-service` consumes more than 100MB of resident memory for over 1 minute. This helps identify potential memory leaks or inefficient resource utilization.

## Team

Our team consists of three members, each focus on and is responsible for different part of the development. A tutor is assigned to the team to help manage the project. The exact progress of the application development can be found under this Confluence page: https://confluence.aet.cit.tum.de/pages/viewpage.action?pageId=258581347&spaceKey=DO25WR&title=Team%2BKruschefan

**Stefan Dimitrov**  
Stefan worked diligently on the microservices design and architecture, having implemented a large portion of microservices. He plays a crucial role in the integration of the front end and the back end of the application, while maintaining the configurations on the application side. On the other hand, he helped deploy the app as well, having mainly developed the Terraform-Ansible code to deploy the app on the cloud and also contributed to local and Kubernetes deployment, as well as the monitoring system.

_Responsibilities: Microservices development, Application integration, Database setup, Cloud/Local deployment, Monitoring, CI/CD, ..._

**Vishavjeet Ghotra (Vishav)**  
Vishav is mainly responsible for the client/front-end development of the project. He designed the UI of the client side, which gives users a clean and. He also implemented the front-end logic and integrated it with the microservices, creating a seamless interaction between both sides. Besides, he is also involved in microservices development, consultation of architecture design, monitoring, testing and CI/CD (for code linting), making sure that the user experience is top-notch.

_Responsibilities: Client/Front-end development, microservices development, Database setup, Monitoring, Testing, CI/CD, ..._

**Chengjie Zhou (Jay)**  
Jay is the solution architect of the team, mainly contributing to the DevOps side. He created the docker system to allow the app to be locally deployed, deployed and orchestrated the app up to Kubernetes by writing the Helm charts, developed the CI/CD pipeline, and helped develop the Terraform script for cloud deployment. Apart from that, he is involved in microservices development, having developed the user authentication system and integrated GenAI system. He also set up the testing suite and the monitoring suite.

_Responsibilities: Solution architect, GenAI integration, Local/Kubernetes/Cloud deployment, Terraform deployment, User authentication suite, Testing, Monitoring, CI/CD, ..._

**Julian Gassner**  
As a tutor, Julian is actively involved in project management and organizing meetings. He consulted the team with best practices and development plans and helped maintain the development repository.

> For questions or feedback, feel free to open an issue or contact us via email.

## License

This project is licensed under the MIT License.  
See the [LICENSE](./LICENSE) file for details.

## Acknowledgements

- This project is the assignment for the course DevOps in SS25 on TUM, Technical University of Munich.
- Thanks to Prof. Dr. Stephan Krusche, Prof. Dr. Ingo Weber, Julian Gassner, the chair of Applied Education Technologies and TUM for the DevOps lecture and tutorials.
