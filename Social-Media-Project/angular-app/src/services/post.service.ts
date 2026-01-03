import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) { }

  newPost(postData: { memberId: number, postContent: string, postTag: string }): Observable<any> {
    // Retrieve the token from sessionStorage inside the method
    const token = sessionStorage.getItem('authToken');

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}` // Assuming Bearer token, adjust if needed
    });

    return this.http.post<any>('http://localhost:8081/new-post', postData, { headers });
  }

  getPosts(): Observable<any> {
    return this.http.get<any>('http://localhost:8081/get-posts');
  }

  getPost(postId: number): Observable<any> {
    return this.http.get<any>(`http://localhost:8081/get-post/${postId}`);
  }

  getPostsWithSearch(postData: { searchTerm: string, searchTag: string }): Observable<any> {
    return this.http.post<any>('http://localhost:8081/get-posts-with-search', postData);
  }
}