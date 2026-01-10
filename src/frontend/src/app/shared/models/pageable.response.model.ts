export interface PageableResponse<T> {
    list: T[];
    total: number;
}