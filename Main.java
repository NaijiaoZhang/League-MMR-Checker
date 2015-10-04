package com.company;
import java.util.Map;
import dto.Summoner.Summoner;import constant.Region;

import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) throws RiotApiException {
        Scanner findUsername=new Scanner(System.in);
        System.out.print("What is your summoner name?:");
        String summonerName= findUsername.next();

        RiotApi api = new RiotApi("00e82d10-743e-4adf-a206-2c967817a311");
        Map<String, Summoner> summoners = api.getSummonersByName(Region.NA, "summonername");
        Summoner summoner = summoners.get("summonername");
        long id = summoner.getId();
        System.out.println(id);
    }

}
