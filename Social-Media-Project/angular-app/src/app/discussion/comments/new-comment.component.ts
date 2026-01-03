import { Component, Output, EventEmitter, Input } from '@angular/core';
import { Comment } from '../../../models/comment.model';
import { CommentService } from '../../../services/comment.service';

@Component({
  selector: 'new-comment',
  templateUrl: './new-comment.component.html',
  styleUrls: ['./new-comment.component.css']
})
export class NewCommentComponent {
  @Input() parentPostId: number | null = null;
  @Input() parentCommentId: number | null = null;

  @Output() commentAdded = new EventEmitter<Comment>();
  
  memberId: number = 1;
  isCommenting: boolean = false;
  commentContent: string = '';

  constructor(
    private _commentService: CommentService
  ) {}

  toggleCommenting() {
    this.isCommenting = !this.isCommenting;
  }

  submitComment() {
    if (this.commentContent.trim()) {

      if (this.parentPostId) {
        this.makeNewCommentOnPost();
      } 
      else if (this.parentCommentId) {
        this.makeNewCommentOnComment();
      }
      else {
        // Make an error message
      }

      // Reset the form
      this.commentContent = '';
      this.isCommenting = false;
    }
  }

  makeNewCommentOnPost() {
    const commentData = {
      memberId: Number(sessionStorage.getItem('memberId')),
      content: this.commentContent,
      parentPostId: this.parentPostId || -1  // Set to -1 if null because this shouldn't be called if it's null
    };

    this._commentService.newCommentForPost(commentData).subscribe({
      next: response => {
        console.log('Comment Made Successfully!');
        this.commentAdded.emit(response);
      },
      error: error => {
        console.error('Comment Failed');
      }
    });
  }

  makeNewCommentOnComment() {
    const commentData = {
      memberId: Number(sessionStorage.getItem('memberId')),
      content: this.commentContent,
      parentCommentId: this.parentCommentId || -1 // Set to -1 if null because this shouldn't be called if it's null
    };

    this._commentService.newCommentForComment(commentData).subscribe({
      next: response => {
        console.log('Comment Made Successfully!');
        this.commentAdded.emit(response);
      },
      error: error => {
        console.error('Comment Failed');
      }
    });
  }

}
