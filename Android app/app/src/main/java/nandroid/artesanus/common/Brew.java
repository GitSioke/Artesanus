package nandroid.artesanus.common;

import java.util.Date;
import java.util.List;

/**
 *  This class represents the model of a whole brew crafting.
 */

public class Brew
{

    private String id;

    private Date startDate;

    private Date endDate;

    private int litres;

    private String beerType;

    private String name;

    private List<Cereal> cereals;

    private List<Heat> heats;

    private List<Hop> hops;

    private List<BrewProcess> processes;

    public int getLitres() {
        return litres;
    }

    public void setLitres(int litres) {
        this.litres = litres;
    }

    public String getBeerType() {
        return beerType;
    }

    public void setBeerType(String beerType) {
        this.beerType = beerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cereal> getCereals() {
        return cereals;
    }

    public void setCereals(List<Cereal> cereals) {
        this.cereals = cereals;
    }

    public List<Heat> getHeats() {
        return heats;
    }

    public void setHeats(List<Heat> heats) {
        this.heats = heats;
    }

    public List<Hop> getHops() {
        return hops;
    }

    public void setHops(List<Hop> hops) {
        this.hops = hops;
    }

    public List<BrewProcess> getProcesses() {
        return processes;
    }

    public void setProcesses(List<BrewProcess> brewProcesses) {
        this.processes = brewProcesses;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
