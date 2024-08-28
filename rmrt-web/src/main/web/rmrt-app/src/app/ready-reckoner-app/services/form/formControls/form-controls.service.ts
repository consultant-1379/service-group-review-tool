import { Injectable } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class FormControlsService {

  constructor( ) { }

  //create form controls for each of the inputs for the network element
  createInputControls(formGroup, networkElement) {
    const inputs = networkElement["inputs"];

    //if error during import
    if(formGroup.get('inputs') == null) {
      return;
    }
    const inputsFormGroup = formGroup.get('inputs') as FormGroup;
    Object.keys(inputsFormGroup.controls).forEach(controlName => {
           inputsFormGroup.removeControl(controlName);
    });
    inputs.forEach( (input) => {
      //if imported from json , input key will have a value
      if( input['inputValue'] != undefined ) {
        formGroup.get('inputs').addControl(input["inputKey"], new FormControl(input["inputValue"], {validators: [Validators.required, Validators.min(0)]}));
      } else {
        // no value for input key
        formGroup.get('inputs').addControl(input["inputKey"], new FormControl('', {validators: [Validators.required, Validators.min(0)]}));
      }
    });

  }




  createOutputControls(inputRow: FormGroup, networkElement) {
    //if network element has no outputs
    if( Object.keys(networkElement['outputs']).length == 0 ) {
      return;
    }
    inputRow.addControl('outputs', new FormGroup({}, {updateOn: "submit"})); 
    const inputsFormGroup = inputRow.get('outputs') as FormGroup;
    Object.keys(inputsFormGroup.controls).forEach(controlName => {
      inputsFormGroup.removeControl(controlName);
    });
    inputRow.addControl('outputs', new FormGroup({}, {updateOn: "submit"}));

    Object.keys(networkElement['outputs']).forEach( (output) => {
      const outputControlName = (networkElement['outputs'][output]);

      const outputsFormGroup = <FormGroup>inputRow.get('outputs');

      outputsFormGroup.addControl(outputControlName, new FormControl('', { updateOn: "submit" }));
    });
  }




  getFormRowsByOutput(formRows: FormArray, outputKey) {

    return formRows.controls.filter( (formGroup) =>

      formGroup.get('outputs') != null
      &&
      Object.keys(formGroup.get('outputs').value)[0] == outputKey

    );

  }



  getFormRowsByPreInput(formRows: FormArray, prePopulatedInput: string) {

    return formRows.controls.filter( (formGroup) =>

      formGroup.get('inputPrePopulated') != null
      &&
      Object.keys(formGroup.get('inputPrePopulated').value)[0] == prePopulatedInput

    );

  }




  updateFormOutputValues (formRows, output) {
    formRows.forEach( (row) => {
      row.get('outputs').setValue(output);
    });
  }



  createInputPrePopulatedControls(formRow: FormGroup, networkElement) {

    if( networkElement['inputPrePopulated'].length == 0 ) {
      return;
    }

    formRow.addControl('inputPrePopulated', new FormGroup({}, {updateOn: "submit"}));

    //when the form data is imported, the inputPrePopulated will be in array from
    if(networkElement['inputPrePopulated'] instanceof Array) {
      networkElement['inputPrePopulated'].forEach( (input) => {
        const inputControlName = (input['inputKey']);
        const inputControlValue = (input['inputValue']);

        const inputPrePopulatedFg = <FormGroup>formRow.get('inputPrePopulated');

        inputPrePopulatedFg.addControl(inputControlName, new FormControl(inputControlValue, { validators: Validators.required, updateOn: "submit" }) );
      });

      //inputPrePopulated is an object when selected from the form
    } else if(networkElement['inputPrePopulated']  instanceof Object) {
      Object.values(networkElement['inputPrePopulated']).forEach( (input: string) => {
        const inputPrePopulatedFg = <FormGroup>formRow.get('inputPrePopulated');

        inputPrePopulatedFg.addControl(input, new FormControl('', { validators: Validators.required, updateOn: "submit" }) );
      });
    }


  }

}
