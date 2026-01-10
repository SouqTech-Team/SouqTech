export type Mode = 'signin' | 'signup';

export interface SignInRequest {
    email: string;
    password: string; 
}

export interface SignUpRequest {
    email: string;
    name: string;
    surname: string;
    password: string; 
}

export interface User {
    email: string;
    name: string;
    surname: string;
}

export interface SignInResponse {
    token: string;
    // ...
}

export interface SignUpResponse {
    email: string;
    // ...
}


