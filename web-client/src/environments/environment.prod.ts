export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080/api',
  templateApiUrl: 'http://localhost:8080/api',
  formApiUrl: 'http://localhost:8080/api',
  userApiUrl: 'http://localhost:8080/api',
  keycloak: {
    issuer: 'http://keycloak:9001',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
