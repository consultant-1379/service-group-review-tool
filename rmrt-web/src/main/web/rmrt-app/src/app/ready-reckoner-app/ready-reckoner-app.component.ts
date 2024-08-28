import { AfterViewInit, Component, OnInit } from '@angular/core';
import { NotificationLog } from "@eds/vanilla";
import { Layout } from "@eds/vanilla/common/scripts/Layout";
import { Tree } from "@eds/vanilla/tree/Tree";
import { AppComponentStateService } from "./services/appState/app-state.service";
import { UserServiceService } from "../rmrt-app/services/users/user-service.service";
import { Router } from "@angular/router";
import { FormActionsService } from "./services/form/formActions/form-actions.service";

@Component({
  selector: 'app-ready-reckoner-app',
  templateUrl: './ready-reckoner-app.component.html',
  styleUrls: ['./ready-reckoner-app.component.css']
})
export class ReadyReckonerAppComponent implements AfterViewInit, OnInit {
  title = 'ENM Dimensioning Ready Reckoner';


  public contentView;
  public user;

  constructor(private userService: UserServiceService,
              private appStateService: AppComponentStateService,
              private formActionsService: FormActionsService,
              private router: Router)
  {

  }

  ngOnInit(): void {
    this.user = {
      name: "Guest",
      authenticated: false,
    };

    this.appStateService.state$.subscribe( (data) => this.contentView = data);
  }

  ngAfterViewInit() {
    const tree = new Tree(document.querySelector('.tree.navigation'));
    tree.init();
    let treeList = document.querySelectorAll('.appnav .tree.navigation .item');
    treeList.forEach(clicked => {
      clicked.addEventListener('click', () => {
        clicked.closest('ul')
          .querySelector(".active")
          .classList.remove("active");
        clicked.classList.add("active");
      });
    });
    const layout = new Layout(document.querySelector('body'));
    layout.init();

    NotificationLog.init();
  }


  toggleStyle() {
    let body = document.getElementsByTagName('body')[0];
    body.classList.toggle("light");
    body.classList.toggle("dark");
  }


  triggerFormSubmission() {
    this.formActionsService.toggleFormSubmit(true);
  }


  goToInputs() {
    this.router.navigate(['readyReckoner']);
  }


  triggerImport() {
    this.formActionsService.triggerImportData();
  }


  triggerExport() {
    this.formActionsService.toggleExportData(true);
  }

}
