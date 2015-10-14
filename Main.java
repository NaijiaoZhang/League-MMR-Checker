package com.company;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constant.Season;
import dto.League.League;
import dto.League.LeagueEntry;
import dto.Match.MatchDetail;
import dto.Match.Participant;
import dto.Match.ParticipantIdentity;
import dto.MatchList.MatchList;
import dto.MatchList.MatchReference;
import dto.Summoner.Summoner;import constant.Region;

import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import java.util.Scanner;

public class Main {
    public static RiotApi api = new RiotApi("00e82d10-743e-4adf-a206-2c967817a311");
    public static HashMap<String, Integer> elomap = new HashMap<String, Integer>();
    public static String[] Tiers = {"BRONZE","SILVER","GOLD","PLATINUM","DIAMOND"};
    public static String[] Divisions = {"V","IV","III","II","I"};







    public  static void main(String[] args) throws RiotApiException {

            int starting = 800;
            for(int i = 0; i <Tiers.length; i++){
                for(int j = 0; j <Divisions.length ; j++){
                    elomap.put(Tiers[i]+Divisions[j],starting);
                    starting += 70;
                }
            }





        Scanner findUsername=new Scanner(System.in);
        System.out.print("What is your summoner name?:");
        String summonerName= findUsername.next();

        int output = calculate(elochart(playeridlist(matchidlist(summonerName,summonerid(summonerName)),summonerid(summonerName))));
        System.out.println("lel");
        System.out.println(output);
        System.out.println("rip");








        //System.out.println(id);
    }
    public static long summonerid(String summonerName) throws RiotApiException{
        Map<String, Summoner> summoners = api.getSummonersByName(Region.NA, "Mastapan");
        Summoner summoner = summoners.get("mastapan");
        System.out.println(summoner.getName());
        long value = summoner.getId();
        return value;

    }
    public static long[] matchidlist(String summonerName, long id) throws RiotApiException{
        //sets stuff up




        List<MatchReference> matchinfo = api.getMatchList(id).getMatches();
        List<MatchReference> shortened = matchinfo.subList(0,5);
        long[] matchidlist = new long[shortened.size()];
        for(int i=0; i<shortened.size(); i ++){
            matchidlist[i] =  shortened.get(i).getMatchId();

        }
        return matchidlist;

    }
    public static long[][] playeridlist(long[] matchid,long id) throws RiotApiException{
        //Makes list of past x matches and players assocaiated with each match
        long[][] playeridlist = new long[matchid.length][10];
        for (int i = 0; i < matchid.length; i++) {
            MatchDetail match = api.getMatch(matchid[i]);
            List<ParticipantIdentity> hi = match.getParticipantIdentities();

            for(int j = 0 ; j < hi.size(); j ++) {
               // System.out.println(hi.size());
               // if (!(hi.get(j).getPlayer().getSummonerId() == id)) {
                    //not working
                    playeridlist[i][j] = hi.get(j).getPlayer().getSummonerId();
              //  }

            }
            try{
                Thread.sleep(2000);

            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            System.out.print("wat");


        }
        return playeridlist;

    }
    public static ArrayList<Integer> elochart(long[][] playeridlist) throws RiotApiException{
        //Returns the "elo" of players using summoner id


        List<List<List<League>>> league = new ArrayList<List<List<League>>>();

        for(int i = 0; i < playeridlist.length; i++){
            List<List<League>> leaguesub = new ArrayList<List<League>>();
            for(int j = 0 ; j< playeridlist[i].length; j++) {
                System.out.println(playeridlist[i][j]);
                leaguesub.add(api.getLeagueEntryBySummoner(Region.NA, playeridlist[i][j]));
                try{
                    Thread.sleep(2000);

                } catch(InterruptedException ex){
                    Thread.currentThread().interrupt();
                }

                }
            league.add(leaguesub);

                }
        ArrayList<Integer> elolist = new ArrayList<Integer>();
        for(List<List<League>> hi : league) {

            for (List<League> z : hi) {
                //check prolly
                for (League s : z) {
                    String tier = s.getTier();
                    for (LeagueEntry y : s.getEntries()) {
                        int leaguepoints = y.getLeaguePoints();
                        String division = y.getDivision();
                        elolist.add(mmr(tier, division , leaguepoints));


                    }
                }
            }
        }
            return elolist;



        }
    public static int calculate(ArrayList<Integer> elolist){
        int sum = 0;
        for(int i = 0; i<elolist.size(); i++){
           sum += elolist.get(i);
        }

        return sum/elolist.size();

    }







    public static int mmr(String tier, String Division, int leaguepoints) {
        return elomap.get(tier+Division)*(70*leaguepoints/100); //check int div
    }

}
