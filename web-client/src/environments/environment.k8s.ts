export const environment = {
  production: true,
  apiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080/api',
  templateApiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080/api',
  formApiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080/api',
  userApiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080/api',
  keycloak: {
    issuer: 'http://keycloak.team-kruschefan.local',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
