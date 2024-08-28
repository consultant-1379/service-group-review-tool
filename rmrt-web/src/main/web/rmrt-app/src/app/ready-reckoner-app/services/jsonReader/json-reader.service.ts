import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class JsonReaderService {

  getJsonContent(file: File) {
    return file.text();
  }


  parseJson(data) {
    let formRows = [];
    let jsonObject = JSON.parse(data);
    let formInputRows = jsonObject["inputs"];

    if(formInputRows == undefined) {
      return null;
    }


    for(let i = 0; i < formInputRows.length ; ++i) {
      let formRow = {
        inputs: []
      };

      formRow["networkElementType"] = formInputRows[i]["networkElementType"];

      let neInputsArray = formInputRows[i]["inputs"];

      //ensure that data imported is of consistent format
      if(!(neInputsArray instanceof Array)) {
        return null;
      }

      for(let i = 0; i < neInputsArray.length; ++i) {
        const inputObject = neInputsArray[i];

        //check the imported object has the required keys and values for keys
        if(
            ( inputObject["label"] != undefined)
              &&
            ( inputObject["inputKey"] != undefined)
              &&
            ( inputObject["inputValue"] != undefined)
          )
          {
            formRow["inputs"].push(inputObject);
          } else {
            return null;
          }
      }

      if(formInputRows[i]['outputs'] != undefined) {
        formRow['outputs'] = formInputRows[i]['outputs'];
      }

      if(formInputRows[i]['inputPrePopulated'] != undefined) {
        formRow['inputPrePopulated'] = formInputRows[i]['inputPrePopulated'];
      }

      formRows.push(formRow);
    }

    return formRows;
  }
}
