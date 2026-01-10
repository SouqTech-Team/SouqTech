import { Product } from './product.model';

export interface Wishlist {
    id: number;
    products: Product[];
    isPublic: boolean;
    shareToken: string;
}
