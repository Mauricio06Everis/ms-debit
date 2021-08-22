package com.example.debit.exception.webclient;

import com.example.debit.util.I18AbleException;

public class ArgumentWebClientNotValid extends I18AbleException {
	public ArgumentWebClientNotValid(String key, Object... args) {
        super(key, args);
    }
}
