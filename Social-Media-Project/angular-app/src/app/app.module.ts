import { BrowserModule } from '@angular/platform-browser';
import { NgModule, isDevMode } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms'; // Allows ng data binding

import { AppComponent } from './app.component';
import { SignUpComponent } from './sign-up.component';
import { LoginComponent } from './login.component';

import { appReducer } from '../state/app.reducer'; // Connects to the data store

import { SignupService } from '../services/sign-up.service';
import { NewPostComponent } from './new-post.component';
import { PostCardComponent } from './discussion/posts/post-card/post-card.component';
import { PostPageComponent } from './discussion/posts/post-page/post-page.component';
import { PostsComponent } from './discussion/posts/posts/posts.component';
import { CommentComponent } from './discussion/comments/comment.component';
import { CommentsComponent } from './discussion/comments/comments.component';
import { NewCommentComponent } from './discussion/comments/new-comment.component';

import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { LikesComponent } from './discussion/shared-elements/likes.component';
import { PostSearchComponent } from './discussion/post-search/post-search.component';

import { AppRoutingModule } from './app-routing.module';


@NgModule({
  declarations: [
    AppComponent,
    SignUpComponent,
    LoginComponent,
    NewPostComponent,
    PostsComponent,
    PostCardComponent,
    PostPageComponent,
    CommentComponent,
    CommentsComponent,
    NewCommentComponent,
    LikesComponent,
    PostSearchComponent,
  ],
  imports: [
    BrowserModule, // Required for running Angular applications in a browser environment
    HttpClientModule, // Required for making HTTP requests in your application
    FormsModule, StoreModule.forRoot({}, {}), EffectsModule.forRoot([]), StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: !isDevMode() }),
    StoreModule.forRoot({ app: appReducer }),
    AppRoutingModule
  ],
  providers: [SignupService],
  bootstrap: [AppComponent]
})
export class AppModule { }
