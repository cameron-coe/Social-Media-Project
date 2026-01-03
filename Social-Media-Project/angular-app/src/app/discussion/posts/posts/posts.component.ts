import { Component, OnInit } from "@angular/core";
import { Observable } from 'rxjs';

import { DataStoreService } from '../../../../services/data-store.service';

@Component({
    selector: 'posts',
    templateUrl: './posts.component.html',
    styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit {
    posts$: Observable<any[]> = new Observable<any[]>(observer => {
        observer.next([]);
    });

    constructor(
        private _dataStoreService: DataStoreService
    ) {}

    ngOnInit() {
        // Subscribe to the posts$ observable to get the value of posts reactively
        this.posts$ = this._dataStoreService.posts$;
    }
}