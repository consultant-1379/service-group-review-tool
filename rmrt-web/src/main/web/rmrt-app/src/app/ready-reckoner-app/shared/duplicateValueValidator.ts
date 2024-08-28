import { FormArray, FormControl, ValidatorFn } from "@angular/forms";

export function validateDuplicates(): ValidatorFn {
  return (formArray:FormArray) => {
    let alreadySelected = [];

    //add the error to network element control if its value is already selected
    for(let i = 0; i < formArray.controls.length ; ++i) {
      const networkElementTypeValue = formArray.at(i).get('networkElementType').value;

      if(networkElementTypeValue == '') {
        return;
      }

      if(alreadySelected.includes(networkElementTypeValue)) {
        formArray.at(i).get('networkElementType').setErrors({duplicateValue: 'Network element already selected'});
        return;
      } else {
        alreadySelected.push(networkElementTypeValue);
      }
    }

    return null;
  }
}


//called when a search row has been removed, finds the next occurrence of
//the network element and removes its error
export function findAndRemoveDuplicateError(formControl: FormControl) {
  const formRows = <FormArray> formControl.parent;
  const networkElementType = formControl.get('networkElementType').value;

  for(let i = 0; i < formRows.controls.length ; ++i) {
    const selectedNE = formRows.at(i).get('networkElementType');

    if(selectedNE.value == networkElementType) {
      if(formRows.at(i).get('networkElementType').hasError('duplicateValue')) {
        formRows.at(i).get('networkElementType').setErrors(null);
        return;
      }
    }
  }
}
