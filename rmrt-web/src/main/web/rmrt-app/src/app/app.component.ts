import { Component, ViewEncapsulation} from '@angular/core';

@Component({
  selector: 'app-root',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './app.component.html',
  styleUrls: [
    '../../node_modules/@eds/vanilla/eds.min.css',
    './app.component.css'
  ]
})

export class AppComponent {
}
