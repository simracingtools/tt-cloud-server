package de.bausdorf.simcacing.tt.stock;

import de.bausdorf.simcacing.tt.iracing.IRacingClient;
import de.bausdorf.simracing.irdataapi.tools.StockDataCache;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StockDataRepository {
    protected final StockDataCache stockDataCache;

    public StockDataRepository(IRacingClient dataClient) {
        this.stockDataCache = dataClient.getDataCache();
    }
}
