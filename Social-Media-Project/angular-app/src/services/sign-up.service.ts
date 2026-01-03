import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SignupService {

  private apiUrl = 'http://localhost:8081/sign-up';

  constructor(private http: HttpClient) { }

  signUp(userData: { username: string, password: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, userData);
  }
}