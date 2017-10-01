package org.account.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferDTO implements Serializable {

    private int sourceAccountId;
    private int destinationAccountId;
    private BigDecimal transferAmount;
    private String currency;

    public int getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(int sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public int getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(int destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferDTO that = (TransferDTO) o;

        if (sourceAccountId != that.sourceAccountId) return false;
        if (destinationAccountId != that.destinationAccountId) return false;
        if (transferAmount != null ? !transferAmount.equals(that.transferAmount) : that.transferAmount != null) return false;
        return !(currency != null ? !currency.equals(that.currency) : that.currency != null);
    }

    @Override
    public int hashCode() {
        int result = sourceAccountId;
        result = 31 * result + destinationAccountId;
        result = 31 * result + (transferAmount != null ? transferAmount.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
                "sourceAccountId=" + sourceAccountId +
                ", destinationAccountId=" + destinationAccountId +
                ", transferAmount=" + transferAmount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
