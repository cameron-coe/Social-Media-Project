import { createReducer, on } from '@ngrx/store';
import { loadPostsSuccess, addPost, setAuthenticationKey } from './app.actions';
import { AppState } from './app.state';

import { PostService } from '../services/post.service';

export const initialState: AppState = {
  posts: [],
  authenticationKey: ''
};

export const appReducer = createReducer(
  initialState,
);