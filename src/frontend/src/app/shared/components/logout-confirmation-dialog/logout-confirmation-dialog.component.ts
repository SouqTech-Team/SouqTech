import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-logout-confirmation-dialog',
  templateUrl: './logout-confirmation-dialog.component.html',
  styleUrls: ['./logout-confirmation-dialog.component.scss']
})
export class LogoutConfirmationDialogComponent {
  title: string = 'Logout';
  materialIcon: string = 'logout';
  content: string = 'Are you sure you want to log out?';
  negativeButton: string = 'No';
  positiveButton: string = 'Yes';

  constructor(
    public dialogRef: MatDialogRef<LogoutConfirmationDialogComponent>,
  ) {}

}
