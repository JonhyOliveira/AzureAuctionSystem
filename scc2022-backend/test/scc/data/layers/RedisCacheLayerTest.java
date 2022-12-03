package scc.data.layers;

import org.junit.jupiter.api.Test;

class RedisCacheLayerTest {

    @Test
    void a()
    {
        System.out.println(RedisCacheLayer.getInstance().getFromCache("session:Wyatt.Schultz", String.class));
    }

}