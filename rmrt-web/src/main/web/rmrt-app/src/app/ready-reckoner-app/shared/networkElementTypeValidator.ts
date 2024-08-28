import { FormControl, ValidationErrors, ValidatorFn } from "@angular/forms";

/*
  A user will be restricted to submit a form with invalid network element type
  (blank or value that does not exist for network element type). Therefore
  assume that if the network element type is imported and does not exist in the list
  then the network element type has been deprecated
*/

export function networkElementTypeValidator(networkElements: [], importedNetworkElement: boolean): ValidatorFn {
  return (networkElementControl: FormControl): ValidationErrors | null => {

    const networkElementControlValue = networkElementControl.value;

    const foundNetworkElement: string = networkElements.find( (networkElement) =>
      networkElement["networkElementType"] == networkElementControlValue );

    if( foundNetworkElement ) {

      return null;

    } else if( !foundNetworkElement && importedNetworkElement) {

      return { deprecated: true };

    } else if( !foundNetworkElement && !importedNetworkElement) {

      return { networkElementNotDefined: true};

    }
  }

}
