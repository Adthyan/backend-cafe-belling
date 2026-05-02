import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment, adminAuthorizationHeader } from '../../environments/environment';
import { MenuItem, MenuItemWrite } from '../models/api.types';

@Injectable({ providedIn: 'root' })
export class MenuService {
  private readonly base = `${environment.apiBaseUrl}/api/menu-items`;

  constructor(private readonly http: HttpClient) {}

  private adminHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: adminAuthorizationHeader(),
      'Content-Type': 'application/json',
    });
  }

  listActive(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.base}/active`);
  }

  listAll(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(this.base, { headers: this.adminHeaders() });
  }

  get(id: number): Observable<MenuItem> {
    return this.http.get<MenuItem>(`${this.base}/${id}`, { headers: this.adminHeaders() });
  }

  create(body: MenuItemWrite): Observable<MenuItem> {
    return this.http.post<MenuItem>(this.base, body, { headers: this.adminHeaders() });
  }

  update(id: number, body: MenuItemWrite): Observable<MenuItem> {
    return this.http.put<MenuItem>(`${this.base}/${id}`, body, { headers: this.adminHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`, { headers: this.adminHeaders() });
  }
}
