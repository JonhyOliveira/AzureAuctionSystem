package scc.functions;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import scc.data.layers.db.DBLayer;
import scc.data.layers.db.MongoDBLayer;
import scc.data.layers.storage.BlobStorageLayer;
import scc.data.layers.db.CosmosDBLayer;
import scc.data.layers.RedisCacheLayer;
import scc.data.layers.storage.File;
import scc.data.layers.storage.FileSystemStorageLayer;
import scc.data.layers.storage.StorageLayer;

/**
 * Azure Functions with Timer trigger for garbage collection.
 * Schedule is: second minute hour ...
 */
public class GarbageCollectionFunctions {

    /**
     * This funtion will be invoked every 10 minutes to ensure that auctions from deleted users are deleted
     */
    @FunctionName("GCRemoveUsers")
    public void deleteAuctionNullUser(
            @TimerTrigger(name = "GCRemoveUsersTrigger", schedule = "1 */10 * * * *") String timerInfo,
            final ExecutionContext context) {
        context.getLogger().info("GC function executed @ " + LocalDateTime.now());
        RedisCacheLayer cacheLayer = RedisCacheLayer.getInstance();
        DBLayer db = MongoDBLayer.getInstance();

        AtomicLong usersDeleted = new AtomicLong();
        AtomicLong questionsUpdated = new AtomicLong();
        AtomicLong bidsUpdated = new AtomicLong();
        AtomicLong auctionsUpdated = new AtomicLong();

        Optional<String> op = cacheLayer.popFromSet("gc:users");
        while (op.isPresent())
        {
            String nickname = op.get();
            db.getAuctionsByUser(nickname).forEach(auctionDAO -> {
                auctionDAO.setOwnerNickname(null);
                db.updateAuction(auctionDAO);
                auctionsUpdated.getAndIncrement();
            });

            db.getBidsByUser(nickname).forEach(bidDAO -> {
                bidDAO.setBidderNickname(null);
                db.updateBid(bidDAO);
                bidsUpdated.getAndIncrement();
            });

            db.getQuestionsAskedByUser(nickname).forEach(questionDAO -> {
                questionDAO.setQuestioner(null);
                db.updateQuestion(questionDAO);
                questionsUpdated.getAndIncrement();
            });

            db.deleteUserByNickname(nickname);
            usersDeleted.getAndIncrement();

            op = cacheLayer.popFromSet("gc:users");
        }

        context.getLogger().info(String.format("Breakdown:\n\t%d users deleted." +
                "\n\t%d auctions updated.\n\t%d bids updated.\n\t%d zquestions updated.", usersDeleted.get(),
                auctionsUpdated.get(), bidsUpdated.get(), questionsUpdated.get()));

    }

    /**
     * This function will be invoked every 3 days to ensure that images that have no entity associated with them are
     * removed from the blob storage
     */
    @FunctionName("GCDanglingImages")
    public void deleteDanglingImages( // TODO change schedule to "0 0 0 */3 * *" when this is working
            @TimerTrigger(name = "GCDanglingImagesTrigger", schedule = "0 */1 * * * *") String timerInfo,
            final ExecutionContext context) {

        DBLayer db = MongoDBLayer.getInstance();
        StorageLayer sL1 = FileSystemStorageLayer.getInstance(); // new BlobStorageLayer(System.getenv("BLOBSTORE_CONNSTRING"));
        StorageLayer sL2 = FileSystemStorageLayer.getInstance(); // new BlobStorageLayer(System.getenv("BLOBSTORE_STRINGREP"));

        List<String> imagesFromUsers = db.getAllImagesFromTable("users").collect(Collectors.toList());
        List<String> imagesFromAuctions = db.getAllImagesFromTable("auctions").collect(Collectors.toList());
        Set<String> allImages = new HashSet<>(imagesFromUsers);
        allImages.addAll(imagesFromAuctions);

        Set<String> imagesFromBS = sL1.listFiles().map(File::getFileID).collect(Collectors.toSet());
        imagesFromBS.addAll(sL2.listFiles().map(File::getFileID).collect(Collectors.toSet()));
        long deleted = 0;

        for(String imgId : imagesFromBS){
            if(!allImages.contains(imgId)) {
                sL1.deleteFile(imgId);
                sL2.deleteFile(imgId);
                deleted++;
            }
        }

        context.getLogger().info(String.format("%d images that were not being used were removed from blob storage.", deleted));
    }

}
