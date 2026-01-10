import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from "@angular/forms";

export const equalFieldsValidator = (...fields: string[]): ValidatorFn => {
    return (abstractControl: AbstractControl): ValidationErrors | null => {
        const areEquals = fields
        .map(fieldName => abstractControl.get(fieldName))
        .filter(Boolean)
        .every((formControl, _, array) => formControl?.value == array[0]?.value);

        return areEquals ? null : { equalFields: true }
    }
};