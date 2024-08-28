import {AfterViewInit, Component, OnInit} from '@angular/core';
import {UserServiceService} from "./services/users/user-service.service";
import {NotificationLog} from "@eds/vanilla";
import {Tree} from "@eds/vanilla/tree/Tree";
import {Layout} from "@eds/vanilla/common/scripts/Layout";

@Component({
  selector: 'app-rmrt-app',
  templateUrl: './rmrt-app.component.html',
  styleUrls: ['./rmrt-app.component.css']
})
export class RmrtAppComponent implements OnInit, AfterViewInit {

  public user: User;

  constructor(private userService: UserServiceService) {
  }

  ngOnInit(): void {
    this.user = new User(undefined);

    this.userService.getLoggedInUser().subscribe(
      data => {
        if(data != null) {
          this.user = new User(data['principal']);
        }
      },
      err => {
        console.log(err);
        NotificationLog.setNotification({
          title: 'Error user information', // String
          description: 'Could not retrieve principal user from session. Please contact system admin if problem persists.', // String
          timestamp: new Date() // Date
        });
      },
      () => console.log("User details loaded.")
    );
  }

  ngAfterViewInit() {

    let treeList = document.querySelectorAll('.appnav .tree.navigation .item');
    treeList.forEach(treeDOM => {
      let tree = new Tree(<HTMLElement>treeDOM);
      tree.init();
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

}

class User {
  public name: string = "Guest";
  public username: string = "Guest";
  public isAdmin: boolean = false;
  public authenticated: boolean = false;

  constructor(userData) {
    if(userData) {
      this.name = userData.name;
      this.username = userData.username;
      this.authenticated = true;
      this.isAdmin = userData.role == "ROLE_ADMIN";
    }
  }
}

