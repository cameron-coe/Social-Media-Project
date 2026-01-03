import { Component } from "@angular/core";
import { OnInit } from "@angular/core";
import { PostService } from "../services/post.service";
import { DataStoreService } from '../services/data-store.service';
import { TagService } from "../services/tag.service";
import { ModalService } from "../services/modal.service";

@Component({
    selector: 'new-post',
    templateUrl: './new-post.component.html',
    styleUrls: ['./new-post.component.css']
})

export class NewPostComponent implements OnInit{
    memberId: number = 1;
    postContent: string = '';
    postTitle: string = '';

    postTagOptions = ['All'];
    postTag = 'All';

    constructor(
        private _postService: PostService, 
        private _dataStore: DataStoreService,
        private _tagService: TagService,
        private _modalService: ModalService
    ) {}

    // LifeCycle hooks
    ngOnInit(): void {
        this._tagService.getTags().subscribe({
            next: response => {
                this.postTagOptions = response.response;
                console.log(this.postTagOptions);
            },
            error: error => {
                console.error('Error Loading Tags', error);
            }
        });
    }

    onSubmit(event: Event) {
        event.preventDefault();  // Prevent default form submission

        const postData = {
            memberId: this.memberId,
            postTitle: this.postTitle,
            postContent: this.postContent,
            postTag: this.postTag
        }

        this._postService.newPost(postData).subscribe({
            next: response => {
                console.log('Post Made Successfully!');
                
                // TODO: Add post to data store based on returned data
                // Add the new post to data store (See Reference:)
                //https://stackblitz.com/edit/angular-ivy-jnmfqg?file=src%2Fapp%2Fdata-store.service.ts,src%2Fapp%2Fapp.component.ts,src%2Fapp%2Fapp.module.ts
                this._dataStore.addPosts([{ title: this.postTitle, content: this.postContent }])
                this.postContent = '';
                this.postTitle = '';

                // Close the New Post Modal
                this._modalService.setModal(null);
            },
            error: error => {
                console.error('Posting Failed!', error);
            }
        });
    }
}