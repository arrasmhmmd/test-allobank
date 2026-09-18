package com.allobank.splitbill.model;

import java.util.List;

public class CreateGroupDTO {
    private String name;
    private List<String> participants;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
}
