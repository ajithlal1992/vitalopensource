package com.vtls.opensource.validation;

import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;

/**
 * This interface includes a function to handle validation results
 */
public interface ValidationHandler
{
   /**
    * Handle a set of validation results
    * @param _resources a {@link ValidatorResources} 
    * @param _result a {@link ValidatorResult} containing a set of validation results
    * @return true if the field satisfies the validation rule, otherwise false
    */ 
   public boolean handleValidatorResult(ValidatorResources _resources, ValidatorResult _result);
}
