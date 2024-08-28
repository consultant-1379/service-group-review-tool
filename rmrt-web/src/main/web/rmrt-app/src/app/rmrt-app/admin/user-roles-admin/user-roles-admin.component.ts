import {Component, OnInit} from '@angular/core';
import {Title} from "@angular/platform-browser";
import {NotificationLog} from "@eds/vanilla";
import {UserServiceService} from "../../services/users/user-service.service";

@Component({
  selector: 'app-user-roles-admin',
  templateUrl: './user-roles-admin.component.html',
  styleUrls: ['./user-roles-admin.component.css']
})
export class UserRolesAdminComponent implements OnInit {

  public users;

  constructor(private userService: UserServiceService,
              private titleService: Title) {
    this.titleService.setTitle("RMRT Admin - User Roles");

  }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers() {
    this.userService.getAllUsers().subscribe(
      data => {
        this.users = data
      },
      err => {
        NotificationLog.setNotification({
          title: 'Error retrieving data', // String
          description: 'Could not retrieve users from database. Please contact system admin.', // String
          timestamp: new Date() // Date
        });
        console.log(err);
      },
      () => console.log("All users loaded.")
    );
  }

  submit(user) {
    let confirmed = confirm("Are you sure you want to change the access role for: " + user.name + "?")
    if (confirmed) {
      if (user.role == "ROLE_USER") {
        user.role = "ROLE_ADMIN";
      } else {
        user.role = "ROLE_USER";
      }

      this.userService.updateUser(user).subscribe(
        data => {
          NotificationLog.setNotification({
            title: user.name, // String
            description: 'Has been updated to: ' + user.role, // String
            timestamp: new Date() // Date
          })
        },
        error => {
          NotificationLog.setNotification({
            title: user.name, // String
            description: 'Error updating user.', // String
            timestamp: new Date() // Date
          })
        },
        () => {
          console.log(user.name);
          this.getUsers();
        }
      );
    }

  }

}
