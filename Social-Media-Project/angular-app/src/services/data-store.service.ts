import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Comment } from '../models/comment.model';
import { PostService } from "../services/post.service";


@Injectable({
  providedIn: 'root',
})
export class DataStoreService {
  private posts = new BehaviorSubject<any[]>([]);
  private selectedPost = new BehaviorSubject<any>([]);

  // We do not expose Subjects, because it's an anti-pattern, we only expose Observables
  public posts$: Observable<any[]> = this.posts.asObservable();

  private spinner = new BehaviorSubject(false);
  public spinner$: Observable<boolean> = this.spinner;

  constructor(private postService: PostService) {
    // Assign an initial value to the posts variable
    this.loadPosts().subscribe({
      next: response => {
        this.posts.next(response.response); // Normal case
      },
      error: (error) => {
        // Temporary manual parsing with control character handling
        if (error.status === 200 && error.error && typeof error.error.text === 'string') {
          try {
            const sanitizedText = error.error.text.replace(/[\u0000-\u001F\u007F]/g, ''); // Remove control characters
            const parsedResponse = JSON.parse(sanitizedText);
            this.posts.next(parsedResponse.response);  // Parse and update posts
          } catch (e) {
            console.error('Error parsing sanitized response:', e);
          }
        } else {
          console.error('Error in Loading Posts:', error);
        }
      }
    });
  }

  // Set the value of the Posts
  setPosts(newPosts: any[]): void {
    this.posts.next(newPosts);
  }

  // Method to add posts to the existing array
  addPosts(postsToAdd: any[]): void {
    const currentPosts = this.posts.getValue();
    this.posts.next([...currentPosts, ...postsToAdd]);
  }

  // We can have several data pieces in one service, if we need, or split them between services
  setSpinner(value: boolean): void {
    this.spinner.next(value);
  }

  // Get the current value of the posts
  getPosts(): any[] {
    return this.posts.getValue();
  }

  // Loads data from the database
  loadPosts(): Observable<any> {
    return this.postService.getPosts();
  }

  // Method to search for posts with a search term
  searchPosts(searchTerm: string, searchTag: string): void {
    // Show spinner while loading posts
    this.setSpinner(true);
  
    this.postService.getPostsWithSearch({ searchTerm: searchTerm, searchTag: searchTag }).subscribe({
      next: response => {
        // Normal case if parsing succeeds
        this.posts.next(response.response);
        console.log('Search result successful');
        this.setSpinner(false); 
      },
      error: (error) => {
        // Handle potential control characters if status is 200
        if (error.status === 200 && error.error && typeof error.error.text === 'string') {
          try {
            // Remove control characters from the response
            const sanitizedText = error.error.text.replace(/[\u0000-\u001F\u007F]/g, '');
            const parsedResponse = JSON.parse(sanitizedText);
            this.posts.next(parsedResponse.response);  // Update posts with the parsed result
            console.log('Search result successful after sanitizing');
          } catch (e) {
            console.error('Error parsing sanitized response:', e);
          }
        } else {
          // Log the error if it's not related to control characters
          console.error('Error in Searching Posts:', error);
        }
  
        // Hide spinner
        this.setSpinner(false);
      }
    });
  }

  // Set the Selected Post
  public setSelectedPost(id: number, title: string, content: string, numOfLikes: number) {
    const thisPost = {
      id: id,
      title: title,
      content: content,
      numOfLikes: numOfLikes
    };

    this.selectedPost.next(thisPost);
  }

  // Get the Selected Post
  public getSelectedPost(): any {
    return this.selectedPost.getValue();
  }

  // Clears Selected Post
  public clearSelectedPost(): void {
    this.selectedPost.next(null);
  }
}
