import {AfterContentChecked, Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-resource-model-view',
  templateUrl: './resource-model-view.component.html',
  styleUrls: ['./resource-model-view.component.css']
})
export class ResourceModelViewComponent implements OnInit, AfterContentChecked {

  @Input() resourceModel;
  @Input() compareModel;
  @Input() deploymentTypes;
  @Input() showZeroDeltas;
  @Input() title;
  @Input() lastUpdated;

  constructor() { }

  ngOnInit(): void {
    if(this.compareModel == undefined) {
      this.compareModel = this.resourceModel;
    }
  }

  ngAfterContentChecked(): void {
    let deltas = Array.prototype.slice.call(document.getElementsByClassName("delta"));

    if(this.resourceModel == this.compareModel) {
      for (let i = 0; i < deltas.length; i++) {
        let element = deltas[i];
        element.classList.add('d-none');
      }
    } else {
      for (let i = 0; i < deltas.length; i++) {
        let element = deltas[i];
        element.classList.remove('d-none');
      }
    }
  }

  dateFormat(lastUpdated) {
    let options = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric' ,
      hour:'numeric',
      minute: 'numeric'
    };
    let date = new Date(lastUpdated);

    return date.toLocaleString(undefined, options);

  }
}
