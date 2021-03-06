package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class EachAgentDraftTabu {

    
    public static SimpleSearchResult eachAgentDraftTabu(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        
        final List<Integer> finalTeamSizes = 
            RsdUtil.getOptimalTeamSizeList(agents.size(), kMax);

        // make it so first agent to pick has the smaller team size, etc.
        Collections.reverse(finalTeamSizes);
        assert finalTeamSizes.get(0) 
            <= finalTeamSizes.get(finalTeamSizes.size() - 1);

        // each list holds the indexes of agents on 1 team.
        final List<List<Integer>> teams =
            new ArrayList<List<Integer>>();
        final List<Integer> captainIndexes = new ArrayList<Integer>();
        // add 1 empty list for each team.
        // then add the captain to this empty list.
        for (int i = 0; i < finalTeamSizes.size(); i++) {
            teams.add(new ArrayList<Integer>());
            final int currentCaptainIndex = rsdOrder.get(i);
            captainIndexes.add(currentCaptainIndex);
            teams.get(i).add(currentCaptainIndex);
        }
        
        for (int i = 0; i < rsdOrder.size(); i++) {
            final int currentAgentIndex = rsdOrder.get(i);        
            final Agent currentAgent = agents.get(currentAgentIndex);
            if (EachDraftHelper.isAgentTaken(teams, currentAgentIndex)) {
                // agent is already on a team 
                // (either captain, or already chosen).
                final int teamIndex = 
                    EachDraftHelper.getTeamIndex(teams, currentAgentIndex);
                final int maxSizeForTeam = finalTeamSizes.get(teamIndex);
                if (teams.get(teamIndex).size() < maxSizeForTeam) {
                    // the team has room to add an agent.
                    
                    // there should be a free agent remaining, or else something
                    // went wrong, because the team can't be filled.
                    assert isFreeAgentLeft(teams, agents.size());
                    final int agentIndexToChoose = 
                        getFavoriteAgentIndex(
                            teams, finalTeamSizes, agents, currentAgent
                        );
                    teams.get(teamIndex).add(agentIndexToChoose);
                    if (MipGenerator.DEBUGGING) {
                        System.out.println(
                            i + ". Agent " + currentAgentIndex 
                                + " chose " + agentIndexToChoose
                        );
                    }
                }
            } else {
                // agent is not on a team.
                // add self to favorite other team that has room
                final int favoriteTeamIndex = 
                    getFavoriteTeamIndex(
                        teams, finalTeamSizes, agents, currentAgent
                    );
                teams.get(favoriteTeamIndex).add(currentAgentIndex);
                if (MipGenerator.DEBUGGING) {
                    System.out.println(
                        i + ". Agent " + currentAgentIndex 
                            + " joined team " + favoriteTeamIndex
                    );
                }
            }
        }
        
        if (MipGenerator.DEBUGGING) {
            int totalAgents = 0;
            for (final List<Integer> team: teams) {
                totalAgents += team.size();
            }
            if (totalAgents != agents.size()) {
                throw new IllegalStateException();
            }
        }
        
        final List<List<Integer>> allocation = 
            RandomAllocation.getAllocationFromLists(
                agents.size(), 
                teams
            );
        final int kMin = finalTeamSizes.get(0);
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final double similarity = 
            PreferenceAnalyzer.getMeanPairwiseCosineSimilarity(agents);
        return new SimpleSearchResult(
            allocation, kMin, kMax, agents, 
            rsdOrder, searchDurationMillis, captainIndexes,
            similarity
        );
    }
    
    public static int countTeamsWithSpace(
        final List<List<Integer>> teams, 
        final List<Integer> finalTeamSizes
    ) {
        int result = 0;
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).size() < finalTeamSizes.get(i)) {
                result++;
            }
        }
        
        return result;
    }
    
    public static boolean isFreeAgentLeft(
        final List<List<Integer>> teams, 
        final int n
    ) {
        for (int i = 0; i < n; i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static int getFavoriteTeamIndex(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final Agent captain
    ) {
        // only call this method if captain is free, not taken.
        assert !EachDraftHelper.isAgentTaken(teams, agents, captain.getUuid());
        
        // all teams should have captains already.
        for (List<Integer> team: teams) {
            assert !team.isEmpty();
        }
        
        // if there is only one team with space, choose it now
        if (countTeamsWithSpace(teams, finalTeamSizes) == 1) {
            return getFirstTeamWithSpaceIndex(teams, finalTeamSizes);
        }
        
        // final GammaZ gammaZ = new GammaZ4();
        final GammaZ gammaZ = new GammaZ2();
        
        final SearchResult searchResult = 
            TabuSearchEachDraft.tabuSearchEachDraftFreeCaptain(
                agents, gammaZ, captain, teams, finalTeamSizes
            );
        
        final int captainIndex = agents.indexOf(captain);
        final List<Integer> captainAllocation = 
            searchResult.getAllocation().get(captainIndex);
        
        if (RsdUtil.getTeamSize(captainAllocation) == 1) {
            // got empty bundle.
            // return index of favorite team with room.
            final int favoriteTeamIndex = 
                EachDraftHelper.getFavoriteTeamIndexWithSpace(
                    teams, finalTeamSizes, agents, captain
                );
            return favoriteTeamIndex;
        }
        
        // got a bundle containing some team.
        // return index of the team included in this bundle.
        
        return getIncludedTeamIndex(teams, captainAllocation);
    }
    
    /**
     * 
     * @param teams list of list<Integer>, 
     * where each list has the indexes of members
     * of one team.
     * @param myAllocation must include all members of exactly 1
     *  team in "teams", as indicated
     * by a "1" at the index of each member of that team.
     * @return index in "teams" of the included team.
     */
    private static int getIncludedTeamIndex(
        final List<List<Integer>> teams,
        final List<Integer> myAllocation
    ) {
        for (int teamIndex = 0; teamIndex < teams.size(); teamIndex++) {
            final List<Integer> team = teams.get(teamIndex);
            // all teams have been assigned captains already, 
            // so each team has at least one member.
            final int firstAgentIndex = team.get(0);
            if (myAllocation.get(firstAgentIndex) == 1) {
                // this is the team included in the bundle.
                return teamIndex;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    private static int getFavoriteAgentIndex(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final Agent captain
    ) {
        // only call this method if captain is already taken.
        assert EachDraftHelper.isAgentTaken(teams, agents, captain.getUuid());
        final int captainTeamIndex = 
            EachDraftHelper.getTeamIndex(teams, agents.indexOf(captain));
        // only call this method if the captain's team is not full.
        assert teams.get(captainTeamIndex).size() 
            < finalTeamSizes.get(captainTeamIndex);
        // only call this method if there is a free agent left.
        assert isFreeAgentLeft(teams, agents.size());

        // if there is only 1 free agent left, choose it now.
        if (countFreeAgentsLeft(teams, agents.size()) == 1) {
            // there is only 1 free agent left. return its index.
            return getFirstFreeAgentIndex(teams, agents.size());
        }
        
        // use GammaZ that penalizes under-demand heavily.
        // final GammaZ gammaZ = new GammaZ4();
        final GammaZ gammaZ = new GammaZ2();
        final SearchResult searchResult = 
            TabuSearchEachDraft.tabuSearchEachDraftTakenCaptain(
                agents, gammaZ, captain, teams, finalTeamSizes
            );
        final int captainIndex = agents.indexOf(captain);
        final List<Integer> captainAllocation = 
            searchResult.getAllocation().get(captainIndex);
        
        if (
            RsdUtil.getTeamSize(captainAllocation) 
            == teams.get(captainTeamIndex).size()
            || RsdUtil.getTeamSize(captainAllocation) == 1
        ) {
            // couldn't afford any free agents on the market.
            // select favorite free agent.
            return favoriteFreeAgentIndex(
                agents, captain, teams
            );
        }
        
        // got a non-empty bundle.
        // select the favorite free agent in this bundle.
        return favoriteFreeAgentInAllocationIndex(
            agents, captain, teams, captainAllocation
        );
    }
    
    /*
     * Returns index in "agents", not in the allocation itself.
     * The favorite free agent is selected from the selfAllocation.
     */
    private static int favoriteFreeAgentInAllocationIndex(
        final List<Agent> agents,
        final Agent self,
        final List<List<Integer>> teams,
        final List<Integer> selfAllocation
    ) {
        assert !selfAllocation.isEmpty();
        assert EachDraftHelper.isAgentTaken(teams, agents.indexOf(self));
        final int selfIndex = agents.indexOf(self);
        assert selfAllocation.get(selfIndex) == 1;
        assert selfAllocation.size() == agents.size();
        // select the favorite free agent in this bundle.
        double bestValue = -1.0;
        int bestIndex = -1;
        for (int i = 0; i < selfAllocation.size(); i++) {
            assert selfAllocation.get(i) == 0 
                || selfAllocation.get(i) == 1;
            
            if (
                i != selfIndex // not the self agent
                && selfAllocation.get(i) == 1 // in the bundle
                && !EachDraftHelper.isAgentTaken(teams, i) // free agent
            ) {
                final UUID currentId = agents.get(i).getUuid();
                final double currentValue = self.getValueByUUID(currentId);
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                    bestIndex = i;
                }
            }
        }
        // there must be at least 1 free agent in the allocation
        if (bestIndex == -1) {
            System.out.println("teams:");
            for (List<Integer> team: teams) {
                System.out.println(team);
            }
            System.out.println("self index: " + selfIndex);
            System.out.println("self allocation: " + selfAllocation);
            System.out.println("agent count: " + agents.size());
            System.out.println(
                "free agents left: " + countFreeAgentsLeft(teams, agents.size())
            );
            throw new IllegalStateException();
        }
        return bestIndex;
    }
    
    private static int favoriteFreeAgentIndex(
        final List<Agent> agents,
        final Agent self,
        final List<List<Integer>> teams
    ) {
        assert countFreeAgentsLeft(teams, agents.size()) > 0;
        double favoriteValue = -1.0;
        UUID favoriteId = null;
        for (final UUID aUuid: self.getAgentIdsHighValueToLow()) {
            // only consider free agents
            if (!EachDraftHelper.isAgentTaken(teams, agents, aUuid)) {
                final double currentValue = self.getValueByUUID(aUuid);
                if (currentValue > favoriteValue) {
                    favoriteValue = currentValue;
                    favoriteId = aUuid;
                }
            }
        }
        
        // there must be a free agent.
        if (favoriteId == null) {
            throw new IllegalStateException();
        }
        
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).getUuid().equals(favoriteId)) {
                return i;
            }
        }
        
        throw new IllegalStateException("not found");
    }
    
    public static int countFreeAgentsLeft(
        final List<List<Integer>> teams, 
        final int n
    ) {
        int result = 0;
        for (int i = 0; i < n; i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                result++;
            }
        }
        
        return result;
    }
    
    private static int getFirstFreeAgentIndex(
        final List<List<Integer>> teams,
        final int n
    ) {
        assert countFreeAgentsLeft(teams, n) > 0;
        
        for (int i = 0; i < n; i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                return i;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    private static int getFirstTeamWithSpaceIndex(
        final List<List<Integer>> teams, 
        final List<Integer> finalTeamSizes
    ) {
        assert countTeamsWithSpace(teams, finalTeamSizes) > 0;
        
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).size() < finalTeamSizes.get(i)) {
                return i;
            }
        }
        
        throw new IllegalStateException();
    }
    
    /*****************************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        // testGetFirstTeamWithSpaceIndex();
        // testGetFirstFreeAgentIndex();
        // testCountFreeAgentsLeft();
        // testCountTeamsWithSpace();
        // testIsFreeAgentLeft();
        // testGetIncludedTeamIndex();
        // testFavoriteFreeAgentIndex();
        testFavoriteFreeAgentInAllocationIndex();
    }
    
    /*
     * Should be: 1
     */
    @SuppressWarnings("unused")
    private static void testGetFirstTeamWithSpaceIndex() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final Integer[] teamSizesArr = {3, 4, 7};
        List<Integer> teamSizes = Arrays.asList(teamSizesArr);
        
        System.out.println(getFirstTeamWithSpaceIndex(teams, teamSizes));
    }
    
    /*
     * should be: 10
     */
    @SuppressWarnings("unused")
    private static void testGetFirstFreeAgentIndex() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final int n = 14;
        System.out.println(getFirstFreeAgentIndex(teams, n));
    }
    
    /*
     * Should be: 4
     */
    @SuppressWarnings("unused")
    private static void testCountFreeAgentsLeft() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final int n = 14;
        System.out.println(countFreeAgentsLeft(teams, n));
    }
    
    /*
     * Should be: 2
     */
    @SuppressWarnings("unused")
    private static void testCountTeamsWithSpace() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final Integer[] teamSizesArr = {3, 4, 7};
        List<Integer> teamSizes = Arrays.asList(teamSizesArr);
        
        System.out.println(countTeamsWithSpace(teams, teamSizes));
    }
    
    /*
     * Should be: true false
     */
    @SuppressWarnings("unused")
    private static void testIsFreeAgentLeft() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final int n = 14;
        System.out.println(isFreeAgentLeft(teams, n));
        final int smallerGroupN = 10;
        System.out.println(isFreeAgentLeft(teams, smallerGroupN));
    }
    
    /*
     * Should be: 1
     */
    @SuppressWarnings("unused")
    private static void testGetIncludedTeamIndex() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        
        Integer[] myAllocationArr = 
            {0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0};
        List<Integer> myAllocation = Arrays.asList(myAllocationArr);
        System.out.println(getIncludedTeamIndex(teams, myAllocation));
    }
    
    /*
     * Should be: 13
     */
    @SuppressWarnings("unused")
    private static void testFavoriteFreeAgentIndex() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        
        final Double[] captainValuesArr = 
        {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};
        List<Double> captainValues = Arrays.asList(captainValuesArr);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        final int takenIndex = 0;
        int selfIndex = takenIndex;
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();
            if (i == selfIndex) {
                values.addAll(captainValues);
            } else {
                for (int j = 1; j < countAgents; j++) {
                    values.add(1.0);
                }
            }

            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 105.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        System.out.println(
            favoriteFreeAgentIndex(agents, agents.get(selfIndex), teams)
        );
    }
    
    /*
     * Should be: 12
     */
    private static void testFavoriteFreeAgentInAllocationIndex() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        
        final Double[] captainValuesArr = 
        {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};
        List<Double> captainValues = Arrays.asList(captainValuesArr);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        final int takenIndex = 0;
        int selfIndex = takenIndex;
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();
            if (i == selfIndex) {
                values.addAll(captainValues);
            } else {
                for (int j = 1; j < countAgents; j++) {
                    values.add(1.0);
                }
            }

            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 105.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        Integer[] allocationArr = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0};
        List<Integer> allocation = Arrays.asList(allocationArr);
        
        System.out.println(
            favoriteFreeAgentInAllocationIndex(
                agents, agents.get(selfIndex), teams, allocation
            )
        );
    }
}
