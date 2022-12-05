package scc.data.layers;

import scc.data.models.AuctionDAO;

import java.util.stream.Stream;

public interface SearchLayer {
    Stream<AuctionDAO> findAuction(String queryText);
}
