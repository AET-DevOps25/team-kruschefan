export const environment = {
  production: true,
  apiUrl: 'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080',
  templateApiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080',
  formApiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080',
  userApiUrl:
    'http://api-gateway.team-kruschefan-project.svc.cluster.local:8080',
  keycloak: {
    issuer: 'http://keycloak.team-kruschefan.local',
    realm: 'forms-ai',
    clientId: 'angular-frontend',
  },
};
