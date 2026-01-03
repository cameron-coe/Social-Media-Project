export interface AppState {
    posts: Post[];
    authenticationKey: string;
  }
  
  export interface Post {
    id: number;
    title: string;
    content: string;
  }