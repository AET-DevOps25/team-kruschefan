export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  templateApiUrl: 'http://localhost:8080',
  formApiUrl: 'http://localhost:8080',
  userApiUrl: 'http://localhost:8080',
  keycloak: {
    issuer: 'http://keycloak:9001',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
