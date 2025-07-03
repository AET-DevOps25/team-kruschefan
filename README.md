# FormsAI

FormsAI is a tool de
A form generating platform using AI

### Architecture Overview
Below are the UML diagrams for the project for an overview of the project architecture.

**Use Case Diagram**  
<img src="/resources/UML/use_case_diagram.png" alt="Use Case Diagram" style="height: 20vw;"/>

**Top Level Architecture**  
<img src="/resources/UML/top_level_architecture.png" alt="Top Level Architecture" style="height: 25vw;"/>

**Subsystem Decomposition**  
<img src="/resources/UML/subsystem_decomposition.png" alt="Subsystem Decomposition" style="height: 25vw;"/>

**Sequence Diagram (for GenAI usage)**  
<img src="/resources/UML/sequence_diagram.png" alt="Sequence Diagram" style="height: 18vw;"/>

Microservices: in services

### Usage

### Local Setup

### Deployment

### CI/CD Pipeline

### Monitoring
TODO

### Team
Our team consists of three members, each focus on and is responsible for different part of the development.

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



