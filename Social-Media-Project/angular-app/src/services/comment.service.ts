import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class CommentService {

    constructor (private http: HttpClient) {}

    private newCommentForPostApiUrl = 'http://localhost:8081/new-comment-to-post';
    newCommentForPost(commentData: { memberId: number, content: string, parentPostId: number }): Observable<any> {
        // Retrieve the token from sessionStorage inside the method
        const token = sessionStorage.getItem('authToken');

        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}` // Assuming Bearer token, adjust if needed
        });

        return this.http.post<any>(this.newCommentForPostApiUrl, commentData, { headers });
    }

    private newCommentForCommentApiUrl = 'http://localhost:8081/new-comment-to-comment';
    newCommentForComment(commentData: { memberId: number, content: string, parentCommentId: number }): Observable<any> {
        // Retrieve the token from sessionStorage inside the method
        const token = sessionStorage.getItem('authToken');

        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}` // Assuming Bearer token, adjust if needed
        });

        return this.http.post<any>(this.newCommentForCommentApiUrl, commentData, { headers });
    }

    private getCommentsForPostApiUrl = 'http://localhost:8081/get-comments-for-post';
    getCommentsForPost(postId: number): Observable<any> {
        return this.http.get<any>(`${this.getCommentsForPostApiUrl}/${postId}`);
    }

    private getCommentsForCommentApiUrl = 'http://localhost:8081/get-comments-for-comment';
    getCommentsForComment(commentId: number): Observable<any> {
        return this.http.get<any>(`${this.getCommentsForCommentApiUrl}/${commentId}`);
    }

    private getCommentThreadApiUrl = 'http://localhost:8081/get-comment-thread';
    getCommentThread(commentId: number): Observable<any> {
        return this.http.get<any>(`${this.getCommentThreadApiUrl}/${commentId}`);
    }
}