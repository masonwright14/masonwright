package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class EachDraftCaptainsChoice {

    /*
     * first agent is assigned to first team (of size kMin).
     * 
     * for agents in rsdOrder:
     *   if current agent is on a team (e.g., first agent):
     *     if the team is not full:
     *       the agent picks its favorite remaining agent to join the team.
     *   if current agent is not on a team 
     *   (e.g., second agent if not picked by first):
     *     the agent either joins a team that has some agent already but is
     *     not full, or joins the first (smallest) remaining empty team and
     *     simultaneously picks its favorite remaining player to join too.
     *     
     * how an agent not on a team decides what to do:
     * value of joining a team with an agent already, that is not full:
     *   sum of team's current players + (# empty spots - 1) * mean value
     *   of remaining players
     * value of joining first empty team and picking an agent, if any:
     *   value of favorite remaining agent + (final team size - 2) * mean
     *   value of remaining agents excluding the favorite
     * 
     */
    public static SimpleSearchResult eachDraftCaptainsChoiceAllocation(
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
        
        // add 1 empty list for each team.
        for (int i = 0; i < finalTeamSizes.size(); i++) {
            teams.add(new ArrayList<Integer>());
        }
        
        // add first agent in RSD order to first team.
        teams.get(0).add(rsdOrder.get(0));

        // process each agent in turn, starting with the first in RSD order
        for (int i = 0; i < rsdOrder.size(); i++) {
            final int currentAgentIndex = rsdOrder.get(i);        
            final Agent currentAgent = agents.get(currentAgentIndex);
            
            if (EachDraftHelper.isAgentTaken(teams, currentAgentIndex)) {
                // agent is already on a team 
                // (either first agent, or already chosen).
                final int teamIndex = 
                    EachDraftHelper.getTeamIndex(teams, currentAgentIndex);
                final int maxSizeForTeam = finalTeamSizes.get(teamIndex);
                if (teams.get(teamIndex).size() < maxSizeForTeam) {
                    // the team has room to add an agent.
                    final List<UUID> idsHighToLowValue = 
                        currentAgent.getAgentIdsHighValueToLow();
                    for (final UUID id: idsHighToLowValue) {
                        if (!EachDraftHelper.isAgentTaken(teams, agents, id)) {
                            final int indexToChoose = 
                                EachDraftHelper.getAgentIndexById(agents, id);
                            teams.get(teamIndex).add(indexToChoose);
                            break;
                        }
                    }
                }
            } else {
                // agent is not on a team.
                // add self to favorite other team that has room
                final int favoriteTeamIndex = 
                    getFavoriteTeamIndexWithSpace(
                        teams, finalTeamSizes, agents, currentAgent
                    );
                teams.get(favoriteTeamIndex).add(currentAgentIndex);
                if (teams.get(favoriteTeamIndex).size() == 1) {
                    // added self to empty team.
                    // if there is still room, may pick favorite remaining
                    // agent to join.
                    if (finalTeamSizes.get(favoriteTeamIndex) > 1) {
                        // there is room to add an agent
                        final List<UUID> idsHighToLowValue = 
                            currentAgent.getAgentIdsHighValueToLow();
                        for (final UUID id: idsHighToLowValue) {
                            if (
                                !EachDraftHelper.isAgentTaken(teams, agents, id)
                            ) {
                                final int indexToChoose = 
                                    EachDraftHelper.getAgentIndexById(
                                        agents, id
                                    );
                                teams.get(favoriteTeamIndex).add(indexToChoose);
                                break;
                            }
                        }
                    }
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
            rsdOrder, searchDurationMillis, null,
            similarity
        );
    }
    
    /*
     * how an agent decides which team to join:
     * for each feasible team (team must have room to join 
     * based on its team-specific size,
     * and must either have at least 1 agent already, or must be 
     * the first empty team in the
     * list):
     * 
     * if the team has some agent already,
     * take the sum of the values of current players, plus 
     * "the number of remaining spaces minus 1" times 
     * the "mean value of other remaining players".
     * 
     * if the team is empty, take the value of the favorite remaining 
     * player, plus the "final team size - 2" * mean value of remaining
     * players excluding the favorite.
     * 
     */
    private static int getFavoriteTeamIndexWithSpace(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final Agent selfAgent
    ) {
        // find mean value of other agents not yet on a team.
        double untakenOtherAgentsValue = 0.0;
        int untakenOtherAgentCount = 0;
        for (int i = 0; i < agents.size(); i++) {
            // don't count the self agent when evaluating
            // the mean value of other remaining agents.
            if (agents.get(i).equals(selfAgent)) {
                continue;
            }
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                final UUID currentAgentId = agents.get(i).getUuid();
                untakenOtherAgentsValue += 
                    selfAgent.getValueByUUID(currentAgentId);
                untakenOtherAgentCount++;
            }
        }
        
        double meanValueOtherRemainingAgents = 0.0;
        if (untakenOtherAgentCount > 0) {
            meanValueOtherRemainingAgents =
                untakenOtherAgentsValue / untakenOtherAgentCount;
        }

        double maxTeamValue = -1.0;
        int bestTeamIndex = -1;
        for (int i = 0; i < teams.size(); i++) {
            final List<Integer> currentTeam = teams.get(i);
            final int currentTeamMaxSize = finalTeamSizes.get(i);
            
            // don't consider teams that are already full.
            if (currentTeam.size() == currentTeamMaxSize) {
                continue;
            }
            
            double currentTeamValue = 0.0;
            if (currentTeam.isEmpty()) {
                // team is empty.
                // you'd pick your favorite agent to join if there is room, 
                // so add its value if the team has space.
                // then add "final team size - 2" * 
                // mean value of remaining agents
                // excluding the favorite.
                if (currentTeamMaxSize > 1) {
                    // there is room to pick favorite agent to join
                    final List<UUID> idsHighToLowValue = 
                        selfAgent.getAgentIdsHighValueToLow();
                    UUID favoriteUUID = null;
                    for (final UUID id: idsHighToLowValue) {
                        if (!EachDraftHelper.isAgentTaken(teams, agents, id)) {
                            favoriteUUID = id;
                            break;
                        }
                    }
                    if (favoriteUUID != null) {
                        // some other agent is left besides self
                        final double favoriteRemainingValue = 
                            selfAgent.getValueByUUID(favoriteUUID);
                        currentTeamValue += favoriteRemainingValue;
                        assert untakenOtherAgentCount > 0;
                        double meanValueRemainingWithoutFavorite = 0.0;
                        if (untakenOtherAgentCount > 1) {
                            meanValueRemainingWithoutFavorite = 
                                (untakenOtherAgentsValue 
                                - favoriteRemainingValue) 
                                    / (untakenOtherAgentCount - 1.0);
                        }
                        final int extraSpaces = currentTeamMaxSize - 2;
                        currentTeamValue += 
                            meanValueRemainingWithoutFavorite * extraSpaces;
                    } 
                }
            } else {
                // team already has an agent.
                // sum values of current agents,
                // then add (spaces - 1) * mean value of remaining agents.
                for (final Integer playerIndex: currentTeam) {
                    final UUID playerUUID = agents.get(playerIndex).getUuid();
                    final double playerValue = 
                        selfAgent.getValueByUUID(playerUUID);
                    currentTeamValue += playerValue;
                }
                
                if (currentTeam.size() + 1 < currentTeamMaxSize) {
                    // team has room for another 
                    // agent besides self agent.                 
                    final int extraSpaces = 
                        currentTeamMaxSize - (1 + currentTeam.size());
                    currentTeamValue += 
                        extraSpaces * meanValueOtherRemainingAgents;
                }
            }

            
            if (currentTeamValue > maxTeamValue) {
                maxTeamValue = currentTeamValue;
                bestTeamIndex = i;
            }
            
            // only allowed to join the first empty team, not some other
            // empty team which may have more spaces.
            if (currentTeam.isEmpty()) {
                break;
            }
        }
        
        assert bestTeamIndex != -1;
        return bestTeamIndex;
    }
}
