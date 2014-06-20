package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NeighborGenEachTabu {
    
    public static List<PriceWithError> sortedNeighbors(
        final List<Double> prices,
        final List<Double> z,
        final double maxPrice,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final Agent captain
    ) {
        final int captainIndex = agents.indexOf(captain);
        if (EachDraftHelper.isAgentTaken(teams, captainIndex)) {
            return sortedNeighborsTakenCaptain(
                prices, z, maxPrice, agents, 
                gammaZ, teams, finalTeamSizes, captain
            );
        }
        
        return sortedNeighborsFreeCaptain(
            prices, z, maxPrice, agents, gammaZ, teams, finalTeamSizes, captain
        );
    }
    
    private static List<PriceWithError> sortedNeighborsTakenCaptain(
        final List<Double> prices,
        final List<Double> z,
        final double maxPrice,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final Agent captain
    ) {
        final List<PriceWithSource> neighborPrices = 
            NeighborGenerator.neighbors(
                prices, z, maxPrice
            );
        final DemandGeneratorOneCTakenCaptain demandGen = 
            new DemandGeneratorOneCTakenCaptain();
        
        final List<PriceWithError> result = new ArrayList<PriceWithError>();
        for (
            final PriceWithSource neighborPriceWithSource
            : neighborPrices
        ) {
            final List<List<Integer>> neighborDemand = 
                demandGen.getAggregateDemandTakenCaptain(
                    agents, 
                    neighborPriceWithSource.getPrice(), 
                    teams,
                    finalTeamSizes,
                    maxPrice,
                    captain
                );
            final int kMax = TabuSearch.getKMax(finalTeamSizes);
            final int kMin = TabuSearch.getKMin(finalTeamSizes);
            final List<Double> neighborZ = 
                gammaZ.z(
                    neighborDemand, neighborPriceWithSource.getPrice(), 
                    kMax, kMin, maxPrice
                );
            final double neighborError = 
                DemandAnalyzer.errorSizeDouble(neighborZ);
            result.add(
                new PriceWithError(
                    neighborPriceWithSource.getPrice(), 
                    neighborZ, 
                    neighborDemand, 
                    neighborError,
                    neighborPriceWithSource.getPriceUpdateSource()
                )
            );
        }
        
        Collections.sort(result);
        
        if (MipGenerator.DEBUGGING) {
            final double firstError = result.get(0).getErrorValue();
            final double lastError = 
                result.get(result.size() - 1).getErrorValue();
            if (firstError > lastError) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
    
    private static List<PriceWithError> sortedNeighborsFreeCaptain(
        final List<Double> prices,
        final List<Double> z,
        final double maxPrice,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final Agent captain
    ) {
        final List<PriceWithSource> neighborPrices = 
            NeighborGenerator.neighbors(
                prices, z, maxPrice
            );
        final DemandGeneratorOneCFreeCaptain demandGen = 
            new DemandGeneratorOneCFreeCaptain();
        
        final List<PriceWithError> result = new ArrayList<PriceWithError>();
        for (
            final PriceWithSource neighborPriceWithSource
            : neighborPrices
        ) {
            final List<List<Integer>> neighborDemand = 
                demandGen.getAggregateDemandFreeCaptain(
                    agents, 
                    neighborPriceWithSource.getPrice(), 
                    teams,
                    finalTeamSizes,
                    maxPrice,
                    captain
                );
            final int kMax = TabuSearch.getKMax(finalTeamSizes);
            final int kMin = TabuSearch.getKMin(finalTeamSizes);
            final List<Double> neighborZ = 
                gammaZ.z(
                    neighborDemand, neighborPriceWithSource.getPrice(), 
                    kMax, kMin, maxPrice
                );
            final double neighborError = 
                DemandAnalyzer.errorSizeDouble(neighborZ);
            result.add(
                new PriceWithError(
                    neighborPriceWithSource.getPrice(), 
                    neighborZ, 
                    neighborDemand, 
                    neighborError,
                    neighborPriceWithSource.getPriceUpdateSource()
                )
            );
        }
        
        Collections.sort(result);
        
        if (MipGenerator.DEBUGGING) {
            final double firstError = result.get(0).getErrorValue();
            final double lastError = 
                result.get(result.size() - 1).getErrorValue();
            if (firstError > lastError) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
}
