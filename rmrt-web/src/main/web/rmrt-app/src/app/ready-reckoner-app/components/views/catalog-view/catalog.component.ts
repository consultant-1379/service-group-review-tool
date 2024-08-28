import { Component, OnInit } from '@angular/core';
import { AppComponentStateService } from "../../../services/appState/app-state.service";
import { NetworkElementsService } from "../../../services/networkElements/network-elements.service";
import { NotificationLog, Select } from '@eds/vanilla';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog-view.component.css']
})
export class CatalogComponent implements OnInit {
  public lastRelease;
  public supportedNETypes;
  public domains = [];
  public selectedOption;
  public filter_domains_NE = [];


  constructor(private appStateService: AppComponentStateService,
              private networkElementsService: NetworkElementsService) { }

  ngOnInit(): void {
    setTimeout( () => {
      this.appStateService.changeState('catalog');
    }, 0);


    this.supportedNETypes = this.networkElementsService.getSupportedNETypes()
      .subscribe(
        (data) => {
          this.supportedNETypes = data;
        },
       (error) => {
         NotificationLog.setNotification({
           title: 'Error fetching data', // String
           description: 'An error has occurred while retrieving the data. Please contact system administrator if problem persists',
           timestamp: new Date() // Date
         });
       },
      () => {
          this.getDomainOptions(this.initDomainSelect);
          this.filter_domains_NE = this.getAllSupportedNE();
          this.lastRelease = this.supportedNETypes["latestRelease"];
      });
  }




  getDomainOptions(initSelect) {
    Object.keys(this.supportedNETypes["domains"]).forEach( (domain) => {
      this.domains.push(domain);
    });

    initSelect();
  }


  initDomainSelect() {
    const selectDOM = document.querySelector('#domain-select');

    if(selectDOM) {
      const selectElement = <HTMLElement> selectDOM;
      const select = new Select(selectElement);

      select.init();
    }
  }


  onSelectOption(option) {
    this.selectedOption = option;

    if(this.selectedOption == 'all') {
      this.filter_domains_NE = this.getAllSupportedNE();
    } else {
      this.filter_domains_NE = this.supportedNETypes["domains"][`${this.selectedOption}`];
    }
  }


  getAllSupportedNE() {
    let allSupportedNE = [];

    //iterate through all domains and merge all arrays
    Object.keys(this.supportedNETypes.domains).forEach( (domain) => {
      allSupportedNE = allSupportedNE.concat(this.supportedNETypes.domains[`${ domain }`]);
    });

    return allSupportedNE;
  }
}
