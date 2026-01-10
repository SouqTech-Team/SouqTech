import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorRoutingModule } from './error-routing.module';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { MaterialModule } from 'src/app/material.module';


@NgModule({
  declarations: [
    PageNotFoundComponent
  ],
  imports: [
    CommonModule,
    ErrorRoutingModule,
    MaterialModule,
  ]
})
export class ErrorModule { }
