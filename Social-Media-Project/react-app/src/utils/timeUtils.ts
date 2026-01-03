export function timeAgo(dateString: string): string {
    const now = new Date();
    const postedDate = new Date(dateString);
    const diffMs = now.getTime() - postedDate.getTime();

    const minutes = Math.floor(diffMs / (1000 * 60));
    const hours = Math.floor(diffMs / (1000 * 60 * 60));
    const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    const months = Math.floor(days / 30);
    const years = Math.floor(days / 365);

    if (minutes < 60) {
        return `Posted ${minutes} minute${minutes !== 1 ? "s" : ""} ago`;
    } 
    else if (hours < 24) {
        return `Posted ${hours} hour${hours !== 1 ? "s" : ""} ago`;
    } 
    else if (days < 30) {
        return `Posted ${days} day${days !== 1 ? "s" : ""} ago`;
    } 
    else if (days < 365) {
        return `Posted ${months} month${months !== 1 ? "s" : ""} ago`;
    } 
    else {
        return `Posted ${years} year${years !== 1 ? "s" : ""} ago`;
    }
}
