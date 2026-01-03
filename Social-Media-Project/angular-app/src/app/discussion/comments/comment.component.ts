import { Component, Input, OnInit } from '@angular/core';
import { Comment } from '../../../models/comment.model';
import { CommentService } from '../../../services/comment.service';
import { ActivatedRoute, Router } from "@angular/router";

@Component({
    selector: 'comment',
    templateUrl: './comment.component.html',
    styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
    @Input() currentComment!: Comment;
    @Input() showComments?: boolean;

    hideNestedComments: boolean = true;

    //private _nestedComments: Comment[] = [];

    constructor(
        private _commentService: CommentService,
        private _route: ActivatedRoute,
        private _router: Router
    ) {}

    ngOnInit(): void {
        if(this.showComments) {
            this.hideNestedComments = false;
            this.getCommentsForComment();
        }
    }


    get getNestedComments(): Comment[] {
        if (this.currentComment.nestedComments == undefined) {
            return [];
        } else {
            return this.currentComment.nestedComments;
        }
    }

    addNestedComment(comment: Comment) {
        // Initialize nestedComments array if it's undefined
        if (!this.currentComment.nestedComments) {
            this.currentComment.nestedComments = [];
        }
        
        this.currentComment.nestedComments.push(comment);
        console.log(this.currentComment.nestedComments);

        this.getCommentsForComment();
    }

    getCommentsForComment() {
        this._commentService.getCommentsForComment(this.currentComment.id).subscribe({
            next: response => {
                this.currentComment.nestedComments = response.response;
            },
            error: error => {
                console.error('Error in Loading Comments'); 
                console.error(error);            
            }
        });
    }

    openThisCommentThread():void {
        const postId = Number(this._route.snapshot.paramMap.get('postId')); // Extract the Post ID from the URL
        const commentId = this.currentComment.id;

        window.location.href = `/post/${postId}/comment/${commentId}`;
    }

    toggleHideNestedComments() {
        this.hideNestedComments = !this.hideNestedComments;

        if (this.hideNestedComments) {
            this.currentComment.nestedComments = [];
        }
        else {
            this.getCommentsForComment();
        }
    }
}
