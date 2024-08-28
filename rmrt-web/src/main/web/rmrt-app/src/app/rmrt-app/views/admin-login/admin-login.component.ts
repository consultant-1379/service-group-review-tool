import { Component, OnInit } from '@angular/core';
import {NotificationLog, SignIn} from "@eds/vanilla";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent implements OnInit {
  title = "RMRT - Login";
  public error;
  public success = false;

  constructor() {}

  ngOnInit(): void {

    const signin = new SignIn({
      element: document.querySelector('.signin'),
      loggedInUrl: '/'
    });

    signin.init();
  }

  submit() {

    const formData = new FormData();
    const inputUsername:HTMLInputElement = document.querySelector("#username");
    const inputPassword:HTMLInputElement = document.querySelector("#password");

    formData.append('username', inputUsername.value);
    formData.append('password', inputPassword.value);

    fetch('/view/login', {
      method: 'POST',
      body: formData
    })
      .then(response => response)
      .then(result => {
        console.log('Success:', result);
        if(result.url.includes("?error")) {
          this.error = "Incorrect sign in details. Please try again."
        } else {
          this.error = null;
          this.success = true;
          setTimeout(()=> location.replace(result.url), 3000)
        }
      })
      .catch(error => {

        console.error('Error:', error);
      });
  }
}
