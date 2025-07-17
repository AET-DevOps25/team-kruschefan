# FormsAI Web Client

This is the web client for FormsAI, a platform for creating and managing forms with AI capabilities.
It provides a user-friendly interface for managing templates, forms, and responses.

## Features

- Create, edit, and delete templates and forms using drag and drop
- AI-assisted form generation based on user prompts
- Manage forms and their responses
- User authentication and authorization
- Integration with Angular Material for UI components

## Getting Started

In web-client, do these steps to get started:

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the development server:
   ```bash
   ng serve
   ```
3. Run `docker compose up keycloak api-gateway` to start the Keycloak server and other services like GenAI and form service.
4. Open your browser and navigate to `http://localhost:4200`.
5. You can log in with the following credentials:
   - Username: `mock-user`
   - Password: `mock-user-secret`

## Workflow

After logging in, you can navigate through the application using the menu bar. The main features include:

- **Editor**: Create and editing templates and forms with/without AI assistance.
- **User Management**: View and manage created templates and forms and view form responses.
- **Profile**: View and edit your profile information.

To get started with creating a form, you can use the AI feature by providing a prompt in the editor. The AI will generate a form based on your input, which you can then customize further. Users can also create forms manually by adding questions by dragging them from the sidebar. Every question has a settings button that allows you to configure the default value, placeholder, options and other properties.

After creating a form, you can save it as a template for future use or export it directly. The submitted forms can be viewed in the User Management section, where you can also see the responses to each form.

## Development

This project is built using [Angular](https://angular.dev/), [RxJS](https://rxjs.dev/guide/overview), and [Angular Material](https://material.angular.dev/). The code is organized into components, services, and interfaces. Each component is built standalone conforming to Angular's best practices. Rxjs is used for handling asynchronous operations such as API calls and event handling. Angular Material is used for all UI components to ensure a consistent and responsive design and handles accessibility by default.

This project uses keycloak for authentication and authorization. Keycloak is configured using the Keycloak-angular and Keycloak-js libraries. The Keycloak service is injected in app.config.ts and any unauthorized access is handled by `guard/auth.guard.ts`.

## AI Integration

The AI capabilities are integrated through the `gen-ai.service.ts` service, which interacts with the backend AI service to generate forms based on the given prompt. The AI service not only generates questions but also provides the appropriate types for each question and options if any. The AI-generated forms can be saved as templates or exported directly as a form for future use.

## Known Issues

- The AI service may not always generate the expected form structure, especially for complex prompts.
- Some UI components may not be fully responsive on smaller screens.
- The user management component currently does not support pagination for large datasets.
- Submission and creation dates are currently hardcoded to the current date in the user management component. This will be updated to reflect actual submission dates in future releases.
