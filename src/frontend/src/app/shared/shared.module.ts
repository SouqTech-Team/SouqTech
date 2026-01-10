import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatusComponent } from './components/status/status.component';
import { MaterialModule } from '../material.module';
import { LogoutConfirmationDialogComponent } from './components/logout-confirmation-dialog/logout-confirmation-dialog.component';

@NgModule({
  declarations: [StatusComponent, LogoutConfirmationDialogComponent],
  imports: [CommonModule, MaterialModule],
  exports: [StatusComponent, LogoutConfirmationDialogComponent],
})
export class SharedModule {}
