import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
  @Input() brandName!: string;
  private year = new Date().getFullYear();

  project = 'SouqTech Platform';
  copyrightLine1 = `Fait avec ❤️ en Tunisie © ${this.year}.`;
  copyrightLine2 = 'Tous droits réservés.';
}
