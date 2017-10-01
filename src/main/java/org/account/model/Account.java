package org.account.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class Account {

    private int id;
    private String accountHolderName;
    private Currency currency;
    private BigDecimal balance;
    private Date createdDate;

    public Account() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
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

        Account account = (Account) o;

        if (id != account.id) return false;
        if (accountHolderName != null ? !accountHolderName.equals(account.accountHolderName) : account.accountHolderName != null)
            return false;
        if (currency != null ? !currency.equals(account.currency) : account.currency != null) return false;
        if (balance != null ? !balance.equals(account.balance) : account.balance != null) return false;
        return !(createdDate != null ? !createdDate.equals(account.createdDate) : account.createdDate != null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (accountHolderName != null ? accountHolderName.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", currency=" + currency +
                ", balance=" + balance +
                ", createdDate=" + createdDate +
                '}';
    }
}
