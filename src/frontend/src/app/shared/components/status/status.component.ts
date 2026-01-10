import { Component, Input } from '@angular/core';
import { Status } from '../../models/status.model';

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.scss']
})
export class StatusComponent {
  @Input() status: Status = 'initial';
}