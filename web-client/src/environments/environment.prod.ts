export const environment = {
  production: true,
  apiUrl: 'http://localhost:8000',
  templateApiUrl: 'http://localhost:8082',
  formApiUrl: 'http://localhost:8081',
  userApiUrl: 'http://localhost:8083',
  keycloak: {
    issuer: 'http://localhost:9001',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
