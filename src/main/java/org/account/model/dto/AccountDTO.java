package org.account.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class AccountDTO implements Serializable {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";

    private Integer id;
    private String accountHolderName;
    private String currency;
    private BigDecimal balance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date createdDate;

    public AccountDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountDTO that = (AccountDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (accountHolderName != null ? !accountHolderName.equals(that.accountHolderName) : that.accountHolderName != null) return false;
        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        return !(createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountHolderName != null ? accountHolderName.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                ", createdDate=" + createdDate +
                '}';
    }
}
