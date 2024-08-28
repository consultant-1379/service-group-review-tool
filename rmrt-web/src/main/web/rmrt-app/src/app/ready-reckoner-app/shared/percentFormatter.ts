import { formatNumber } from "@angular/common";

export function formatPercentage(arr: []) {
  arr.forEach( (object) => {
    if( ( isNaN(object['value']) ) ) {
      // @ts-ignore
      object['value'] = 0;
    } else if( (isNaN(object['limit'])) ) {
      // @ts-ignore
      object['limit'] = 0;
    }

    let ratio = object['value'] / object['limit']
    // @ts-ignore
    object['percentage'] = formatNumber(Math.ceil(ratio * 100), 'en-US', '1.0-0');
  })
}
