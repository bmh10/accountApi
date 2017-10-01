package org.account.model.dto;

public class ErrorDTO {

    private String errorMessage;

    public ErrorDTO(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorDTO errorDTO = (ErrorDTO) o;
        return !(errorMessage != null ? !errorMessage.equals(errorDTO.errorMessage) : errorDTO.errorMessage != null);
    }

    @Override
    public int hashCode() {
        return errorMessage != null ? errorMessage.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ErrorDTO{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
