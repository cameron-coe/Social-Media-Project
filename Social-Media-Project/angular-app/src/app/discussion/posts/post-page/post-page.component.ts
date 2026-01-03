import { Component, OnInit } from "@angular/core";
import { DataStoreService } from "../../../../services/data-store.service";

import { Comment } from "../../../../models/comment.model";
import { CommentService } from "../../../../services/comment.service";
import { PostService } from "../../../../services/post.service";

import { ActivatedRoute, Router } from "@angular/router";

@Component({
    selector: 'post-page',
    templateUrl: './post-page.component.html',
    styleUrls: ['./post-page.component.css']
})
export class PostPageComponent implements OnInit{

    public selectedPost: any;

    private _comments: Comment[] = []; // Todo: maybe delete this later

    public commentThread: Comment[] = [];

    hideComments: boolean = false;

    constructor(
        private _dataStoreService: DataStoreService,
        private _commentService: CommentService,
        private _postService: PostService,
        private _route: ActivatedRoute,
        private _router: Router
    ) {}

    ngOnInit() {
        this.selectedPost = this._dataStoreService.getSelectedPost();

        // Runs if there is no Selected Post
        if (!this.selectedPost.id) {
            console.log("Empty");
            this.loadPost();
            console.log("-->", this.selectedPost);
        }

        this.loadCommentThread();

        // Show post comments by default if there is no comment thread
        if (!this._route.snapshot.paramMap.get('commentId')) {
            this.getPostComments();
        }
    }

    loadCommentThread(): void {
        const commentId = Number(this._route.snapshot.paramMap.get('commentId')); // Extract the Comment ID from the URL

        if (!commentId) {
            return;
        }

        this.hideComments = false;

        this._commentService.getCommentThread(commentId).subscribe({
            next: response => {
                this.commentThread = response.response;
                console.log("----Thread:", this.commentThread);
            },
            error: error => {
                console.error('Error in Loading Comment Thread', error);          
            }
        });

        //this.commentThread = [dsafdsa];
    }

    get getComments(): Comment[] {
        if (this._comments == undefined) {
            return [];
        } else {
            return this._comments;
        }
    }

    getPostComments() {
        const postId = Number(this._route.snapshot.paramMap.get('postId')); // Extract the Post ID from the URL
        this._commentService.getCommentsForPost(postId).subscribe({
            next: response => {
                this._comments = response.response;
            },
            error: error => {
                console.error('Error in Loading Comments', error);          
            }
        });
    }

    addComment(comment: Comment) {
        this._comments.push(comment);
        this.getPostComments();
    }

    toggleHideComments() {
        this.hideComments = !this.hideComments;

        if (this.hideComments) {
            this._comments = [];
        }
        else {
            this.getPostComments();
        }
    }

    private loadPost(): void {
        const postId = Number(this._route.snapshot.paramMap.get('postId')); // Extract the Post ID from the URL
        if (!isNaN(postId)) {
            this._postService.getPost(postId).subscribe({
                next: response => {
                    this.selectedPost = response.response;
                }
            });
        }
    }

    goToPostsPage(): void {
        this._router.navigate([``]);
    }

    getSelectedCommentId(): number {
        const commentId = Number(this._route.snapshot.paramMap.get('commentId')); // Extract the Post ID from the URL
        return commentId;
    }

    getPostId(): number {
        const postId = Number(this._route.snapshot.paramMap.get('postId')); // Extract the Post ID from the URL
        return postId;
    }

    unselectComment(): void {
        const postId = Number(this._route.snapshot.paramMap.get('postId')); // Extract the Post ID from the URL

        //this._router.navigate([`/post/${postId}/comment/${this.currentComment.id}`]);
        window.location.href = `/post/${postId}`;
    }
}