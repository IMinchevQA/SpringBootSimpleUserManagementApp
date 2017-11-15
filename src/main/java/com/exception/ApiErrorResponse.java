package com.exception;


/**
 * Created by Ivan Minchev on 11/14/2017.
 */
public class ApiErrorResponse {
    private String status;
    private String message;

    public ApiErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

//    @Override
//    public String toString() {
//        return "ApiErrorResponse{" +
//                "status=" + this.status + this.status.getReasonPhrase() +
//                ", message=" + this.message +
//                '}';
//    }
}
