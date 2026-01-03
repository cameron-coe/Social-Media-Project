import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class TagService {
    constructor(private http: HttpClient) { }

    getTags(): Observable<any> {
        return this.http.get<any>('http://localhost:8081/tags')
    }


}



