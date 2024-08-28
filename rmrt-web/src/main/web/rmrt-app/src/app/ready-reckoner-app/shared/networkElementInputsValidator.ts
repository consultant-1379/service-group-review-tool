import { FormGroup, ValidationErrors, ValidatorFn } from "@angular/forms";

//when form is imported, if inputs are not created then there was an error
//processing inputs data, due to user changing names of input load drivers
//or the inputs are imported from previous release where they are now deprecated

export const validateNetworkElementInputs: ValidatorFn =
  (networkInputRow: FormGroup): ValidationErrors | null => {

    if(networkInputRow.get('inputs') == null) {
      return {processingInputsError: true}
    } else {
      return null;
    }

}

