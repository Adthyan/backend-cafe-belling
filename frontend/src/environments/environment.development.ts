export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080',
  adminUser: 'admin',
  adminPassword: 'admin',
};

export function adminAuthorizationHeader(): string {
  return 'Basic ' + btoa(`${environment.adminUser}:${environment.adminPassword}`);
}
