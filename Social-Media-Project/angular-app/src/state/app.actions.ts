import { createAction, props } from '@ngrx/store';
import { Post } from './app.state';

export const loadPosts = createAction('[Posts] Load Posts');

export const startup = createAction('[App] Startup');

export const loadPostsSuccess = createAction(
  '[Posts] Load Posts Success',
  props<{ posts: Post[] }>()
);

export const addPost = createAction(
  '[Posts] Add Post',
  props<{ post: Post }>()
);

export const setAuthenticationKey = createAction(
  '[Auth] Set Authentication Key',
  props<{ key: string }>()
);