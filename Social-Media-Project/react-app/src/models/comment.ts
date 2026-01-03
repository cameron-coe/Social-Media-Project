export type CommentType = {
    id: number;
    author: string;
    content: string;
    numOfLikes: number;
    createdOn: string;
    replies: CommentType[];
    likedByUser?: boolean;
}