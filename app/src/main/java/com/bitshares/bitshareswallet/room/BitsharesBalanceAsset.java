package com.bitshares.bitshareswallet.room;

/**
 * Created by lorne on 31/10/2017.
 */

public class BitsharesBalanceAsset {
    int id;
    public long amount;
    public String base;
    public long base_precision;
    public String quote;
    public long total;
    public long quote_precision;
    public String currency;
    public long balance;
    public long currency_precision;

    @Override
    public String toString() {
        return "id =" + id + "\n" +
                "amount =" + amount + "\n" +
                "base =" + base + "\n" +
                "base_precision =" + base_precision + "\n" +
                "quote =" + quote + "\n" +
                "total =" + total + "\n" +
                "quote_precision =" + quote_precision + "\n" +
                "currency =" + currency + "\n" +
                "balance =" + balance + "\n" +
                "currency_precision =" + currency_precision + "\n";
    }
}
