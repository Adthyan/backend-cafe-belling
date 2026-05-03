export const environment = {
  production: true,
  apiBaseUrl: 'https://backend-cafe-belling.onrender.com',
  // apiBaseUrl: 'http://localhost:8080',
  adminUser: 'admin',
  adminPassword: 'admin',
};

export function adminAuthorizationHeader(): string {
  return 'Basic ' + btoa(`${environment.adminUser}:${environment.adminPassword}`);
}