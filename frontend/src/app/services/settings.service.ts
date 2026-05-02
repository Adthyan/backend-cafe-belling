import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment, adminAuthorizationHeader } from '../../environments/environment';
import { ShopSettings } from '../models/api.types';

@Injectable({ providedIn: 'root' })
export class SettingsService {
  private readonly base = `${environment.apiBaseUrl}/api/settings`;

  constructor(private readonly http: HttpClient) {}

  get(): Observable<ShopSettings> {
    return this.http.get<ShopSettings>(this.base);
  }

  patch(body: ShopSettings): Observable<ShopSettings> {
    const headers = new HttpHeaders({
      Authorization: adminAuthorizationHeader(),
      'Content-Type': 'application/json',
    });
    return this.http.patch<ShopSettings>(this.base, body, { headers });
  }
}
