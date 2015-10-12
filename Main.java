package com.company;
import java.util.List;
import java.util.Map;

import dto.Match.MatchDetail;
import dto.Match.Participant;
import dto.Match.ParticipantIdentity;
import dto.MatchList.MatchList;
import dto.MatchList.MatchReference;
import dto.Summoner.Summoner;import constant.Region;

import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import java.util.Scanner;

public class Main{
    Map<String, Integer> elomap = new Map<String,Integer>();
    String[] Tiers = {"BRONZE","SILVER","GOLD","PLATINUM","DIAMOND"};
    String[] Divisions = {"V","IV","III","II","I"};
    int starting = 800;
    for(int i = 0; i < Tiers.length; i++){
        for(int j = 0; j <Divisions ; j++){
            elomap.put(Tiers[i]+Divisions[j],starting);
            starting += 70;
        }
    }
    
    public static void main(String[] args) throws RiotApiException {
        Scanner findUsername=new Scanner(System.in);
        System.out.print("What is your summoner name?:");
        String summonerName= findUsername.next();

        RiotApi api = new RiotApi("00e82d10-743e-4adf-a206-2c967817a311");
        Map<String, Summoner> summoners = api.getSummonersByName(Region.NA, "summonername");
        Summoner summoner = summoners.get("summonername");
        long id = summoner.getId();
        List<MatchReference> matchinfo = api.getMatchList(id).getMatches();
        Long[] matchidlist = new Long[matchinfo.size()];
        for(int i=0; i<matchinfo.size(); i ++){
           matchidlist[i] =  matchinfo.get(i).getMatchId();

        }





        System.out.println(id);
    }
    public long[][] playeridlist(Long[] matchid, RiotApi api,long id) throws RiotApiException{
        long[][] playeridlist = new long[matchid.length][9];
        for (int i = 0; i < matchid.length; i++) {
            MatchDetail match = api.getMatch(matchid[i]);
            List<ParticipantIdentity> hi = match.getParticipantIdentities();

            for(int j = 0 ; j < hi.size(); j ++) {
                if (!(hi.get(j).getPlayer().getSummonerId() == id)) {
                    playeridlist[i][j] = hi.get(j).getPlayer().getSummonerId();
                }
            }


        }
        return playeridlist;

    }
    public int[][] elochart(int[][] playeridlist){


    }
    public int mmr(String tier, String Division){
        return elomap.get(tier+Division);
    }
}
