import { createSelector, createFeatureSelector } from '@ngrx/store';
import { AppState } from './app.state';

export const selectAppState = createFeatureSelector<AppState>('app');

export const selectPosts = createSelector(
  selectAppState,
  (state: AppState) => state.posts
);

export const selectAuthenticationKey = createSelector(
  selectAppState,
  (state: AppState) => state.authenticationKey
);