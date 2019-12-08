package pt.elevenzeronine.rankup.factory;

import java.util.List;

public class Rank {

    private String rank, prefix;

    private Integer position;

    private double price;

    private List<String> commands;

    public Rank(String rank, String prefix,Integer position, Double price, List<String> commands, Boolean isDefault) {
        this.rank = rank;
        this.prefix = prefix.replaceAll("&", "§");
        this.position = position;
        this.price = price;
        this.commands = commands;
    }

    public List<String> getCommands() { return this.commands; }

    public String getRank() {
        return rank;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getPosition() {
        return position;
    }

    public double getPrice() {
        return price;
    }

}


