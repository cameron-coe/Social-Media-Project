import { Component, Input } from '@angular/core';
import { DataStoreService } from '../../../../services/data-store.service';
import { Router } from '@angular/router';

@Component({
    selector: 'post-card',
    templateUrl: './post-card.component.html',
    styleUrls: ['./post-card.component.css']
})

export class PostCardComponent {
    @Input() id!: number;
    @Input() title: string = '';
    @Input() content: string = '';
    @Input() numOfLikes: number = 1;

    constructor(
        private _dataStore: DataStoreService,
        private _router: Router
    ) {}

    onPostClick(): void {
        this._dataStore.setSelectedPost(
            this.id,
            this.title,
            this.content,
            this.numOfLikes
        );

        this._router.navigate([`/post/${this.id}`]);
    }

}