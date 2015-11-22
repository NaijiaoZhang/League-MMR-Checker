package com.simpleandclean.ggez;

import android.app.Activity;
import android.os.AsyncTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.simpleandclean.ggez.HomePage;

import constant.Region;
import dto.League.League;
import dto.League.LeagueEntry;
import dto.Match.MatchDetail;
import dto.Match.ParticipantIdentity;
import dto.MatchList.MatchReference;
import dto.Summoner.Summoner;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

public class Main extends AsyncTask<String, Void, String> {
    public static RiotApi api = new RiotApi("00e82d10-743e-4adf-a206-2c967817a311");
    public static HashMap<String, Integer> elomap = new HashMap<String, Integer>();
    public static String[] Tiers = {"BRONZE","SILVER","GOLD","PLATINUM","DIAMOND"};
    public static String[] Divisions = {"V","IV","III","II","I"};
    public static String summonerName;
    private int matchmakingrating;
    private HomePage homepage;

    public Main (String s){
        summonerName=s;
    }





    public Main() throws RiotApiException {

            int starting = 800;
            for(int i = 0; i <Tiers.length; i++){
                for(int j = 0; j <Divisions.length ; j++){
                    elomap.put(Tiers[i]+Divisions[j],starting);
                    starting += 70;
                }
            }

            elomap.put("MASTER1",2600);
            elomap.put("CHALLENGER1",2800);

//TODO filter ranked solo, account for baddies not in leagues , weight by recent, make code less shitty,(prolly fixed) for some reason if i retrieve 5 games it only gives me 2 wtf?




        int output;
        try {
            output = calculate(elochart(playeridlist(matchidlist(summonerid(summonerName)), summonerid(summonerName)), matchidlist(summonerid(summonerName))));
        }
        catch(RiotApiException ex2){
            throw ex2;
        }
            System.out.println("Your MMR:");
        System.out.println(output);
        System.out.println("rip");

    }

    public void setActvity(HomePage a) {
        homepage = a;
    }

    @Override
    public String doInBackground(String... S) {
        try{
            matchmakingrating = calculate(elochart(playeridlist(matchidlist(summonerid(summonerName)), summonerid(summonerName)), matchidlist(summonerid(summonerName))));
        }
        catch(RiotApiException ex5) {
        }
        //return S[0];
        return S[0];
    }

    @Override
    protected void onPostExecute(String s) {
        homepage.setMatchMakingRating(matchmakingrating);
    }

    public static long summonerid(String summonerName) throws RiotApiException  {
        Map<String,Summoner> summoners;
        try {
           summoners = api.getSummonersByName(Region.NA, "mastapan");
        }
        catch(RiotApiException ex){
            throw ex;
        }
        Summoner summoner = summoners.get("mastapan");
        System.out.println(summoner.getName());
        long value = summoner.getId();
        return value;

    }
    public static long[] matchidlist(long id) throws RiotApiException  {
        //sets stuff up


        List<MatchReference> matchinfo;
        try {
            matchinfo = api.getMatchList(id).getMatches();
        }
        catch(RiotApiException ex3) {
            throw ex3;
        }
        //filter by ranked 5v5 i guess,
        List<MatchReference> shortened = matchinfo.subList(0, 10);
        long[] matchidlist = new long[shortened.size()];
        for(int i=0; i<shortened.size(); i ++){
            matchidlist[i] =  shortened.get(i).getMatchId();

        }
        return matchidlist;

    }
    public static Map<Long, List<Long>> playeridlist(long[] matchid,long id) throws RiotApiException {
        //Makes list of past x matches and players assocaiated with each match
        Map<Long, List<Long>> playeridlist = new HashMap<Long, List<Long>>();
        //long[][] playeridlist = new long[matchid.length][];
        for (int i = 0; i < matchid.length; i++) {
            MatchDetail match = api.getMatch(matchid[i]);
            List<ParticipantIdentity> hi = match.getParticipantIdentities();
            List<Long> templist = new ArrayList<Long>();

            for(int j = 0, k =0 ; k < hi.size(); j ++,k++) {
                if((hi.get(j).getPlayer().getSummonerId() == id)){
                    k++;
                }

                if (!(hi.get(j).getPlayer().getSummonerId() == id)) {
                    //not working

                    templist.add(hi.get(k).getPlayer().getSummonerId());

                }

            }
            playeridlist.put(matchid[i],templist);
            try{
                Thread.sleep(2000);

            } catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            System.out.print("wat");


        }
        return playeridlist;

    }
    public static ArrayList<Integer> elochart(Map<Long,List<Long>> playeridlist,long[] matchid) throws RiotApiException {

//        for(int i = 0; i < playeridlist.length; i++){
//            for(int j = 0 ; j< playeridlist[i].length; j++) {
//                System.out.println(playeridlist[i][j]);
//            }
//
//        }




        //Returns the "elo" of players using summoner id


        List<List<List<League>>> league = new ArrayList<List<List<League>>>();

        for(int i = 0; i < matchid.length; i++){
            List<List<League>> leaguesub = new ArrayList<List<League>>();
            for(int j = 0 ; j< playeridlist.get(matchid[i]).size(); j++) {
                System.out.println(playeridlist.get(matchid[i]).get(j));
                //something something not in a league
                leaguesub.add(api.getLeagueEntryBySummoner(Region.NA, playeridlist.get(matchid[i]).get(j)));
                try{
                    Thread.sleep(1250);

                } catch(InterruptedException ex){
                    Thread.currentThread().interrupt();
                }

                }
            league.add(leaguesub);

                }
        ArrayList<Integer> elolist = new ArrayList<Integer>();
        System.out.println("wtf is league" + league.size());
        for(List<List<League>> hi : league) {


            for (List<League> z : hi) {
                //check prolly
            //List<League> z = hi.get(0);
            System.out.println("number of games " + z.size());
               // for (League s : z) {
                League s = z.get(0);

                    String tier = s.getTier();
                    for (LeagueEntry y : s.getEntries()) {
                        int leaguepoints = y.getLeaguePoints();
                        String division = y.getDivision();
                        elolist.add(mmr(tier, division , leaguepoints));


                    }
                //}
           }
        }
        System.out.println(elolist.size() + "wtf why has it shrunk");
            return elolist;



        }
    public static int calculate(ArrayList<Integer> elolist){
        int sum = 0;
        for(int i = 0; i<elolist.size(); i++){
            System.out.println(elolist.get(i));
           sum += elolist.get(i);
        }

        return sum/elolist.size();

    }



    public static int mmr(String tier, String Division, int leaguepoints) {
        if ((tier + Division).equals("MASTER1") || (tier + Division).equals("CHALLENGER1")) {
            return elomap.get(tier + Division) + (300 * leaguepoints / 1356);
        } else {
            return elomap.get(tier + Division) + (70 * leaguepoints / 100); //check int div
        }
    }
