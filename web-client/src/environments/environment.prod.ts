export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080/genai',
  templateApiUrl: 'http://localhost:8080/template',
  formApiUrl: 'http://localhost:8080/form',
  userApiUrl: 'http://localhost:8080/user',
  keycloak: {
    issuer: 'http://localhost:9001',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
