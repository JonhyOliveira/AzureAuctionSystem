package scc.functions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.azure.cosmos.implementation.apachecommons.lang.NotImplementedException;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import scc.data.layers.BlobStorageLayer;
import scc.data.layers.CosmosDBLayer;

/**
 * Azure Functions with Timer trigger for garbage collection.
 * Schedule is: second minute hour ...
 */
public class GarbageCollectionFunctions {

    /**
     * This funtion will be invoked every 10 minutes to ensure that auctions from deleted users are deleted
     */
    @FunctionName("GC-AuctionsNullOwner")
    public void deleteAuctionNullUser(
            @TimerTrigger(name = "GC-NullAuctionOwnerTrigger", schedule = "0 */10 * * * *") String timerInfo,
            final ExecutionContext context) {
        context.getLogger().info("GC function executed @ " + LocalDateTime.now());
        CosmosDBLayer db = CosmosDBLayer.getInstance();

        List<String> deletedAuctions = db.getAuctionsByUser(null)
                .map(auctionDAO -> db.delAuctionByID(auctionDAO.getAuctionID(), auctionDAO.getOwnerNickname()) ? auctionDAO.getAuctionID() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        context.getLogger().info(String.format("Deleted %d auctions: [ %s ]", deletedAuctions.size(),
                String.join(", ", deletedAuctions)));
    }

    /**
     * This function will be invoked every 3 days to ensure that images that have no entity associated with them are
     * removed from the blob storage
     */
    @FunctionName("GC-DanglingImages")
    public void deleteDanglingImages( // TODO change schedule to "0 0 0 */3 * *" when this is working
            @TimerTrigger(name = "GC-DanglingImagesTrigger", schedule = "0 0 0 * * *") String timerInfo,
            final ExecutionContext context) {

        CosmosDBLayer db = CosmosDBLayer.getInstance();
        BlobStorageLayer bs = BlobStorageLayer.getInstance();

        List<String> imagesFromUsers = db.getAllImagesFromTable("users").collect(Collectors.toList());
        List<String> imagesFromAuctions = db.getAllImagesFromTable("auctions").collect(Collectors.toList());
        Set<String> allImages = new HashSet<>(imagesFromUsers);
        allImages.addAll(imagesFromAuctions);

        List<String> imagesFromBS = bs.listFiles().collect(Collectors.toList());

        for(String imgId : imagesFromBS){
            if(!allImages.contains(imgId))
                bs.deleteBlob(imgId);
        }

        context.getLogger().info("All images that were not being used were removed from blob storage.");
        //throw new NotImplementedException(":(");
    }

}
