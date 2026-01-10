export interface Review {
  id: number;
  rating: number;
  comment: string;
  userUsername: string; // Simplifié pour l'affichage
  isVerifiedPurchase: boolean;
  helpfulCount: number;
  createdAt: string;
}

export interface ReviewRequest {
  rating: number;
  comment: string;
}
