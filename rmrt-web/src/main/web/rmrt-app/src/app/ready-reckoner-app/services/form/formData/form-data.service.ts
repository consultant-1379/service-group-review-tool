import {Injectable} from '@angular/core';
import {AbstractControl, FormArray, FormGroup} from "@angular/forms";
import {FormControlsService} from "../formControls/form-controls.service";

@Injectable({
  providedIn: 'root'
})
export class FormDataService {

  constructor(private formControlsService: FormControlsService) { }

  //Get the data submitted from each row of the form
  gatherFormData(form, networkElementList) {
    const formData = [];

    for(let formGroup of form.get('networkElementSearchRows').controls) {
      const inputRow = {
        networkElementType: "",
        inputs: [],
      };

      inputRow['networkElementType'] = formGroup.get('networkElementType').value;

      //find the network element type from the list
      const networkElementType = networkElementList.find((ne) => ne["networkElementType"] == inputRow['networkElementType']);

      //get the inputs
      const inputs = networkElementType['inputs'];

      inputs.forEach((input) => {

        const inputLabel = input['inputLabel'];
        const controlName = input['inputKey'];
        const controlValue = formGroup.get('inputs').get(controlName).value;

        inputRow['inputs'].push(
          {
            label: inputLabel,
            inputKey: controlName,
            inputValue: controlValue
          });
      });

      //get the outputs if any
      if(networkElementType['outputs'] != null ) {
        inputRow['outputs'] = networkElementType['outputs'];
      }


      if( networkElementType['inputPrePopulated'] != undefined ) {
        inputRow['inputPrePopulated'] = [];

        const inputPrePopulated = networkElementType['inputPrePopulated'];

        Object.keys(inputPrePopulated).forEach( (input) => {
          const prePopulatedInputLabel = input;
          const prePopulatedInputKey = inputPrePopulated[input];
          const prePopulatedInputValue = formGroup.get('inputPrePopulated').get(prePopulatedInputKey).value;

          inputRow['inputPrePopulated'].push(
            {
              label: prePopulatedInputLabel,
              inputKey: prePopulatedInputKey,
              inputValue: prePopulatedInputValue
            });
        });
      }

      formData.push(inputRow);
    }

    return formData;
  }




  getSubmissionContents(formArray: FormArray) {
    const submitObject = {};

    //send the inputs and their respective values
    for(let formRow of formArray.controls) {
      const inputsObject = formRow.get('inputs').value;

      Object.keys(inputsObject).forEach( (inputKey) => {
        if(submitObject[inputKey] != undefined) {
          return;
        }

        submitObject[inputKey] = inputsObject[inputKey];
      });


      //also submit pre populated inputs
      if( formRow.get('inputPrePopulated') != null ) {
        const prePopulatedInputObj = formRow.get('inputPrePopulated').value;

        Object.keys(prePopulatedInputObj).forEach( (prePopulatedInput) => {
          if(submitObject[prePopulatedInput] != undefined) {
            return;
          }

          submitObject[prePopulatedInput] = prePopulatedInputObj[prePopulatedInput];
        });
      }

    }

    return submitObject;
  }





  //update the request body for evaluating the output load drivers in the form
  updateOutputRequest(formRows, outputKey) {

    //if form has no more rows
    if(formRows.length == 0) {
      return;
    }

    return {
      outputs: new Array(outputKey),
      inputs: this.getFormRowInputs(formRows)
    };
  }


  /*
    Make sure the inputs for the network element imported are up to date.

   */
  checkUpToDateInputs(networkElements, inputRowData): boolean {

    //search the network element in the input
    const supportedNetworkElement = this.findNetworkElementByType(networkElements, inputRowData["networkElementType"]);

    if(supportedNetworkElement) {
      const supportedNetworkElementInputs: [] = supportedNetworkElement["inputs"];

      let supportedInputKeys = [];

      supportedNetworkElementInputs.forEach( (input) => {
        supportedInputKeys.push(input['inputKey']);
      });


      if(supportedInputKeys.length != inputRowData["inputs"].length) {
        return false;
      }

      //check if any of the imported inputs are non existent
      for(let input of inputRowData["inputs"]) {
        if( !(supportedInputKeys.includes(input["inputKey"]) )) {
          return false;
        }
      }


      //if code reaches this point then the inputs are valid
      return true;
    }

    //not found network element
    return false;
  }


  //check the imported network element outputs against the supported network element outputs
  checkUpToDateOutput(networkElements: [], inputRowData): boolean {

    //get the supported network element
    const supportedNetworkElement = this.findNetworkElementByType(networkElements, inputRowData["networkElementType"]);

    if(supportedNetworkElement == null) {
      return false;
    }

    //output load drivers to be checked against
    const supportedNeOutputs = Object.values(supportedNetworkElement["outputs"]);

    //get the output load drivers of the import
    const importOutputs = Object.values(inputRowData["outputs"]);

    if(supportedNeOutputs.length != importOutputs.length) {
      return false;
    }

    for(let i = 0; i < importOutputs.length; ++i) {
      if( supportedNeOutputs.includes(importOutputs[i]) == false ) {
        return false;
      }
    }

  }



  findNetworkElementByType(networkElements, networkElementType) {
    //search the network element in the input
    const supportedNetworkElement = networkElements.find((networkElement) => networkElement["networkElementType"] == networkElementType);

    if (supportedNetworkElement) {
      return supportedNetworkElement;
    } else {
      return null;
    }
  }




  //create a request body for api to evaluate the output fields' value,
  //inputKey and inputValue are defined when user edits any of the input load drivers
  createOutputRequestObject(formRows: FormArray, editedFormRow: FormGroup, inputKey?, inputValue?) {

    //no need to create an output request if form row being edited has no output value
    if( editedFormRow.get('outputs') == null ) {
      return;
    }

    //only create request when all inputs of the row have been filled in to reduce number of requests to API
    for(const inputValue of Object.values(editedFormRow.get('inputs').value)) {
      if(inputValue === "") {
        return;
      }
    }

    //format of the request body of evaluating the output
    let requestBody = {
      outputs: [],
      inputs: {}
    }

    //if user has edited any of the inputs, then update the form control accordingly
    if(inputKey && inputValue) {
      editedFormRow.get('inputs').get(inputKey).setValue(Number(inputValue));
    }

    requestBody['outputs'] =  Object.keys(editedFormRow.get('outputs').value);

    //get the output key of the form row that has been edited
    let outputKey = requestBody['outputs'][0];

    //get the other rows in the form that share the same output key
    const formRowsByOutputKey = this.formControlsService.getFormRowsByOutput(formRows, outputKey);

    for(const formRow of formRowsByOutputKey) {

      let inputValues = formRow.get('inputs').value;

      for( const [inputKey, value] of  Object.entries(inputValues) ) {

        if(value == "" || value == undefined || ( isNaN(<number>value)) ) {
          continue;
        }

        requestBody["inputs"][inputKey] = value;
      }
    }


    return requestBody;

  }



  /*
  * request object for inputPrePopulated will be an empty inputs object and an outputs array
  * with the inputPrePopulated load driver
  * */
  createOutputRequestForInputPre(formRow: FormGroup) {

    if(formRow.get('inputPrePopulated') == null) {
      return;
    }

    let requestBody = {
      outputs: [],
      inputs: {}
    }

    Object.keys(formRow.get('inputPrePopulated').value).forEach( (preInputLoadDriver) => {
      requestBody["outputs"].push(preInputLoadDriver);
    });

    return requestBody;
  }


  //check the imported network element and ensure the inputPrePopulated fields are up to date
  checkUpToDatePrePopulatedInput(networkElements, inputRowData) {

    //get the supported network element
    const supportedNetworkElement = this.findNetworkElementByType(networkElements, inputRowData["networkElementType"]);

    if(supportedNetworkElement == null) {
      return false;
    }

    //inputPrePopulated load drivers to be checked against
    let supportedNeInputsPrePopulated = Object.values(supportedNetworkElement["inputPrePopulated"]);

    //get the inputPrePopulated load drivers of the import
    let importNeInputsPrePopulated = [];

    inputRowData["inputPrePopulated"].forEach( (input) => {
      importNeInputsPrePopulated.push(input["inputKey"]);
    });

    if(supportedNeInputsPrePopulated.length != importNeInputsPrePopulated.length) {
      return false;
    }

    for(let i = 0; i < importNeInputsPrePopulated.length; ++i) {
      if( supportedNeInputsPrePopulated.includes(importNeInputsPrePopulated[i]) == false ) {
        return false;
      }
    }


  }



  getFormRowInputs(formRows: FormArray) {
    let inputs = {};

    // @ts-ignore
    for(const formRow of formRows) {

      //inputs
      let rowInputValues = formRow.get('inputs').value;

      for( const [inputKey, value] of  Object.entries(rowInputValues) ) {

        if(value == "" || value == undefined || ( isNaN(<number>value)) ) {
          continue;
        }

        inputs[inputKey] = value;
      }
    }

    return inputs;
  }

}
