import { Component, OnInit } from '@angular/core';
import {Donut, Heatmap, LineChart, RadarChart} from "@eds/vanilla";

@Component({
  selector: 'app-home-view',
  templateUrl: './home-view.component.html',
  styleUrls: ['./home-view.component.css']
})
export class HomeViewComponent implements OnInit {

  private donutData = {
    "series": [{"name":"Chrome","values":[2751],"colorID":"1"},{"name":"Edge","values":[732],"colorID":"2"},{"name":"Firefox","values":[666],"colorID":"3"},{"name":"IE","values":[655],"colorID":"4"},{"name":"Other","values":[22],"colorID":"5"}]
  };

  private radarData = {
    "series": [{"name":"Employee 1","values":[4,5,1,3,4,5]},{"name":"Employee 2","values":[1,5,1,5,5,3]},{"name":"Employee 3","values":[3,5,2,2,2,2]}],
    "common": ["Communication","Technical Knowledge","Team Player","Meeting Deadlines","Problem Sovling","Punctuality"]
  }

  private heatMapData:any = {
    "commonX": [100,200,300,400,500,600],
    "commonY": ["Chrome","Safari","Firefox","Samsung Internet","Other"],
    "series": [{"name":"Browser latency","values":[[50,55,60,80,90],[45,30,30,10,5],[5,10,10,5,3],[0,5,0,2,1],[0,0,0,1,0],[0,0,0,0,0]]}]
  };

  constructor() { }

  ngOnInit(): void {
    this.donuts();
    this.radar();
    this.heatMap();
  }

  private donuts() {
    const chart1 = new Donut({
      element: document.getElementById('percentage-donut'),
      data: this.donutData,
      showValue: true,
      unit: 'Visitors'
    });

    const chart2 = new Donut({
      element: document.getElementById('absolute-donut'),
      data: this.donutData,
      showValue: true,
      showAbsoluteValue: true,
      unit: 'Visitors'
    });

    chart1.init();
    chart2.init();

  }
  private radar() {
    const radarChart = new RadarChart({
      element: document.getElementById('radar-chart-multi'),
      data: this.radarData,
      height: 400,
      y: {
        unit: 'Rank'
      },
      tooltip: {
        format: {
          value: '1.0f'
        }
      }
    });

    radarChart.init();
  }
  private heatMap() {
    const chart = new Heatmap({
      element: document.getElementById('number-heatmap'),
      height: 150,
      data: this.heatMapData,
      margin: {
        left: 140
      },
      range: {
        unit: '# People',
        reverse: true
      },
      x: {
        unit: 'Latency [ms]'
      },
      y: {
        unit: 'Browser'
      },
      tooltip: {
        format: {
          value: '.0f'
        }
      },
      palette: 'diverging 1'
    });
    chart.init();
  }
}
