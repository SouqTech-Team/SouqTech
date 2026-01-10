import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private duration = 1500;
  // private verticalPosition = 'bottom';
  // private horizontalPosition = 'end';

  constructor(private matSnackBar: MatSnackBar) {}

  show(message: string, action: string = ''): void {
    this.matSnackBar.open(message, action, { duration: this.duration });
  }
}

// remove(toast: ToastInfo) {
//   this.toasts = this.toasts.filter(t => t != toast);
// }

// clear() {
// 	this.toasts.splice(0, this.toasts.length);
// }}
