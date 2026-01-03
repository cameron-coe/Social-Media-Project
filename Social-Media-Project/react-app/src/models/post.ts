import { CommentType } from "./comment";

export type PostType = {
    id: number;
    title: string;
    author: string;
    content: string;
    numOfLikes: number;
    comments: CommentType[];
    likedByUser?: boolean;
    numberOfComments?: number;
}