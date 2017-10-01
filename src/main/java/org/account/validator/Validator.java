package org.account.validator;

import org.account.exception.InvalidParameterException;
import org.account.exception.RequiredParameterException;

public interface Validator<T> {

    void validate(T entity) throws RequiredParameterException, InvalidParameterException;
}
