import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PostsComponent } from './discussion/posts/posts/posts.component';
import { PostPageComponent } from './discussion/posts/post-page/post-page.component';

const routes: Routes = [
  { path: '', component: PostsComponent },  // Default route (homepage)
  { path: 'post/:postId', component: PostPageComponent }, // Route for individual posts
  { path: 'post/:postId/comment/:commentId', component: PostPageComponent }, // Route for individual posts
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })], // Configures the router at the app level
  exports: [RouterModule] // Makes RouterModule available throughout the app
})
export class AppRoutingModule {}
