export const environment = {
  production: false,
  apiUrl: 'http://localhost:8000',
  templateApiUrl: 'http://localhost:8082',
  formApiUrl: 'http://localhost:8081',
  keycloak: {
    issuer: 'http://localhost:9001',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
