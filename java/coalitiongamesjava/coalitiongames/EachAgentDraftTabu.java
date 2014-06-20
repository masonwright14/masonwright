package coalitiongames;

import java.util.ArrayList;
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
                    assert isFreeAgentLeft(teams, agents);
                    final int agentIndexToChoose = 
                        getFavoriteAgentIndex(
                            teams, finalTeamSizes, agents, currentAgent
                        );
                    teams.get(teamIndex).add(agentIndexToChoose);
                }
            } else {
                // agent is not on a team.
                // add self to favorite other team that has room
                final int favoriteTeamIndex = 
                    getFavoriteTeamIndex(
                        teams, finalTeamSizes, agents, currentAgent
                    );
                teams.get(favoriteTeamIndex).add(currentAgentIndex);
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
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).size() < finalTeamSizes.get(i)) {
                    return i;
                }
            }
        }
        
        final GammaZ gammaZ = new GammaZ4();
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
        
        for (int teamIndex = 0; teamIndex < teams.size(); teamIndex++) {
            final List<Integer> team = teams.get(teamIndex);
            // all teams have been assigned captains already, 
            // so each team has at least one member.
            final int firstAgentIndex = team.get(0);
            if (captainAllocation.get(firstAgentIndex) == 1) {
                // this is the team included in the bundle.
                return teamIndex;
            }
        }
        
        throw new IllegalStateException();
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
        assert isFreeAgentLeft(teams, agents);

        // if there is only 1 free agent left, choose it now.
        if (countFreeAgentsLeft(teams, agents) == 1) {
            // there is only 1 free agent left. return its index.
            for (int i = 0; i < agents.size(); i++) {
                if (!EachDraftHelper.isAgentTaken(teams, i)) {
                    return i;
                }
            }
        }
        
        // use GammaZ that penalizes under-demand heavily.
        final GammaZ gammaZ = new GammaZ4();
        final SearchResult searchResult = 
            TabuSearchEachDraft.tabuSearchEachDraftTakenCaptain(
                agents, gammaZ, captain, teams, finalTeamSizes
            );
        final int captainIndex = agents.indexOf(captain);
        final List<Integer> captainAllocation = 
            searchResult.getAllocation().get(captainIndex);
        
        if (RsdUtil.getTeamSize(captainAllocation) == 1) {
            // got empty bundle.
            // select favorite free agent.
            double favoriteValue = -1.0;
            UUID favoriteId = null;
            for (final UUID aUuid: captain.getAgentIdsHighValueToLow()) {
                // only consider free agents
                if (!EachDraftHelper.isAgentTaken(teams, agents, aUuid)) {
                    final double currentValue = captain.getValueByUUID(aUuid);
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
        
        // got a non-empty bundle.
        // select the favorite free agent in this bundle.
        double bestValue = -1.0;
        int bestIndex = -1;
        for (int i = 0; i < captainAllocation.size(); i++) {
            if (
                i != captainIndex // not the captain
                && captainAllocation.get(i) == 1 // in the bundle
                && !EachDraftHelper.isAgentTaken(teams, i) // free agent
            ) {
                final UUID currentId = agents.get(i).getUuid();
                final double currentValue = captain.getValueByUUID(currentId);
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                    bestIndex = i;
                }
            }
        }
        // there must be at least 1 free agent in the allocation
        if (bestIndex == -1) {
            throw new IllegalStateException();
        }
        return bestIndex;
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
    
    private static int countFreeAgentsLeft(
        final List<List<Integer>> teams, 
        final List<Agent> agents
    ) {
        int result = 0;
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                result++;
            }
        }
        
        return result;
    }
    
    public static boolean isFreeAgentLeft(
        final List<List<Integer>> teams, 
        final List<Agent> agents
    ) {
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                return true;
            }
        }
        
        return false;
    }
}
