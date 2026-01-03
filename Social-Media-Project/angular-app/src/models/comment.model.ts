export interface Comment {
    id: number;
    content: string;
    nestedComments?: Comment[];
    numOfLikes?: number;
}