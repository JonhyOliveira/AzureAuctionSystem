package scc.data.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class AuctionDAOTest {

    @Test
    void testJson() throws JsonProcessingException {
        AuctionDAO auc = new AuctionDAO("id", "tit", "descri", "epic thumbnail",
                "me", System.currentTimeMillis() + 1000, 100F, false);

        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper.writeValueAsString(auc));
    }

}