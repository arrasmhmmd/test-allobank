package com.allobank.splitbill.model;

public class ApiResponse<T> {
    private boolean success;
    private String description;
    private T data;
    private Object error;

    public ApiResponse(boolean success, String description, T data, Object error) {
        this.success = success;
        this.description = description;
        this.data = data;
        this.error = error;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Object getError() { return error; }
    public void setError(Object error) { this.error = error; }
}
