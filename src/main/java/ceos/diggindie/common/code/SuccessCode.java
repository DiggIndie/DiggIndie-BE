package ceos.diggindie.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements Code {

    GET_SUCCESS(200, "GET_SUCCESS"),
    LOGIN_SUCCESS(200, "LOGIN_SUCCESS"),
    DELETE_SUCCESS(200, "DELETE_SUCCESS"),
    INSERT_SUCCESS(201, "INSERT_SUCCESS"),
    UPDATE_SUCCESS(204, "UPDATE_SUCCESS");

    private final int statusCode;
    private final String message;

}
