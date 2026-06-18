export interface TrackingResponseDTO {
  id: string;
  name: string;
  url: string;
  targetPrice: number;
  active: boolean;
}

export interface TrackingRequestDTO {
  name: string;
  url: string;
  targetPrice: number;
}
