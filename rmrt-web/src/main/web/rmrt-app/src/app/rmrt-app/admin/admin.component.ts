import { Component, OnInit } from '@angular/core';
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  constructor(private titleService:Title) {
    this.titleService.setTitle("RMRT - Admin");
  }

  ngOnInit(): void {
  }

}
