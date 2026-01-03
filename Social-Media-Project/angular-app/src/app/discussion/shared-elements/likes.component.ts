import { Component } from '@angular/core';

@Component({
    selector: 'likes',
    templateUrl: './likes.component.html',
    styleUrls: ['./likes.component.css']
})
export class LikesComponent {
    numOfLikes: number = 0;
    isLiked: boolean = false;

    toggleLike() {
        if (this.isLiked) {
        this.numOfLikes--;
        } else {
        this.numOfLikes++;
        }
        this.isLiked = !this.isLiked;
    }
}
