import {Component, Input, OnInit} from '@angular/core';
import {Dialog} from "@eds/vanilla";
import {FileUploadService} from "../../services/validation/file-upload.service";

@Component({
  selector: 'app-file-upload-dialog',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  @Input() private repository;
  public error;
  private dialog: Dialog;

  constructor(private fileUploadService:FileUploadService) {}

  ngOnInit(): void {
    const dialogDOM = document.getElementById('file-upload-dialog');

    if (dialogDOM) {
      this.dialog = new Dialog(dialogDOM);
      this.dialog.init();
    }
  }

  submit() {
    let loading_icon = document.getElementById("validation-loading");
    loading_icon.classList.add("loading");
    let fileInput:HTMLInputElement = <HTMLInputElement>document.getElementById("file-input");
    let file = fileInput.files.item(0);

    if(file) {

      const reader = new FileReader();
      reader.onload = (e) => {
        const text = reader.result.toString().trim();

        this.fileUploadService.postFile(text, this.repository.project).subscribe(
          data => {
            console.log(data);
            this.repository.changeRequests.unshift(data);
            this.error = undefined;
            this.dialog.hide();
          },
          error => {
            console.log(this.error);

            let err = error["responseJSON"].error;
            let columnNumber = err.columnNumber;
            let lineNumber = err.lineNumber;
            let errorMessage = "<p>Error found in XML</p>";
            if (columnNumber || lineNumber) {
              errorMessage += `<p>Line Number: ${lineNumber}, Column Number: ${columnNumber}</p>`;
            }
            this.error = errorMessage;

          },
          () => {
            loading_icon.classList.remove("loading");
            console.log("Submitted file for validation")
          }
        );

      }
      reader.readAsText(file);

    } else {
      this.error = "No File Present";
    }
  }



}
